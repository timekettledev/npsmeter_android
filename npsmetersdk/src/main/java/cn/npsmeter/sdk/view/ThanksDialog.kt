package cn.npsmeter.sdk.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import kotlin.math.min

class ThanksDialog : DialogFragment() {
    private lateinit var config: ConfigResponseModel.ConfigModel
    private lateinit var mContext: Context
    private lateinit var sceneView: View
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.thanksDialogFullScreen)//dialog全屏
    }

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (savedInstanceState != null) {
            this.dismiss()
            return null
        }
        val view = inflater.inflate(R.layout.layout_thanks_nps, container, false)
        this.dialog?.window?.setDimAmount(0f)//去除遮罩问题
        this.dialog?.setCancelable(false)//设置不可取消
        this.sceneView = view.findViewById(R.id.sun)
        this.textView = view.findViewById(R.id.text)
        this.textView.text = config.thanks_fields
        this.textView.setTextColor(config.textColor())
        val dm: DisplayMetrics = resources.displayMetrics
        val linearParams = this.textView.layoutParams
        this.textView.layoutParams = linearParams

        val redView:View = view.findViewById(R.id.red)
        redView.setBackgroundColor(config.primaryColor())

        this.startAnimation(dm, linearParams.height)
        return view
    }


    private fun getHeight(text: String, width: Int): StaticLayout {
        val myTextPaint = TextPaint()
        myTextPaint.isAntiAlias = true
        myTextPaint.textSize = 6F * resources.displayMetrics.density
        val resources: Resources = this.resources
        val dm: DisplayMetrics = resources.displayMetrics

        return StaticLayout.Builder.obtain(
            text,
            0,
            text.length,
            myTextPaint,
            width
        ).setLineSpacing((1.0*dm.density).toFloat(), 1.0F).build()
    }

    private fun startAnimation(dm: DisplayMetrics, height: Int) {
        val objectAnimator = ObjectAnimator.ofFloat(this.sceneView,
            "translationY",
            dm.heightPixels.toFloat(),
            dm.heightPixels.toFloat()/2 -height/2)
        objectAnimator.duration = 300
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                outAnimation(dm, height)
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        objectAnimator.start()
    }

    // 弹窗消失的动画
    fun outAnimation(dm: DisplayMetrics, height: Int) {
        val objectAnimator = ObjectAnimator.ofFloat(this.sceneView,
            "alpha", 1f,0f)
        objectAnimator.duration = 200
        objectAnimator.startDelay = 1500
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                dismiss()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        objectAnimator.start()
    }


    companion object {
        fun newInstance(
            configModel: ConfigResponseModel.ConfigModel,
        ): ThanksDialog {
            return ThanksDialog().apply {
                config = configModel
            }
        }
    }
}

