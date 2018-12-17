package dk.nodes.mlkitscannerlib.barcode_detection

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import dk.nodes.mlkitscannerlib.other.FrameMetadata
import dk.nodes.mlkitscannerlib.other.GraphicOverlay
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeQRRecognitionProcessor {

    /**
    The following formats are supported:

    Code 128 (FORMAT_CODE_128)
    Code 39 (FORMAT_CODE_39)
    Code 93 (FORMAT_CODE_93)
    Codabar (FORMAT_CODABAR)
    EAN-13 (FORMAT_EAN_13)
    EAN-8 (FORMAT_EAN_8)
    ITF (FORMAT_ITF)
    UPC-A (FORMAT_UPC_A)
    UPC-E (FORMAT_UPC_E)
    QR Code (FORMAT_QR_CODE)
    PDF417 (FORMAT_PDF417)
    Aztec (FORMAT_AZTEC)
    Data Matrix (FORMAT_DATA_MATRIX)
     */

//    val options = FirebaseVisionBarcodeDetectorOptions.Builder()
//        .setBarcodeFormats(
//            FirebaseVisionBarcode.FORMAT_QR_CODE,
//            FirebaseVisionBarcode.FORMAT_AZTEC)
//        .build()

    val detector = FirebaseVision.getInstance().visionBarcodeDetector

    // Whether we should ignore process(). This is usually caused by feeding input data faster than
    // the model can handle.
    private val shouldThrottle = AtomicBoolean(false)

    init {
    }

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

    protected fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> {
        return detector.detectInImage(image)
    }


    protected fun onSuccess(results: List<FirebaseVisionBarcode>, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {

        graphicOverlay.clear()

        Log.d("BARCODE_SUCCESS", "HERE's YOUR BARCODE SIR! $results")
        if (results.isNotEmpty()) {
            results.first().rawValue?.let { barcodeString ->
                Log.d("BARCODE_STRING", barcodeString)
            }
        }

//        for (i in blocks.indices) {
//            val lines = blocks[i].lines
//            for (j in lines.indices) {
//                val elements = lines[j].elements
//                for (k in elements.indices) {
//                    val textGraphic = TextGraphic(graphicOverlay, elements[k])
//                    Log.d("TEXT_FROM_CAMERA", elements[k].text)
//                    graphicOverlay.add(textGraphic)
//
//                }
//            }
//        }
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
            .addOnSuccessListener { result ->
                shouldThrottle.set(false)
                this@BarcodeQRRecognitionProcessor.onSuccess(result, metadata, graphicOverlay)
            }
            .addOnFailureListener(
                object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        shouldThrottle.set(false)
                        this@BarcodeQRRecognitionProcessor.onFailure(e)
                    }
                })
        // Begin throttling until this frame of input has been processed, either in onSuccess or
        // onFailure.
        shouldThrottle.set(true)
    }

    companion object {

        private val TAG = "TextRecProc"
    }
}