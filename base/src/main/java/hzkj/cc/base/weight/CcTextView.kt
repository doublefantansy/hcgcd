package hzkj.cc.base.weight

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class CcTextView(context: Context, attributeSet: AttributeSet? = null) : TextView(context, attributeSet)
{
    init
    {
        paint.isFakeBoldText = true
    }
}