package dk.nodes.mlkitscannerlib.text_detection

import android.graphics.*
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.text.FirebaseVisionText
import dk.nodes.mlkitscannerlib.other.GraphicOverlay

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic internal constructor(overlay: GraphicOverlay, private val text: FirebaseVisionText.Element?) :
    GraphicOverlay.Graphic(overlay) {

    private val rectPaint: Paint
    private val textPaint: Paint
    var bounds = Rect()

    init {

        rectPaint = Paint()
        rectPaint.color = TEXT_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH

        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    /** Draws the text block annotations for position, size, and raw value on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        if (text == null) {
            throw IllegalStateException("Attempting to draw a null text.")
        }

        // Draws the bounding box around the TextBlock.
        val rect = RectF(text.boundingBox)
        rect.left = translateX(rect.left)
        rect.top = translateY(rect.top)
        rect.right = translateX(rect.right)
        rect.bottom = translateY(rect.bottom)
        canvas.drawRect(rect, rectPaint)

        // Renders the text at the bottom of the box.
        canvas.drawText(text.text, rect.left, rect.bottom, textPaint)
    }

    companion object {

        private val TEXT_COLOR = Color.WHITE
        private val TEXT_SIZE = 54.0f
        private val STROKE_WIDTH = 4.0f
    }
}