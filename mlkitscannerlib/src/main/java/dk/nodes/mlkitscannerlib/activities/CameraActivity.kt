package dk.nodes.mlkitscannerlib.activities

import android.Manifest
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
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v4.app.ActivityCompat
import android.os.Build
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.Toast

class CameraActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 200

    var cameraSource: CameraSource? = null
    var preview: CameraSourcePreview? = null
    var graphicOverlay: GraphicOverlay? = null

    var type = ProcessorType.Text // Can be Text or Barcode

    private val TAG = CameraActivity::class.java.simpleName.toString().trim { it <= ' ' }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cameraSourcePreviewId = intent.getIntExtra("cameraSourcePreviewId", 0)
        val graphicOverlayId = intent.getIntExtra("graphicOverlayId", 0)
        val layoutId = intent.getIntExtra("layoutId", 0)
        type = intent.getSerializableExtra("processorType") as ProcessorType

        if(layoutId != 0) {
            setContentView(layoutId)
            preview = findViewById<CameraSourcePreview>(cameraSourcePreviewId)
            graphicOverlay = findViewById<GraphicOverlay>(graphicOverlayId)
        } else {
            setContentView(R.layout.activity_camera)
            preview = findViewById<CameraSourcePreview>(R.id.camera_source_preview)
            graphicOverlay = findViewById<GraphicOverlay>(R.id.graphics_overlay)
        }



        //Check for, or ask for camera permissions
        if (checkPermission()) {
            startCamera()
        } else {
            requestPermission()
        }

//        val returnIntent = Intent()
//        returnIntent.putExtra("result", result)
//        setResult(Activity.RESULT_OK, returnIntent)
//        finish()
    }

    fun startCamera() {
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

    private fun checkPermission(): Boolean {
        return when {
            (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) -> false
            else -> true
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                startCamera()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel("You need to allow access permissions",
                            DialogInterface.OnClickListener { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermission()
                                }
                            })
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@CameraActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
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

        fun setup(intent: Intent, cameraSourcePreviewId: Int?, graphicOverlayId: Int?, layoutId: Int?, processorType: ProcessorType) {
            intent.putExtra("cameraSourcePreviewId", cameraSourcePreviewId)
            intent.putExtra("graphicOverlayId", graphicOverlayId)
            intent.putExtra("layoutId", layoutId)
            intent.putExtra("processorType", processorType)
        }
    }
}
