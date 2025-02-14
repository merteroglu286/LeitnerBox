package com.merteroglu286.leitnerbox.presentation.component

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.merteroglu286.leitnerbox.R

class EditTextComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        background = ContextCompat.getDrawable(context, R.drawable.bg_edit_text_rounded)

        setPadding(20, 24, 20, 24)

        textSize = 16f

        setTextColor(ContextCompat.getColor(context, android.R.color.black))

        setHintTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        background = if (focused) {
            ContextCompat.getDrawable(context, R.drawable.bg_edit_text_focused)
        } else {
            ContextCompat.getDrawable(context, R.drawable.bg_edit_text_rounded)
        }
    }
}