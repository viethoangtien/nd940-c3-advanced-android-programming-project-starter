package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var backgroundColor = 0
    private var backgroundColorStartAnimation = 0
    private var textColor = 0
    private var textButton = ""
    private var widthFraction = 0f
    private var sweepAngle = 0f

    private var backgroundAnimator: ValueAnimator? = null
    private var circleAnimator: ValueAnimator? = null

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimensionPixelSize(R.dimen.default_text_size).toFloat()
    }

    private val progressPaint = Paint().apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorAccent)
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            backgroundColorStartAnimation = context.getColor(R.color.colorPrimaryDark)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            textButton = context.getString(R.string.download)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val textX = (width / 2)
        val textY = ((height / 2) - ((paint.descent() + paint.ascent()) / 2)).toInt()

        canvas.drawColor(backgroundColor)
        when (buttonState) {
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
                invalidate()
            }

            ButtonState.Loading -> {
                textButton = context.getString(R.string.button_loading)
                paint.color = backgroundColorStartAnimation
                Log.d(
                    "LoadingButton",
                    "widthFraction: $widthFraction, width: $width, height: $height buttonState: $buttonState"
                )
                canvas.drawRect(0f, 0f, width * widthFraction, height.toFloat(), paint)

                // Draw circle progress
                val startAngle = 0f // Start from the 0-degree position
                val radius =
                    context.resources.getDimensionPixelOffset(R.dimen.circle_radius) // Radius of the circle
                val textWidth = paint.measureText(textButton)
                val centerX = textX + textWidth / 2 + radius
                val centerY = height / 2
                canvas.drawArc(
                    (centerX - radius).toFloat(),
                    (centerY - radius).toFloat(),
                    (centerX + radius).toFloat(),
                    (centerY + radius).toFloat(),
                    startAngle, // Start angle
                    sweepAngle, // Sweep angle
                    true, // Use fill style
                    progressPaint
                )
            }

            ButtonState.Completed -> {
                canvas.drawColor(backgroundColor)
            }
        }

        // Draw text center align
        paint.color = textColor
        canvas.drawText(textButton, textX.toFloat(), textY.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState != ButtonState.Completed) return true
        buttonState = ButtonState.Clicked
        startAnimation()
        return true
    }

    private fun startAnimation() {
        backgroundAnimator?.cancel()
        backgroundAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000 // Duration of the animation
            addUpdateListener { animation ->
                widthFraction = animation.animatedValue as Float
                if (widthFraction == 1f) {
                    buttonState = ButtonState.Completed
                    textButton = context.getString(R.string.download)
                }
                invalidate()
            }
            start()
        }

        circleAnimator?.cancel() // Cancel any ongoing animations
        circleAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 2000 // Animation duration in milliseconds
            addUpdateListener { animation ->
                sweepAngle = animation.animatedValue as Float
                invalidate() // Redraw the view
            }
            start()
        }
    }
}