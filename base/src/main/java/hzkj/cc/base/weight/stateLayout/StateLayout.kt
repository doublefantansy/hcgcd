package hzkj.cc.base.weight.stateLayout

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil

class StateLayout(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet)
{
    enum class STATE(var tag: Int, var msg: String)
    {
        LOADING(0, "数据加载中……"), EMPTY(1, "暂无数据"), CONTENT(2, ""), NETERROR(3, "网络异常")
    }

    var content: View? = null
    var textView: TextView
    var imageView: ImageView
    var status: STATE = STATE.LOADING
    lateinit var valueAnimator: ValueAnimator
    var retryListenner: RetryListenner? = null

    init
    {
        imageView = ImageView(context).also {
            it.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setImageDrawable(resources.getDrawable(R.drawable.statelayout_loading))
            it.visibility = View.INVISIBLE
        }
        textView = TextView(context).also {
            it.visibility = View.INVISIBLE

            it.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            it.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
            it.setOnClickListener {
                retryListenner?.retry()
                changeStatus(STATE.LOADING)
            }
            it.textSize = 16f
            it.text = STATE.LOADING.msg
        }
        addView(imageView)
        addView(textView)
        valueAnimator = ValueAnimator.ofFloat(0f, 360f)
            .also {
                it.duration = 1000
                it.interpolator = (LinearInterpolator())
                it.repeatCount = ValueAnimator.INFINITE
                it.addListener(object : Animator.AnimatorListener
                               {
                                   override fun onAnimationRepeat(animation: Animator?)
                                   {
                                   }

                                   override fun onAnimationEnd(animation: Animator?)
                                   {
                                   }

                                   override fun onAnimationCancel(animation: Animator?)
                                   {
                                   }

                                   override fun onAnimationStart(animation: Animator?)
                                   {
                                       Handler().postDelayed({
                                                                 imageView.visibility = View.VISIBLE
                                                                 textView.visibility = View.VISIBLE
                                                             }, 100)
                                   }
                               })
                it.addUpdateListener {
                    imageView.rotation = it.animatedValue as Float
                }
            }
        valueAnimator.start()
        //    for (index in 0..childCount) {
        //            System.out.println(getChildAt(index))
        //    }
    }

    fun changeStatus(status: STATE)
    {
        this.status = status
        when (this.status)
        {
            STATE.EMPTY ->
            {
                valueAnimator.end()
                imageView.setImageDrawable(resources.getDrawable(R.drawable.statelayout_empty))
                imageView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                content?.visibility = View.GONE
            }
            STATE.NETERROR ->
            {
                valueAnimator.end()
                imageView.setImageDrawable(resources.getDrawable(R.drawable.statelayout_net_error))
                imageView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                content?.visibility = View.GONE
            }
            STATE.LOADING ->
            {
                valueAnimator.start()
                imageView.setImageDrawable(resources.getDrawable(R.drawable.statelayout_loading))
                imageView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                content?.visibility = View.GONE
            }
            STATE.CONTENT ->
            {
                content?.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                textView.visibility = View.GONE
                if (valueAnimator.isRunning)
                {
                    valueAnimator.end()
                }
            }
        }
        textView.text = this.status.msg
        requestLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        if (getChildAt(2) == null)
        {
            throw Exception("必须有一个子view")
        }
        content = getChildAt(2)
        when (status)
        {
            STATE.CONTENT ->
            {
                content?.layout(0, 0, width, height)
            }
            else ->
            {
                imageView.layout(width / 2 - imageView.measuredWidth / 2, height / 2 - imageView.measuredHeight / 2, width / 2 + imageView.measuredWidth / 2, height / 2 + imageView.measuredHeight / 2)
                textView.layout(width / 2 - textView.measuredWidth / 2, (height / 2 + imageView.measuredHeight / 2 + ViewUtil.dipToPx(context, 10)).toInt(), width / 2 + textView.measuredWidth / 2, (height / 2 + imageView.measuredHeight / 2 + ViewUtil.dipToPx(context, 10) + textView.measuredHeight).toInt())
            }
        }
    }
}