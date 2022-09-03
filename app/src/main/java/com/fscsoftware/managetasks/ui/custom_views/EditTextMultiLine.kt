package com.fscsoftware.managetasks.ui.custom_views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText


class EditTextMultiLine : AppCompatEditText  {

    constructor(context : Context ) : super(context)

    constructor(context : Context, attrs : AttributeSet?) : super(context, attrs)

    constructor(context : Context, attrs : AttributeSet?, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    /*
    override fun addTextChangedListener(watcher: TextWatcher?) {
        super.addTextChangedListener(watcher)
    } */

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // ignore the [Enter] key
        if (keyCode==KeyEvent.KEYCODE_ENTER) return true
        return super.onKeyDown(keyCode, event)
    }

}
