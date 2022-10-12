package com.mtis.gowith.view.customview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class NotoTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        setType(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setType(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setType(context)
    }

    private fun setType(context: Context) {
        //asset에 폰트 복사
        //NotoSnat 경령화된 폰트 위치: https://github.com/theeluwin/NotoSansKR-Hestia
        this.typeface = Typeface.createFromAsset(context.assets, "font/noto_sans_kr_regular.otf")
    }
}