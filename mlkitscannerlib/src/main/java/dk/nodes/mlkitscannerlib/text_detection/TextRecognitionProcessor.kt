package dk.nodes.mlkitscannerlib.text_detection

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import dk.nodes.mlkitscannerlib.contracts.Contract
import dk.nodes.mlkitscannerlib.other.FrameMetadata
import dk.nodes.mlkitscannerlib.other.GraphicOverlay

import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Processor for the text recognition demo.
 */
class TextRecognitionProcessor {

    private val detector: FirebaseVisionTextRecognizer

    // Whether we should ignore process(). This is usually caused by feeding input data faster than
    // the model can handle.
    private val shouldThrottle = AtomicBoolean(false)

    init {
        detector = FirebaseVision.getInstance().onDeviceTextRecognizer
    }

    var output: Contract.ProcessorOutput? = null

    //region ----- Exposed Methods -----


    fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: $e")
        }
    }


    @Throws(FirebaseMLException::class)
    fun process(data: ByteBuffer?, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {

        if (shouldThrottle.get()) {
            return
        }
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(frameMetadata.width)
            .setHeight(frameMetadata.height)
            .setRotation(frameMetadata.rotation)
            .build()

        data?.let { byteData ->
            detectInVisionImage(FirebaseVisionImage.fromByteBuffer(byteData, metadata), frameMetadata, graphicOverlay)
        }

    }

    //endregion

    //region ----- Helper Methods -----

    protected fun detectInImage(image: FirebaseVisionImage): Task<FirebaseVisionText> {
        return detector.processImage(image)
    }


    protected fun onSuccess(results: FirebaseVisionText, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {

        graphicOverlay.clear()

        val blocks = results.textBlocks

        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    val textGraphic = TextGraphic(graphicOverlay, elements[k])
                    Log.d("TEXT_FROM_CAMERA", elements[k].text)
                    graphicOverlay.add(textGraphic)
//                    output?.onScannerResult(elements[k].text)
                }
            }
        }
    }

    protected fun onFailure(e: Exception) {
        Log.w(TAG, "Text detection failed.$e")
    }

    private fun detectInVisionImage(
        image: FirebaseVisionImage,
        metadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {

        detectInImage(image)
            .addOnSuccessListener { results ->
                shouldThrottle.set(false)
                this@TextRecognitionProcessor.onSuccess(results, metadata, graphicOverlay)
            }
            .addOnFailureListener(
                object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        shouldThrottle.set(false)
                        this@TextRecognitionProcessor.onFailure(e)
                    }
                })
        // Begin throttling until this frame of input has been processed, either in onSuccess or
        // onFailure.
        shouldThrottle.set(true)
    }

    companion object {

        private val TAG = "TextRecProc"
    }

    //endregion


}