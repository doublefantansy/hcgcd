package hzkj.cc.base.weight

import android.app.Dialog
import android.content.Context
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil

class LoadingDialog(context: Context) : Dialog(context)
{
    var loadingView: LoadingView = LoadingView(context)

    init
    {
        var layout = FrameLayout(context)
        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.base_white))
        layout.setPadding(ViewUtil.dipToPx(context, 17).toInt(), ViewUtil.dipToPx(context, 17).toInt(), ViewUtil.dipToPx(context, 17).toInt(), ViewUtil.dipToPx(context, 17).toInt())
        layout.addView(loadingView)
        setContentView(layout)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    override fun show()
    {
        super.show()
        loadingView.move()
    }

    override fun dismiss()
    {
        super.dismiss()
        loadingView.end()
    }
}