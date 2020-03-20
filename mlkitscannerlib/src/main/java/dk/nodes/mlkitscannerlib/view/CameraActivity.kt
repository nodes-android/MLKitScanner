package dk.nodes.mlkitscannerlib.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dk.nodes.mlkitscannerlib.R
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import dk.nodes.mlkitscannerlib.contracts.MLKitScannerLibContract


class CameraActivity: AppCompatActivity(), MLKitScannerLibContract.CameraFragmentOutput {

    private var finished = false

    var type: ProcessorType? = null // Can be Text or Barcode

    private val TAG = CameraActivity::class.java.simpleName.toString().trim { it <= ' ' }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        type = intent.getSerializableExtra("processorType") as ProcessorType

        val fragment = CameraFragment.newInstance(type!!, this)

        supportFragmentManager.beginTransaction()
            .add(R.id.cameraContainer, fragment, "SOMETHING")
            .commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onScannerResult(result: String?) {
        if (!finished) {
            Log.e(TAG, "finishing with result")
            finished = true
            val returnIntent = Intent()
            returnIntent.putExtra("result", result)
            setResult(Activity.RESULT_OK, returnIntent)

            finish()
            Log.e(TAG, "****** AFTER FINISHED *******")
        }
    }

    override fun onScannerError(result: String?) {
        Log.e(TAG, result)
    }

    companion object {
        const val requestCode = 9876

        fun setup(intent: Intent, cameraSourcePreviewId: Int?, graphicOverlayId: Int?, layoutId: Int?, processorType: ProcessorType) {
            intent.putExtra("cameraSourcePreviewId", cameraSourcePreviewId)
            intent.putExtra("graphicOverlayId", graphicOverlayId)
            intent.putExtra("layoutId", layoutId)
            intent.putExtra("processorType", processorType)
        }

    }
}
