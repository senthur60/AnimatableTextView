package com.senthur.animatabletextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class AnimatableTextView : AppCompatTextView {
    private var initialDraw = true
    private var isAnimating = false
    private var lineDim = Rect()


    private var animationPropertiesList: List<TextAnimationProperties> = mutableListOf()

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    override fun onDraw(canvas: Canvas?) {
        if (initialDraw) {
            calculateText()
        }
        if (animationPropertiesList.isNotEmpty()) {
            animationPropertiesList.indices.forEach { index ->
                val textWidth = paint.measureText(text[index].toString())
                val lineNo = layout.getLineForOffset(index)
                val xPos = layout.getPrimaryHorizontal(index)
//                layout.getOffsetForHorizontal(lineNo, xPos)
                val charXPos = xPos + this.paddingLeft
                layout.getLineBounds(lineNo, lineDim)
                val charYPos = (layout.getLineBaseline(lineNo) + paint.baselineShift).toFloat()
                val charPivotX = charXPos + textWidth / 2
                val charPivotY = (lineDim.top + ((lineDim.bottom - lineDim.top) / 2)).toFloat()
                canvas?.save()
                canvas?.translate(
                    animationPropertiesList[index].transX,
                    animationPropertiesList[index].transY
                )
                canvas?.rotate(animationPropertiesList[index].rotate, charPivotX, charPivotY)
                canvas?.scale(
                    animationPropertiesList[index].scaleX,
                    animationPropertiesList[index].scaleY,
                    charPivotX,
                    charPivotY
                )
                val alpha = paint.alpha
                paint.alpha = (animationPropertiesList[index].alpha * 255).toInt()
                canvas?.drawText(text[index].toString(), charXPos, charYPos, paint)
                paint.alpha = alpha
                canvas?.restore()
            }
        } else {
            super.onDraw(canvas)
        }

    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        initialDraw = true
    }

    private fun calculateText() {
        animationPropertiesList = mutableListOf()
        if (!isInLayout && layout != null) {
            if (text!!.isNotEmpty()) {
                animationPropertiesList = List(text.length) {
                    TextAnimationProperties()
                }
            }
            initialDraw = false
        }
    }

    fun animateText(textAnimDetails: TextAnimationProperties):AnimatorSet {
        if (initialDraw) {
            calculateText()
        }
        val animators: MutableList<Animator> = mutableListOf()
        if (animationPropertiesList.isNotEmpty()) {
            val scaleX = textAnimDetails.scaleX - 1f
            val scaleY = textAnimDetails.scaleY - 1f
            val alpha = textAnimDetails.alpha - 1f
            animationPropertiesList.indices.forEach { index ->
                val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                valueAnimator.addUpdateListener {
                    val animatedValue = it.animatedValue as Float
                    animationPropertiesList[index].scaleX = 1f + (scaleX * animatedValue)
                    animationPropertiesList[index].scaleY = 1f + (scaleY * animatedValue)
                    animationPropertiesList[index].transX = textAnimDetails.transX-(textAnimDetails.transX * animatedValue)
                    animationPropertiesList[index].transY = textAnimDetails.transY-(textAnimDetails.transY * animatedValue)
                    animationPropertiesList[index].rotate = (textAnimDetails.rotate * animatedValue)
                    animationPropertiesList[index].alpha = 1f + (alpha * animatedValue)
                    this.invalidate()

                }
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        applyDefaultProperty(animationPropertiesList[index])
                    }
                })
                valueAnimator.duration = (textAnimDetails.duration * 1000L).toLong()
                if (textAnimDetails.animType == TextAnimationProperties.AnimType.PLAY_WITH_DELAY) {
                    valueAnimator.startDelay = (index * (textAnimDetails.duration * 1000L / 10)).toLong()
                }
                animators.add(valueAnimator)
            }
        }
        val animatorSet = AnimatorSet()
        animatorSet.addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
            }

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                animationPropertiesList.indices.forEach { index ->
                    animationPropertiesList[index].alpha = 0f

                }
            }
        })
        if (textAnimDetails.animType == TextAnimationProperties.AnimType.PLAY_SEQUENTIALLY) {
            animatorSet.playSequentially(animators)
        } else {
            animatorSet.playTogether(animators)
        }
        return  animatorSet
    }

    private fun applyDefaultProperty(textAnimDetails: TextAnimationProperties) {
        textAnimDetails.scaleX = 1f
        textAnimDetails.scaleY = 1f
        textAnimDetails.transX = 0f
        textAnimDetails.transY = 0f
        textAnimDetails.rotate = 0f
        textAnimDetails.alpha = 1f
    }
}