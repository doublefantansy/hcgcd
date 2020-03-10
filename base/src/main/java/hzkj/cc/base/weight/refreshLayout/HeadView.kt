package hzkj.cc.base.weight.refreshLayout

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import hzkj.cc.base.R
import hzkj.cc.base.weight.LoadingView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HeadView(context:Context?, attrs:AttributeSet? = null, defStyleAttr:Int = 0) : LinearLayout(context, attrs, defStyleAttr)
{
		var text:TextView? = null
		var image:ImageView? = null
		var loading:LoadingView? = null
		// 此时箭头朝向
		var isDown:Boolean = true
		var isRun:Boolean = false
		lateinit var a:ValueAnimator

		private lateinit var view:View

		companion object
		{
				var LOADING = 1
				var PULL_TO_REFRESH = 10
				var LEAVE_TO_REFRESH = 101
				var REFRESH_SUCCESS = 1011
				var REFRESH_FAIL = 10111
		}

		init
		{
				MainScope().launch {
						initLayout()
						text = view.findViewById(R.id.text)
						image = view.findViewById(R.id.image)
						loading = view.findViewById(R.id.load)
						addView(view)
						loading?.move()
				}
				a = ValueAnimator.ofFloat(0f, 360f)
						.apply {
								addUpdateListener {
										image?.rotation = animatedValue as Float
								}
								repeatCount = ValueAnimator.INFINITE
								interpolator = LinearInterpolator()
								duration = 3000
						}
		}

		suspend fun initLayout()
		{
				withContext(Dispatchers.IO) {
						view = LayoutInflater.from(context)
								.inflate(R.layout.base_head_view, this@HeadView, false)
				}
		}

		fun createArrowAnimator()
		{
				ValueAnimator.ofFloat(if(isDown) 0f else 180f, if(isDown) 180f else 0f)
						.apply {
								addUpdateListener {
										System.out.println(image?.rotation!!)
										image?.rotation = animatedValue as Float
								}
								addListener(object : Animator.AnimatorListener
								{
										override fun onAnimationRepeat(animation:Animator?)
										{
										}

										override fun onAnimationCancel(animation:Animator?)
										{
										}

										override fun onAnimationStart(animation:Animator?)
										{
												isRun = true
										}

										override fun onAnimationEnd(animation:Animator?)
										{
												isRun = false
												isDown = !isDown
										}
								})
								duration = 300
								start()
						}
		}

		fun reset()
		{
//        image?.visibility = View.VISIBLE
//        loading?.visibility = View.GONE
				image?.setImageDrawable(resources.getDrawable(R.drawable.refresh_down))
				a.end()
		}

		fun changeText(status:Int)
		{
				when(status)
				{
						LOADING ->
						{
								image?.visibility = View.GONE
								loading?.visibility = View.VISIBLE
								loading?.move()
								isDown = true
						}
						PULL_TO_REFRESH ->
						{
								image?.visibility = View.VISIBLE
								loading?.visibility = View.GONE
								if(!isDown && !isRun)
								{
										ValueAnimator.ofFloat(180f, 0f)
												.apply {
														addUpdateListener {
																image?.rotation = animatedValue as Float
														}
														addListener(object : Animator.AnimatorListener
														{
																override fun onAnimationRepeat(animation:Animator?)
																{
																}

																override fun onAnimationCancel(animation:Animator?)
																{
																}

																override fun onAnimationStart(animation:Animator?)
																{
																		isRun = true
																}

																override fun onAnimationEnd(animation:Animator?)
																{
																		isDown = true
																		isRun = false
																}
														})
														duration = 300
														start()
												}
								}
						}
						LEAVE_TO_REFRESH ->
						{
								image?.visibility = View.VISIBLE
								loading?.visibility = View.GONE
								if(isDown && !isRun)
								{
										ValueAnimator.ofFloat(0f, 180f)
												.apply {
														addUpdateListener {
																image?.rotation = animatedValue as Float
														}
														addListener(object : Animator.AnimatorListener
														{
																override fun onAnimationRepeat(animation:Animator?)
																{
																}

																override fun onAnimationCancel(animation:Animator?)
																{
																}

																override fun onAnimationStart(animation:Animator?)
																{
																		isRun = true
																}

																override fun onAnimationEnd(animation:Animator?)
																{
																		isDown = false
																		isRun = false
																}
														})
														duration = 300
														start()
												}
								}
						}
						else -> null
				}
		}
}