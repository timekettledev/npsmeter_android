package cn.npsmeter.sdk.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import cn.npsmeter.sdk.NPSCloseType
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import kotlin.math.min

class NpsIconThanksDialog : DialogFragment() {
    private lateinit var config: ConfigResponseModel.ConfigModel
    private lateinit var mContext: Context
    private lateinit var sceneView: View
    lateinit var textView: TextView
    private lateinit var bitmap: Bitmap
    private var closeAction: ((NPSCloseType) -> Unit)? = null

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
        val view = inflater.inflate(R.layout.layout_thanks_icon_nps, container, false)
        this.dialog?.window?.setDimAmount(0f)//去除遮罩问题
        this.sceneView = view.findViewById(R.id.sun)
        this.textView = view.findViewById(R.id.text)

        val text = config.thanks_fields
        this.textView.text = text
        this.textView.setTextColor(config.textColor())

        val image = view.findViewById<ImageView>(R.id.image)
        image.setImageBitmap(bitmap)

        val dm: DisplayMetrics = resources.displayMetrics

        val paint: TextPaint = this.textView.paint
        val screenWidth = dm.widthPixels / dm.density
        val linearParams = this.textView.layoutParams
        val layout = this.getHeight(text, (screenWidth - 50 * 2 - 24 * 2).toInt())
        linearParams.width = min(
            (layout.width * dm.density).toInt(),
            Layout.getDesiredWidth(text, 0, text.length, paint)
                .toInt()
        )
        linearParams.height = (layout.height * dm.density).toInt() + 21 + 13 + 30;
        this.textView.layoutParams = linearParams

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
        ).setLineSpacing((1.0 * dm.density).toFloat(), 1.0F).build()
    }


    private fun startAnimation(dm: DisplayMetrics, height: Int) {
        val objectAnimator = ObjectAnimator.ofFloat(
            this.sceneView,
            "translationY",
            dm.heightPixels.toFloat(),
            dm.heightPixels.toFloat() / 2 - height / 2
        )
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

    fun outAnimation(dm: DisplayMetrics, height: Int) {
        val objectAnimator = ObjectAnimator.ofFloat(
            this.sceneView,
            "translationY",
            dm.heightPixels.toFloat() / 2 - height / 2,
            dm.heightPixels.toFloat()
        )
        objectAnimator.duration = 200
        objectAnimator.startDelay = 2000
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
            bitmapImage: Bitmap,
            closeAction: ((NPSCloseType) -> Unit)?

        ): NpsIconThanksDialog {
            return NpsIconThanksDialog().apply {
                config = configModel
                bitmap = bitmapImage
                this.closeAction = closeAction
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        closeAction?.invoke(NPSCloseType.Finish)
        closeAction = null
    }
}