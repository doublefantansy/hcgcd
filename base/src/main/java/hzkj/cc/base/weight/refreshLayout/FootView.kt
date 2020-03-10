package hzkj.cc.base.weight.refreshLayout

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import hzkj.cc.base.R
import hzkj.cc.base.weight.LoadingView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FootView : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var view: View
    lateinit var image: ImageView
    lateinit var loading: LoadingView
    var isDown: Boolean = false
    var isRun: Boolean = false

    companion object {
        var LOADING = 1
        var PULL_TO_LOAD = 10
        var LEAVE_TO_LOAD = 1110

    }

    init {
        MainScope().launch {
            initLayout()
            addView(view)
            image = view.findViewById(R.id.image)
            image.rotation = 180f
            loading = view.findViewById(R.id.load)
        }
    }

    fun createArrowAnimator() {
        ValueAnimator.ofFloat(
            if (!isDown) 0f else 180f, if (!isDown) 180f else 0f
        ).apply {
            addUpdateListener {
                System.out.println(image?.rotation!!)
                image?.rotation = animatedValue as Float
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    isRun = true

                }

                override fun onAnimationEnd(animation: Animator?) {
                    isRun = false
                    isDown = !isDown

                }


            })
            duration = 300
            start()
        }
    }

    fun changeText(status: Int) {
        when (status) {
            LOADING -> {
                image?.visibility = View.GONE
                loading?.visibility = View.VISIBLE
                loading?.move()
            }
            PULL_TO_LOAD -> {
                image?.visibility = View.VISIBLE
                loading?.visibility = View.GONE
                if (!isDown && !isRun) {
                    createArrowAnimator()
                }
            }
            LEAVE_TO_LOAD -> {
                if (isDown && !isRun) {
                    createArrowAnimator()
                }
            }
        }
    }

    suspend fun initLayout() {
        withContext(Dispatchers.IO)
        {
            view = LayoutInflater.from(context).inflate(R.layout.base_foot_view, this@FootView, false)
        }
    }
}

