package dk.nodes.mlkitscanner

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import dk.nodes.mlkitscannerlib.activities.CameraActivity
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import android.app.Activity
import dk.nodes.mlkitscannerlib.camera.CameraSourcePreview
import dk.nodes.mlkitscannerlib.other.GraphicOverlay


class MainActivity : AppCompatActivity() {

//    var preview: CameraSourcePreview? = null
//    var graphicOverlay: GraphicOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, CameraActivity::class.java)

//        preview = findViewById<CameraSourcePreview>(R.id.camera_source_preview)
//        graphicOverlay = findViewById<GraphicOverlay>(R.id.graphics_overlay)
//        CameraActivity.setup(intent, null, null, ProcessorType.Text)

        CameraActivity.setup(intent, R.id.camera_source_preview_main, R.id.graphics_overlay_main, R.layout.activity_main, ProcessorType.Text)

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
