package com.merteroglu286.leitnerbox.presentation.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.merteroglu286.leitnerbox.R

class ButtonComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonComponent)
        val buttonType = typedArray.getInt(R.styleable.ButtonComponent_button_type, 0)
        typedArray.recycle()

        isClickable = true
        isFocusable = true

        when (buttonType) {
            0 -> {
                setBackgroundResource(R.drawable.bg_button_filled)
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }
            1 -> {
                setBackgroundResource(R.drawable.bg_button_outlined)
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
        }

        val padding = resources.getDimensionPixelSize(R.dimen.button_padding)
        setPadding(padding, padding, padding, padding)

        textAlignment = TEXT_ALIGNMENT_CENTER
        textSize = 16f
    }

}