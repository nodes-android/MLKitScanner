package dk.nodes.mlkitscanner

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import dk.nodes.mlkitscannerlib.activities.CameraActivity
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import android.app.Activity



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, CameraActivity::class.java)
        CameraActivity.setup(intent, R.id.camera_source_preview, R.id.graphics_overlay, ProcessorType.Text)
        startActivityForResult(intent, CameraActivity.requestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CameraActivity.requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("result")
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
