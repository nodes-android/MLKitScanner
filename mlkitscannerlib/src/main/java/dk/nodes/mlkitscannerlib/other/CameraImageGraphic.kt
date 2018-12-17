package dk.nodes.mlkitscannerlib.other

import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import dk.nodes.mlkitscannerlib.other.GraphicOverlay.Graphic


/** Draw camera image to background.  */
class CameraImageGraphic(overlay: GraphicOverlay, private val bitmap: Bitmap) : Graphic(overlay) {

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null)
    }
}