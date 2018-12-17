package dk.nodes.mlkitscannerlib.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.google.android.gms.common.images.Size
import dk.nodes.mlkitscannerlib.other.GraphicOverlay

import java.io.IOException

/** Preview the camera image in the screen.  */
class CameraSourcePreview(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    private val surfaceView: SurfaceView
    private var startRequested: Boolean = false
    private var surfaceAvailable: Boolean = false
    private var cameraSource: CameraSource? = null

    private var overlay: GraphicOverlay? = null

    private val isPortraitMode: Boolean
        get() {
            val orientation = context.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return true
            }

            Log.d(TAG, "isPortraitMode returning false by default")
            return false
        }

    init {
        startRequested = false
        surfaceAvailable = false

        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource?) {
        if (cameraSource == null) {
            stop()
        }

        this.cameraSource = cameraSource

        if (this.cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource, overlay: GraphicOverlay) {
        this.overlay = overlay
        start(cameraSource)
    }

    fun stop() {
        cameraSource?.let {
            it.stop()
        }
    }

    fun release() {
        cameraSource?.let {
            it.release()
            cameraSource = null
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            cameraSource?.let { cameraSource ->
                cameraSource.start(surfaceView.holder)
                overlay?.let { overlay ->
                    if (overlay != null) {
                        cameraSource.previewSize?.let { size ->
                            val min = Math.min(size.width, size.height)
                            val max = Math.max(size.width, size.height)
                            if (isPortraitMode) {
                                // Swap width and height sizes when in portrait, since it will be rotated by
                                // 90 degrees
                                overlay.setCameraInfo(min, max, cameraSource.cameraFacing)
                            } else {
                                overlay.setCameraInfo(max, min, cameraSource.cameraFacing)
                            }
                            overlay.clear()
                        }
                    }
                    startRequested = false
                }

            }
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                Log.e(TAG, "Could not start camera source.", e)
            }

        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var width = 320
        var height = 240
        cameraSource?.let {
            val size = it.previewSize
            if (size != null) {
                width = size.width
                height = size.height
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode) {
            val tmp = width
            width = height
            height = tmp
        }

        val layoutWidth = right - left
        val layoutHeight = bottom - top

        // Computes height and width for potentially doing fit width.
        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = layoutWidth.toFloat() / width.toFloat()
        val heightRatio = layoutHeight.toFloat() / height.toFloat()

        // To fill the view with the camera preview, while also preserving the correct aspect ratio,
        // it is usually necessary to slightly oversize the child and to crop off portions along one
        // of the dimensions.  We scale up based on the dimension requiring the most correction, and
        // compute a crop offset for the other dimension.
        if (widthRatio > heightRatio) {
            childWidth = layoutWidth
            childHeight = (height.toFloat() * widthRatio).toInt()
            childYOffset = (childHeight - layoutHeight) / 2
        } else {
            childWidth = (width.toFloat() * heightRatio).toInt()
            childHeight = layoutHeight
            childXOffset = (childWidth - layoutWidth) / 2
        }

        for (i in 0 until childCount) {
            // One dimension will be cropped.  We shift child over or up by this offset and adjust
            // the size to maintain the proper aspect ratio.
            getChildAt(i).layout(
                -1 * childXOffset, -1 * childYOffset,
                childWidth - childXOffset, childHeight - childYOffset
            )
        }

        try {
            startIfReady()
        } catch (e: IOException) {
            Log.e(TAG, "Could not start camera source.", e)
        }

    }

    companion object {
        private val TAG = "MIDemoApp:Preview"
    }
}