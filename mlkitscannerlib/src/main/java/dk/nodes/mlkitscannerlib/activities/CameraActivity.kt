package dk.nodes.mlkitscannerlib.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dk.nodes.mlkitscannerlib.R
import dk.nodes.mlkitscannerlib.camera.CameraSource
import dk.nodes.mlkitscannerlib.camera.CameraSourcePreview
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import dk.nodes.mlkitscannerlib.other.GraphicOverlay
import java.io.IOException

class CameraActivity : AppCompatActivity() {

    var cameraSource: CameraSource? = null
    var preview: CameraSourcePreview? = null
    var graphicOverlay: GraphicOverlay? = null

    var type = ProcessorType.Text // Can be Text or Barcode

    private val TAG = CameraActivity::class.java.simpleName.toString().trim { it <= ' ' }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_camera)

        //TODO: Ask permissions for camera here


        val intent = intent
        val cameraSourcePreviewId = intent.getIntExtra("cameraSourcePreviewId", 0)
        val graphicOverlayId = intent.getIntExtra("graphicOverlayId", 0)
        type = intent.getSerializableExtra("processorType") as ProcessorType

        preview = findViewById<CameraSourcePreview>(cameraSourcePreviewId)
        graphicOverlay = findViewById<GraphicOverlay>(graphicOverlayId)

        createCameraSource()
        startCameraSource()

//        val returnIntent = Intent()
//        returnIntent.putExtra("result", result)
//        setResult(Activity.RESULT_OK, returnIntent)
//        finish()
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

    companion object {
        val requestCode = 9876

        fun setup(intent: Intent, cameraSourcePreviewId: Int, graphicOverlayId: Int, processorType: ProcessorType) {
            intent.putExtra("cameraSourcePreviewId", cameraSourcePreviewId)
            intent.putExtra("graphicOverlayId", graphicOverlayId)
            intent.putExtra("processorType", processorType)
        }
    }
}
