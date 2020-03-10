package hzkj.cc.base.weight.refreshLayout

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hzkj.cc.base.R

class RefreshAndLoadMoreLayout(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    var downY: Float = 0f
    var downX: Float = 0f
    var moveDistance: Int = 0
    var toastDistance: Int = 0
    var moveY: Float = 0f
    var isRefresh: Boolean = false
    var isLoading: Boolean = false
    var footView: FootView
    var headView: HeadView
    var toastView: ToastView
    var recyclerView: RecyclerView
    var refreshListener: RefreshListener? = null
    var loadingListenner: LoadingListenner? = null
    var clickListenner: ClickListenner? = null
    var status: Int = 0
    var firstMeasure: Boolean = true
    var canRefresh: Boolean = true
    var canLoading: Boolean = true
    var isToasting = false
    var showToast = true
    var first = true

    //    var isGrid = false
    companion object {
        var IS_UP = 1
        var IS_DOWN = 11
        var NORMAL = 111
        var FIRST = 1111
    }

    init {
        headView = HeadView(context)
        footView = FootView(context)
        toastView = ToastView(context)
        recyclerView = RecyclerView(context).also {
            //            (it.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.RefreshAndLoadMoreLayout)
        var isGrid = typedArray.getBoolean(R.styleable.RefreshAndLoadMoreLayout_isGrid, false)
        var gridCount = typedArray.getInt(R.styleable.RefreshAndLoadMoreLayout_gridCount, 3)

        if (isGrid) {
            recyclerView.layoutManager = GridLayoutManager(context, gridCount).also {
                //            it.orientation = RecyclerView.VERTICAL
            }
        } else {
            recyclerView.layoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
        }

        headView.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        footView.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        toastView.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(headView)
        addView(recyclerView)
        addView(footView)
        addView(toastView)
        toastView.alpha = 0f
    }

    fun end() {
        headView.loading?.end()
        footView.loading?.end()
    }

    fun refreshComplete() {
        ValueAnimator.ofInt(moveDistance, 0)
            .apply {
                addUpdateListener {
                    moveDistance = animatedValue as Int
                    requestLayout()
                }
                addListener(object : AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        isRefresh = false
                        if (first) {
                            first = false
                        } else {
                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                        Handler().postDelayed({
                            if (showToast) {
                                toastView.count = recyclerView.adapter!!.itemCount
                                if (toastView.count != 0) {
                                    toast(ToastView.HASREFRESH + "${toastView.count}条")
                                } else {
                                    toast(
                                        ToastView.REFRESHEMPTY,
                                        resources.getColor(R.color.base_red)
                                    )
                                }
                            }
                        }, 0)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                duration = 300
                start()
            }
        headView.reset()
    }

    fun toast(text: String, textColor: Int = resources.getColor(R.color.base_blue)) {
        toastView.textView.text = text
        toastView.textView.setTextColor(textColor)
        ValueAnimator.ofFloat(0f, 1f)
            .apply {
                duration = 500
                addUpdateListener {
                    toastView.alpha = it.animatedValue as Float
                }
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        Handler().postDelayed({
                            AnimatorSet().apply {
                                playTogether(ValueAnimator.ofFloat(1f, 0f).also {
                                    it.duration = 500
                                    it.addUpdateListener {
                                        toastView.alpha = it.animatedValue as Float
                                    }
                                    it.addListener(object : Animator.AnimatorListener {
                                        override fun onAnimationRepeat(animation: Animator?) {
                                        }

                                        override fun onAnimationEnd(animation: Animator?) {
//                                            isToasting = false
                                        }

                                        override fun onAnimationCancel(animation: Animator?) {
                                        }

                                        override fun onAnimationStart(animation: Animator?) {
                                        }
                                    })
                                })
                            }
                                .start()
                        }, 500)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        isToasting = false
                    }
                })
            }
            .start()
    }

    fun closeLoad() {
        ValueAnimator.ofInt(moveDistance, 0)
            .apply {
                addUpdateListener {
                    moveDistance = animatedValue as Int
                    requestLayout()
                }
                addListener(object : AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        isLoading = false
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
                duration = 300
                start()
            }
    }

    fun loadComplete(state: String, count: Int) {
        ValueAnimator.ofInt(moveDistance, 0)
            .apply {
                addUpdateListener {
                    moveDistance = animatedValue as Int
                    requestLayout()
                }
                duration = 300
                start()
            }
        isLoading = false
        recyclerView.adapter?.notifyDataSetChanged()
        if (showToast) {
            if (state == ToastView.HASREFRESH || state == ToastView.HASLOAD) {
                toastView.count = count
                toast(state + "${toastView.count}条")
            } else {
                toast(state, resources.getColor(R.color.base_red))
            }
        }
    }

    fun startRefresh() {
        ValueAnimator.ofInt(moveDistance, headView.measuredHeight)
            .apply {
                addUpdateListener {
                    moveDistance = animatedValue as Int
                    requestLayout()
                }
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (!isRefresh) {
                            isRefresh = true
                            headView.changeText(HeadView.LOADING)
                        }
                    }
                })
                duration = 250
                start()
            }
    }

    fun closeRefresh() {
        ValueAnimator.ofInt(headView.measuredHeight, 0)
            .apply {
                addUpdateListener {
                    moveDistance = animatedValue as Int
                    requestLayout()
                }
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        isRefresh = false
                        headView.changeText(HeadView.PULL_TO_REFRESH)
                        headView.reset()

                    }
                })
                duration = 250
                start()
            }
    }

    fun initAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
//        recyclerView.adapter!!.setHasStableIds(true)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        headView.layout(
            0,
            -headView.measuredHeight + moveDistance,
            r,
            moveDistance + headView.measuredHeight
        )
        recyclerView.layout(0, moveDistance, r, b - t + moveDistance)
        footView.layout(0, b - t + moveDistance, r, b + footView.measuredHeight + moveDistance)
        toastView.layout(0, 0, r, toastView.measuredHeight)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        moveY = 0f
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev?.y
                status = FIRST
            }
            MotionEvent.ACTION_MOVE -> {
                moveY = ev?.y
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (moveY == 0f) {
            return super.onInterceptTouchEvent(ev)
        }
        if (!isToasting) {
            recyclerView.adapter?.let {
                if (((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0 && moveY > downY) || ((recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == recyclerView.adapter!!.itemCount - 1 && moveY < downY)) {
                    return true
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (headView.measuredHeight > 0 && firstMeasure) {
            firstMeasure = false
            moveDistance = headView.measuredHeight
        }
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        when (e?.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = e?.y
                downX = e?.x
            }
            MotionEvent.ACTION_MOVE -> {
                moveY = e?.y
                if ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0 && moveY > downY && !isRefresh && !isLoading && canRefresh) {
                    moveDistance = (((moveY - downY) / 4).toInt())
                    if (moveDistance > headView.measuredHeight) {
                        status = IS_DOWN
                        headView.changeText(HeadView.LEAVE_TO_REFRESH)
                    } else {
                        status = NORMAL
                        headView.changeText(HeadView.PULL_TO_REFRESH)
                    }
                    requestLayout()
                }
                if ((recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == recyclerView.adapter!!.itemCount - 1 && canLoading && moveY < downY && !isRefresh && !isLoading) {
                    moveDistance = (-((downY - moveY) / 4).toInt())
                    if (moveDistance < -footView.measuredHeight) {
                        status = IS_UP
                    } else {
                        status = NORMAL
                    }
                    if (status == IS_UP) {
                        footView.changeText(FootView.LEAVE_TO_LOAD)
                    } else if (status == NORMAL) {
                        footView.changeText(FootView.PULL_TO_LOAD)
                    }
                    requestLayout()
                }
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                moveY = 0f
                when (status) {
                    NORMAL -> {
                        ValueAnimator.ofInt(moveDistance, 0)
                            .apply {
                                addUpdateListener {
                                    moveDistance = animatedValue as Int
                                    requestLayout()
                                }
                                duration = 500
                                start()
                            }
                    }
                    IS_UP -> {
                        if (canLoading) {
                            ValueAnimator.ofInt(moveDistance, -footView.measuredHeight)
                                .apply {
                                    addUpdateListener {
                                        moveDistance = animatedValue as Int
                                        requestLayout()
                                    }
                                    addListener(object : Animator.AnimatorListener {
                                        override fun onAnimationRepeat(animation: Animator?) {
                                        }

                                        override fun onAnimationCancel(animation: Animator?) {
                                        }

                                        override fun onAnimationStart(animation: Animator?) {
                                        }

                                        override fun onAnimationEnd(animation: Animator?) {
                                            footView.changeText(FootView.LOADING)
                                            loadingListenner?.loadMore()
                                            isLoading = true
                                        }
                                    })
                                    duration = 200
                                    start()
                                }
                        }
                    }
                    IS_DOWN -> {
                        if (canRefresh) {
                            if (!isRefresh) {
                                ValueAnimator.ofInt(moveDistance, headView.measuredHeight)
                                    .apply {
                                        addUpdateListener {
                                            moveDistance = animatedValue as Int
                                            requestLayout()
                                        }
                                        addListener(object : Animator.AnimatorListener {
                                            override fun onAnimationRepeat(animation: Animator?) {
                                            }

                                            override fun onAnimationCancel(animation: Animator?) {
                                            }

                                            override fun onAnimationStart(animation: Animator?) {
                                            }

                                            override fun onAnimationEnd(animation: Animator?) {
                                                isRefresh = true
                                                headView.changeText(HeadView.LOADING)
                                                refreshListener?.refresh()
                                            }
                                        })
                                        duration = 200
                                        start()
                                    }
                            }
                        }
                    }
                }
            }
//            }
        }
        return super.onTouchEvent(e)
    }
}
