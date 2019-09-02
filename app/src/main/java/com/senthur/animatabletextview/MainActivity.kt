package com.senthur.animatabletextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        animatableView.setText(getString(R.string.animateTest), TextView.BufferType.NORMAL)
        animatableView.setTextSize(40f)
        animatableView.post {
            val animatorSet =  animatableView.animateText(TextAnimationProperties(transX = animatableView.width.toFloat()))
            val listener = object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    val animatorSet =
                        animatableView.animateText(TextAnimationProperties(transX = animatableView.width.toFloat()))
                    animatorSet.addListener(this)
                    animatorSet.start()

                }
            }
            animatorSet.addListener(listener)
            animatorSet.start()
        }
    }
}
