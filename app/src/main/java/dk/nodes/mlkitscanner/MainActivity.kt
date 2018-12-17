package dk.nodes.mlkitscanner

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dk.nodes.mlkitscannerlib.barcode_detection.BarcodeQRRecognitionProcessor
import dk.nodes.mlkitscannerlib.camera.CameraSource
import dk.nodes.mlkitscannerlib.camera.CameraSourcePreview
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import dk.nodes.mlkitscannerlib.other.GraphicOverlay
import dk.nodes.mlkitscannerlib.text_detection.TextRecognitionProcessor
import java.io.IOException

class MainActivity : AppCompatActivity() {

    var cameraSource: CameraSource? = null
    var preview: CameraSourcePreview? = null
    var graphicOverlay: GraphicOverlay? = null

    private val TAG = MainActivity::class.java.simpleName.toString().trim { it <= ' ' }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preview = findViewById<CameraSourcePreview>(R.id.camera_source_preview)
        graphicOverlay = findViewById<GraphicOverlay>(R.id.graphics_overlay)

        createCameraSource()
        startCameraSource()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource?.release()
        }
    }

    fun createCameraSource() {

        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay!!)
            cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
        }

        val type = ProcessorType.Text // Can be Text or Barcode
        cameraSource?.setMachineLearningFrameProcessor(type)
    }

    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }

                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
            }

        }
    }
}
