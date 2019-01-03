package dk.nodes.mlkitscannerlib.view

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dk.nodes.mlkitscannerlib.R
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import dk.nodes.mlkitscannerlib.contracts.MLKitScannerLibContract

class CameraActivity: AppCompatActivity(), MLKitScannerLibContract.CameraFragmentOutput {

    var finished = false

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

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
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
        val requestCode = 9876

        fun setup(intent: Intent, cameraSourcePreviewId: Int?, graphicOverlayId: Int?, layoutId: Int?, processorType: ProcessorType) {
            intent.putExtra("cameraSourcePreviewId", cameraSourcePreviewId)
            intent.putExtra("graphicOverlayId", graphicOverlayId)
            intent.putExtra("layoutId", layoutId)
            intent.putExtra("processorType", processorType)
        }

    }
}
