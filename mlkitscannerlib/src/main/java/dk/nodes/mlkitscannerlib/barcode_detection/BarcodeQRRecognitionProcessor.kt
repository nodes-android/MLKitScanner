package dk.nodes.mlkitscannerlib.barcode_detection

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.common.FirebaseMLException
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import dk.nodes.mlkitscannerlib.contracts.MLKitScannerLibContract
import dk.nodes.mlkitscannerlib.other.FrameMetadata
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
//            FirebaseVisionBarcode.FORMAT_EAN_13,
//            FirebaseVisionBarcode.FORMAT_EAN_8)
//        .build()

    val detector = FirebaseVision.getInstance().visionBarcodeDetector
//    val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

    var output: MLKitScannerLibContract.ProcessorOutput? = null

    var result = ""

    // Whether we should ignore process(). This is usually caused by feeding input data faster than
    // the model can handle.
    private val shouldThrottle = AtomicBoolean(false)

    //region ----- Exposed Methods -----

    fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Detector: $e")
        }
    }

    @Throws(FirebaseMLException::class)
    fun process(data: ByteBuffer?, frameMetadata: FrameMetadata) {

        //Exit processing if throttling
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
            detectInVisionImage(FirebaseVisionImage.fromByteBuffer(byteData, metadata))
        }

    }

    //endregion

    //region ----- Helper Methods -----

    protected fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> {
        return detector.detectInImage(image)
    }


    protected fun onSuccess(results: List<FirebaseVisionBarcode>) {

        if (results.isNotEmpty()) {
            results.first().rawValue?.let { barcodeString ->
                if (barcodeString != result) {
                    Log.e(TAG, "onScannerResult - Got a barcode! -> $barcodeString")
                    result = barcodeString
                    output?.onScannerResult(result)
                }

            }
        }
    }

    protected fun onFailure(e: Exception) {
        Log.w(TAG, "Barcode detection failed.$e")
        output?.onScannerError(e.message)
    }

    private fun detectInVisionImage(image: FirebaseVisionImage) {

        // Begin throttling until this frame of input has been processed,
        // either in onSuccess or onFailure.
        shouldThrottle.set(true)

        detectInImage(image)
            .addOnSuccessListener { result ->
                this@BarcodeQRRecognitionProcessor.onSuccess(result)
                shouldThrottle.set(false)
            }
            .addOnFailureListener(
                object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        this@BarcodeQRRecognitionProcessor.onFailure(e)
                        shouldThrottle.set(false)
                    }
                })

    }

    companion object {
        private val TAG = "BarcodeQRProcessor"
    }
}