package hzkj.cc.base.weight.indulgeTitle

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.weight.CcTextView

class IndulgeTitle(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    var textView: CcTextView
    var mostLeftImageView: ImageView
    var rightImageView: ImageView

    var leftImageView: ImageView

    var showLeft: Boolean = false
    var rightImage: Drawable? = null
    var centerTextColor: Int = 0
    var leftImage: Drawable? = null

    var mostLeftImage: Drawable? = null
    lateinit var rightImageClickListenner: () -> Unit
    lateinit var leftImageClickListenner: () -> Unit

    init {
        textView = CcTextView(context)
        textView.textSize = 20f
        textView.layoutParams =
            ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(textView)
        mostLeftImageView = ImageView(context)
        mostLeftImageView.layoutParams =
            ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(mostLeftImageView)

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.IndulgeTitle)
        textView.text = typedArray.getString(R.styleable.IndulgeTitle_centerText)
        showLeft = typedArray.getBoolean(R.styleable.IndulgeTitle_showLeftBack, true)
        rightImage = typedArray.getDrawable(R.styleable.IndulgeTitle_rightImage)
        leftImage = typedArray.getDrawable(R.styleable.IndulgeTitle_leftImage)

        mostLeftImage = typedArray.getDrawable(R.styleable.IndulgeTitle_mostLeftImage)
        centerTextColor = typedArray.getColor(R.styleable.IndulgeTitle_centerTextColor, 0)
        textView.setTextColor(if (centerTextColor == 0) context.resources.getColor(R.color.base_white) else centerTextColor)

        if (mostLeftImage == null) {
            mostLeftImageView.setImageDrawable(
                context.resources.getDrawable(R.drawable.left_back)
            )
        } else {
            mostLeftImageView.setImageDrawable(
                mostLeftImage
            )
        }


        mostLeftImageView.setOnClickListener { (context as AppCompatActivity)?.finish() }
        typedArray.recycle()
        rightImageView = ImageView(context)
        rightImageView.layoutParams =
            ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        if (rightImage != null) {
            rightImageView.setImageDrawable(rightImage)
        }
        addView(rightImageView)
        leftImageView = ImageView(context)
        leftImageView.layoutParams =
            ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        if (leftImage != null) {
            leftImageView.setImageDrawable(leftImage)
        }
        addView(leftImageView)
        leftImageView.setOnClickListener {
            leftImageClickListenner.invoke()
        }
        rightImageView.setOnClickListener {
            rightImageClickListenner.invoke()
        }
    }

    fun setRightDrawable(drawable: Drawable) {
        rightImageView.setImageDrawable(drawable)
    }

    fun setLeftDrawable(drawable: Drawable) {
        leftImageView.setImageDrawable(drawable)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            (ViewUtil.dipToPx(context, 55)).toInt()
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (showLeft) {
            mostLeftImageView.layout(
                ViewUtil.dipToPx(context, 5).toInt(),
                ((height) / 2 - mostLeftImageView.measuredHeight / 2),
                mostLeftImageView.measuredWidth + ViewUtil.dipToPx(context, 5).toInt(),
                ((height) / 2 + mostLeftImageView.measuredHeight / 2)
            )
        }

        rightImageView.layout(
            width - ViewUtil.dipToPx(context, 10).toInt() - rightImageView.measuredWidth,
            ((height) / 2 - rightImageView.measuredHeight / 2),
            width - ViewUtil.dipToPx(context, 10).toInt(),
            ((height) / 2 + rightImageView.measuredHeight / 2)
        )

        leftImageView.layout(
            width - ViewUtil.dipToPx(
                context,
                20
            ).toInt() - leftImageView.measuredWidth - rightImageView.measuredWidth,
            ((height) / 2 - leftImageView.measuredHeight / 2),
            width - ViewUtil.dipToPx(context, 20).toInt() - rightImageView.measuredWidth,
            ((height) / 2 + leftImageView.measuredHeight / 2)
        )
        textView.layout(
            width / 2 - textView.measuredWidth / 2,
            ((height) / 2 - textView.measuredHeight / 2),
            width / 2 + textView.measuredWidth / 2,
            ((height) / 2 + textView.measuredHeight / 2)
        )
    }
}