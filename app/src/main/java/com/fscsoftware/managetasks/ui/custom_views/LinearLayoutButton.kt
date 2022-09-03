package com.fscsoftware.managetasks.ui.custom_views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.fscsoftware.managetasks.R

class LinearLayoutButton : LinearLayout {

    private val ELEVATION = 20F;

    init {
        this.elevation = ELEVATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.outlineSpotShadowColor = this.context.getColor(R.color.shadow)
            this.outlineAmbientShadowColor = this.context.getColor(R.color.shadow)
        }

    }

    constructor(context : Context) : super(context)

    constructor(context : Context, attrs : AttributeSet?) : super(context, attrs)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    fun setCallbackOnTouch (callback : () -> Unit) {

        setOnTouchListener { view, motionEvent ->

            view.performClick()

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.elevation = 0F
                    true
                }
                MotionEvent.ACTION_UP -> {
                    view.elevation = ELEVATION
                    callback()
                    true
                }
                else -> false
            }
        }

    }

}