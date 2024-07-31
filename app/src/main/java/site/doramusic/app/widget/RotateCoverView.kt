package site.doramusic.app.widget

import kotlin.jvm.JvmOverloads
import android.animation.ObjectAnimator
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.animation.LinearInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat

class RotateCoverView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(
    context!!, attrs, defStyleAttr
) {
    private var mShadowRadius = 0
    private var paint = Paint()
    private var middleRect = RectF()
    private var innerRect = RectF()
    private var albumPathRect = RectF()
    private var albumTextPath = Path()
    private var density = 0f

    // Animation
    private var rotateAnimator: ObjectAnimator? = null
    private var lastAnimationValue: Long = 0

    init {
        init()
    }

    private fun init() {
        density = context.resources.displayMetrics.density
        val shadowXOffset = (density * X_OFFSET).toInt()
        val shadowYOffset = (density * Y_OFFSET).toInt()
        mShadowRadius = (density * SHADOW_RADIUS).toInt()
        val circle: ShapeDrawable
        if (elevationSupported()) {
            circle = ShapeDrawable(OvalShape())
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density)
        } else {
            val oval: OvalShape = OvalShadow(mShadowRadius)
            circle = ShapeDrawable(oval)
            ViewCompat.setLayerType(this, LAYER_TYPE_SOFTWARE, circle.paint)
            circle.paint.setShadowLayer(
                mShadowRadius.toFloat(),
                shadowXOffset.toFloat(),
                shadowYOffset.toFloat(),
                KEY_SHADOW_COLOR
            )
            val padding = mShadowRadius
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding)
        }
        circle.paint.isAntiAlias = true
        circle.paint.color = DEFAULT_ALBUM_COLOR
        background = circle
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        paint.style = Paint.Style.FILL
        paint.color = DEFAULT_ALBUM_COLOR
        paint.textSize = ALBUM_CIRCLE_TEXT_SIZE * density
        rotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        rotateAnimator!!.setDuration(10000)
        rotateAnimator!!.setInterpolator(LinearInterpolator())
        rotateAnimator!!.setRepeatMode(ValueAnimator.RESTART)
        rotateAnimator!!.setRepeatCount(ValueAnimator.INFINITE)
    }

    private fun elevationSupported(): Boolean {
        return Build.VERSION.SDK_INT >= 21
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!elevationSupported()) {
            setMeasuredDimension(
                measuredWidth + mShadowRadius * 2,
                measuredHeight + mShadowRadius * 2
            )
        }
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = MIDDLE_RECT_COLOR
        canvas.drawOval(middleRect, paint)
        paint.color = INNER_RECT_COLOR
        canvas.drawOval(innerRect, paint)
        paint.textSize = ALBUM_CIRCLE_TEXT_SIZE * density
        paint.color = ALBUM_CIRCLE_TEXT_COLOR
        canvas.drawTextOnPath(ALBUM_TEXT, albumTextPath, 2 * density, 2 * density, paint)
        paint.textSize = ALBUM_CIRCLE_TEXT_SIZE_SMALL * density
        canvas.drawText(APP_NAME, (width / 2).toFloat(), (height / 2).toFloat(), paint)
        canvas.drawText(APP_SLOGAN, (width / 2).toFloat(), height / 2 + 4 * density, paint)
        canvas.drawText(COPY_RIGHT, (width / 2).toFloat(), height / 2 + 12 * density, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val middleRectSize = density * MIDDLE_RECT_SIZE
        val innerRectSize = density * INNER_RECT_SIZE
        val albumRectSize = density * ALBUM_TEXT_PATH_RECT_SIZE
        middleRect[0f, 0f, middleRectSize] = middleRectSize
        innerRect[0f, 0f, innerRectSize] = innerRectSize
        albumPathRect[0f, 0f, albumRectSize] = albumRectSize
        middleRect.offset(w / 2 - middleRectSize / 2, h / 2 - middleRectSize / 2)
        innerRect.offset(w / 2 - innerRectSize / 2, h / 2 - innerRectSize / 2)
        albumPathRect.offset(w / 2 - albumRectSize / 2, h / 2 - albumRectSize / 2)
        albumTextPath.addOval(albumPathRect, Path.Direction.CW)
    }

    // Animation
    fun startRotateAnimation() {
        rotateAnimator!!.cancel()
        rotateAnimator!!.start()
    }

    fun cancelRotateAnimation() {
        lastAnimationValue = 0
        rotateAnimator!!.cancel()
    }

    fun pauseRotateAnimation() {
        lastAnimationValue = rotateAnimator!!.currentPlayTime
        rotateAnimator!!.cancel()
    }

    fun resumeRotateAnimation() {
        rotateAnimator!!.start()
        rotateAnimator!!.currentPlayTime = lastAnimationValue
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (rotateAnimator != null) {
            rotateAnimator!!.cancel()
            rotateAnimator = null
        }
    }

    /**
     * Draw oval shadow below ImageView under lollipop.
     */
    private inner class OvalShadow internal constructor(shadowRadius: Int) : OvalShape() {
        private var mRadialGradient: RadialGradient? = null
        private val mShadowPaint: Paint

        init {
            mShadowPaint = Paint()
            mShadowRadius = shadowRadius
            updateRadialGradient(rect().width().toInt())
        }

        override fun onResize(width: Float, height: Float) {
            super.onResize(width, height)
            updateRadialGradient(width.toInt())
        }

        override fun draw(canvas: Canvas, paint: Paint) {
            val viewWidth = this@RotateCoverView.width
            val viewHeight = this@RotateCoverView.height
            canvas.drawCircle(
                (viewWidth / 2).toFloat(),
                (viewHeight / 2).toFloat(),
                (viewWidth / 2).toFloat(),
                mShadowPaint
            )
            canvas.drawCircle(
                (viewWidth / 2).toFloat(),
                (viewHeight / 2).toFloat(),
                (viewWidth / 2 - mShadowRadius).toFloat(),
                paint
            )
        }

        private fun updateRadialGradient(diameter: Int) {
            mRadialGradient = RadialGradient(
                diameter / 2f, diameter / 2f,
                mShadowRadius.toFloat(), intArrayOf(FILL_SHADOW_COLOR, Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
            mShadowPaint.shader = mRadialGradient
        }
    }

    companion object {
        // private static final String TAG = "AlbumImageView";
        private const val KEY_SHADOW_COLOR = 0x1E000000
        private const val FILL_SHADOW_COLOR = 0x3D000000
        private const val X_OFFSET = 0f
        private const val Y_OFFSET = 1.75f
        private const val SHADOW_RADIUS = 24f
        private const val SHADOW_ELEVATION = 16
        private const val DEFAULT_ALBUM_COLOR = -0xc3a088
        private const val MIDDLE_RECT_COLOR = -0xb38e74
        private const val INNER_RECT_COLOR = 0x4FD8D8D8
        private const val ALBUM_CIRCLE_TEXT_COLOR = -0x634234
        private const val ALBUM_CIRCLE_TEXT_SIZE = 4.5f
        private const val ALBUM_CIRCLE_TEXT_SIZE_SMALL = 4f
        private const val MIDDLE_RECT_SIZE = 80
        private const val INNER_RECT_SIZE = 64
        private const val ALBUM_TEXT_PATH_RECT_SIZE = 56
        private const val ALBUM_TEXT = "仅用于学习交流，禁止用于包括但不仅限于商业用途，本产品由https://dorachat.com赞助"
        private const val APP_NAME = "Dora Music"
        private const val APP_SLOGAN = "版权所有，侵权必究"
        private const val COPY_RIGHT = "doramusic ©2023"
    }
}