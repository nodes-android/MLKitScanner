package dk.nodes.mlkitscanner

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import dk.nodes.mlkitscannerlib.activities.CameraActivity
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import android.app.Activity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val scannerType = ProcessorType.Barcode

        scanAgainButton.text = when(scannerType) {
            ProcessorType.Barcode -> "Scan barcode"
            ProcessorType.Text -> "Scan text"
        }

        scanAgainButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
//        CameraActivity.setup(intent, null, null, null, ProcessorType.Barcode) //This will default to CameraActivity's layout.
            CameraActivity.setup(intent, R.id.custom_camera_source_preview, R.id.custom_graphics_overlay, R.layout.custom_camera_layout, scannerType)
            startActivityForResult(intent, CameraActivity.requestCode)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("MainActivity","we're back..")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CameraActivity.requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                resultTV.text = result
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }

    }

}
