package dk.nodes.mlkitscannerlib.view

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import dk.nodes.mlkitscannerlib.R
import dk.nodes.mlkitscannerlib.camera.CameraSource
import dk.nodes.mlkitscannerlib.camera.CameraSourcePreview
import dk.nodes.mlkitscannerlib.camera.ProcessorType
import dk.nodes.mlkitscannerlib.contracts.MLKitScannerLibContract
import dk.nodes.mlkitscannerlib.other.GraphicOverlay
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.IOException

class CameraFragment : Fragment(), MLKitScannerLibContract.ProcessorOutput {

    private val PERMISSION_REQUEST_CODE = 200
    private val PROCESSOR_TYPE = "PROCESSOR_TYPE"
    private val LAYOUT_DECIDER = "LAYOUT_DECIDER"

    var cameraSource: CameraSource? = null
    var preview: CameraSourcePreview? = null
    var graphicOverlay: GraphicOverlay? = null

    var output: MLKitScannerLibContract.CameraFragmentOutput? = null

    var type: ProcessorType? = null // Can be Text or Barcode
//    var useDefaultLayout = true
//    var listener: TYPE? = null

    private val TAG = CameraActivity::class.java.simpleName.toString().trim { it <= ' ' }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getSerializable(PROCESSOR_TYPE) as ProcessorType
//            useDefaultLayout = it.getBoolean(LAYOUT_DECIDER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preview = view?.findViewById<CameraSourcePreview>(R.id.camera_source_preview)
        graphicOverlay = view?.findViewById<GraphicOverlay>(R.id.graphics_overlay)

        //Check for, or ask for camera permissions
        if (checkPermission()) {
            startCamera()
        } else {
            requestPermission()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        startCameraSource()
    }

    override fun onDetach() {
        super.onDetach()
        preview?.let {
            //Releases and stops the cameraSource
            it.release()
        }
    }

    fun startCamera() {
        createCameraSource()
        startCameraSource()
    }

    override fun onScannerResult(result: String?) {

        Log.e(TAG, "*** Got a result in fragment! ***")
//        outputTV.text = result

        output?.let {
            it.onScannerResult(result)
        }
    }

    override fun onScannerError(result: String?) {
        Log.e(TAG, result)

        output?.onScannerError(result)
    }

    private fun checkPermission(): Boolean {
        return when {
            (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) -> false
            else -> true
        }
    }

    private fun requestPermission() {
        activity?.let { parentActivity ->
            ActivityCompat.requestPermissions(
                parentActivity,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity?.applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                startCamera()
            } else {
                Toast.makeText(activity?.applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            context!!,
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
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        }
    }

    fun createCameraSource() {

        if (cameraSource == null) {
            activity?.let { parentActivity ->
                cameraSource = CameraSource(parentActivity, graphicOverlay!!)
            }
            cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
        }

        type?.let { processorType ->
            cameraSource?.setMachineLearningFrameProcessor(processorType)
        }

        when(type) {
            ProcessorType.Barcode -> cameraSource?.barcodeFrameProcessor?.output = this
            ProcessorType.Text -> cameraSource?.textFrameProcessor?.output = this
        }

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
        @JvmStatic
        fun newInstance(processorType: ProcessorType, useDefaultLayout: Boolean) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PROCESSOR_TYPE, processorType)
//                    putBoolean(LAYOUT_DECIDER, useDefaultLayout)
                }
            }
    }
}
