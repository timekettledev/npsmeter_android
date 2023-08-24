package cn.npsmeter.sdk.view

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import cn.npsmeter.sdk.NPSCloseType
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.UserConfig
import cn.npsmeter.sdk.api.ConfigResponseModel
import cn.npsmeter.sdk.api.QuestionResponseModel
import cn.npsmeter.sdk.api.ServiceApi
import cn.npsmeter.sdk.utils.ThanksIconManager
import kotlin.math.min


class NpsQuestionAlertView : DialogFragment() {

    private lateinit var closeAction: (NPSCloseType) -> Unit
    private lateinit var question: QuestionResponseModel.QuestionModel
    private lateinit var userConfig: UserConfig
    private lateinit var config: ConfigResponseModel.ConfigModel
    private lateinit var questionView: NpsMeterQuestionView
    private lateinit var mContext: Context
    private lateinit var answerButton: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private var canAnswer = false
    private var showUserClose = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFullScreen) //dialog全屏
    }

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window!!
        val lp = window.attributes
        val screenWidth = requireActivity().windowManager.defaultDisplay.width
        if (config.type() == 3) {
            lp.gravity = Gravity.CENTER
            lp.width = dp2px(600f).toInt().coerceAtMost(screenWidth - 80)
        } else {
            lp.gravity = Gravity.BOTTOM
            lp.width = dp2px(600f).toInt().coerceAtMost(screenWidth)
        }
        window.attributes = lp
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (showUserClose) {
            this.closeAction(NPSCloseType.User)
            this.dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (savedInstanceState != null) {
            showUserClose = false
            this.dismiss()
            return null
        }
        val view = inflater.inflate(R.layout.view_question, container, false)

        val screenWidth = requireActivity().windowManager.defaultDisplay.width
        val screenHeight = requireActivity().windowManager.defaultDisplay.height
        if (config.type() != 3 && screenHeight > screenWidth) {
            val bottomMargin: LinearLayout = view.findViewById(R.id.bottom_margin);
            bottomMargin.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, userConfig.bottomPadding
            )
        }

        val logoLayout: LinearLayout = view.findViewById(R.id.logo_background)
        if (config.show_logo == 1) {
            logoLayout.visibility = View.VISIBLE
        } else {
            logoLayout.visibility = View.GONE
        }

        val titleTextView: TextView = view.findViewById(R.id.title)
        titleTextView.text = question.title
        titleTextView.paint.isFakeBoldText = true //字体加粗
        titleTextView.setTextColor(this.config.textColor())

        val resources: Resources = this.resources
        val dm: DisplayMetrics = resources.displayMetrics

        var width = (dm.widthPixels / dm.density).toInt()
        if (config.type() == 3) {
            width = ((dm.widthPixels - 80) / dm.density).toInt()
        }
        val height = (dm.heightPixels / dm.density).toInt()

        var large = false
        if (width > 600) {
            large = true
            width = 600
        }

        answerButton = view.findViewById(R.id.answer_button)
        answerButton.setOnClickListener {
            this.answer(null, this.questionView.answer())
        }
        this.changeSubmitButton(false)

        val closeButton: ImageView = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            showUserClose = false
            this.closeAction(NPSCloseType.User)
            this.dismiss()
        }

        val doNotCloseView: View = view.findViewById(R.id.do_not_close)
        doNotCloseView.setOnClickListener {

        }

        val topView: LinearLayout = view.findViewById(R.id.white_back)
        topView.setBackgroundColor(this.config.backgroundColor())


        val topFillColor = config.backgroundColor()
        val topGd = GradientDrawable()//创建drawable
        if (config.type() == 3 || (screenHeight > screenWidth && userConfig.bottomPadding > 1)) {
            topGd.cornerRadii =
                this.getCornerRadii(leftBottom = 12F, rightBottom = 12F)
        } else {
            topGd.cornerRadii =
                this.getCornerRadii(leftBottom = 0F, rightBottom = 0F)
        }
        topGd.setColor(topFillColor)
        topView.background = topGd

        val commitView: RelativeLayout = view.findViewById(R.id.commit_view)
        val showSureButton = this.question.showSureButton()
        if (!showSureButton) {
            commitView.visibility = View.GONE
        }

        val logoText: TextView = view.findViewById(R.id.logo_text)
        logoText.setTextColor(this.config.primaryColor())
        val poweredText: TextView = view.findViewById(R.id.powered)
        poweredText.setTextColor(this.config.primaryColor())
        poweredText.alpha = 0.3F

        when (question.type) {
            "nps" -> {
                this.questionView =
                    NpsMeterNpsView(
                        question,
                        this.mContext,
                        this.config,
                        large,
                        width,
                    ) { rating ->
                        this.answer(rating, null)
                    }
            }
            "ces" -> {
                this.questionView =
                    NpsMeterNpsView(
                        question,
                        this.mContext,
                        this.config,
                        large,
                        width,
                    ) { rating ->
                        this.answer(rating, null)
                    }
            }
            "select" -> {
                this.questionView =
                    NpsMeterSelectView(question,
                        this.mContext,
                        this.config,
                        large,
                        width,
                        min(300, height - 52 - 8 - 90 - 80),
                        false, changeButton = {

                        }, selectResult = { text ->
                            this.answer(null, arrayOf(text))
                        })
            }
            "checkbox" -> {
                this.questionView =
                    NpsMeterSelectView(question,
                        this.mContext,
                        this.config,
                        large,
                        width,
                        min(300, height - 52 - 8 - 90 - 80),
                        true, changeButton = { canSubmit ->
                            this.changeSubmitButton(canSubmit)
                        }, selectResult = {
                        })
            }
            "text" -> {
                this.questionView =
                    NpsMeterTextView(question, this.mContext, this.config) { canSubmit ->
                        this.changeSubmitButton(canSubmit)
                    }
            }
            "face" -> {
                this.questionView =
                    PicQuestionView(question, this.mContext, this.config) { value ->
                        this.answer(null, arrayOf(value))
                    }
            }
        }

        val questionLayout: RelativeLayout = view.findViewById(R.id.question)
        questionLayout.addView(this.questionView)

        this.progressBar = ProgressBar(context)
        this.progressBar.isIndeterminate = true

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(this.progressBar)
        questionLayout.addView(rl, params)

        this.progressBar.visibility = View.GONE

        return view
    }

    private fun changeSubmitButton(canSubmit: Boolean) {
        val gd = GradientDrawable()//创建drawable
        this.canAnswer = canSubmit
        if (canSubmit) {
            gd.setColor(this.config.primaryColor())
        } else {
            gd.setColor(this.config.primaryColor() and 0xB2FFFFFF.toInt())
        }
        val resources: Resources = this.resources
        val dm: DisplayMetrics = resources.displayMetrics
        gd.cornerRadius = 4 * dm.density
        answerButton.background = gd
    }

    private fun getCornerRadii(
        leftTop: Float = 12F, rightTop: Float = 12F,
        leftBottom: Float = 12F, rightBottom: Float = 12F,
    ): FloatArray {
        //这里返回的一个浮点型的数组，一定要有8个元素，不然会报错
        return floatArrayOf(
            dp2px(leftTop),
            dp2px(leftTop),
            dp2px(rightTop),
            dp2px(rightTop),
            dp2px(rightBottom),
            dp2px(rightBottom),
            dp2px(leftBottom),
            dp2px(leftBottom)
        )
    }

    private fun dp2px(dpVal: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, mContext.resources.displayMetrics
        )
    }

    private fun answer(rating: Int?, answer: Any?) {
        if (this.canAnswer || !this.question.showSureButton()) {
            this.progressBar.visibility = View.VISIBLE
            ServiceApi.answer(
                this.config.id,
                this.question.id.toString(),
                this.question.view_id,
                this.question.view_unique_id,
                rating,
                answer,
                this.mContext,
            ) { questionModel, _ ->
                try {
                    this.progressBar.visibility = View.GONE
                    if (questionModel != null) {

                        val manager = fragmentManager!!
                        if (questionModel.is_complete == 1) {//已经结束，展示感谢

                            val bitmap: Bitmap? = ThanksIconManager.getImage(config)
                            if (bitmap != null) {
                                NpsIconThanksDialog.newInstance(config, bitmap).apply { }
                                    .show(manager, "")
                            } else {
                                ThanksDialog.newInstance(config).apply { }.show(manager, "")
                            }
                            showUserClose = false
                            this.dismiss()
                            this.closeAction(NPSCloseType.Finish)
                        } else {
                            if (questionModel.canShow()) {
                                newInstance(
                                    questionModel,
                                    config,
                                    userConfig,
                                    this.closeAction
                                ).apply {

                                }.show(manager, "")
                                showUserClose = false
                                this.dismiss()
                            } else {
                                showUserClose = false
                                this.dismiss()
                                this.closeAction(NPSCloseType.OtherError)
                            }
                        }
                    } else {
                        showUserClose = false
                        this.dismiss()
                        this.closeAction(NPSCloseType.RequestAnswerError);
                    }
                } catch (exception: Exception) {
                    showUserClose = false
                    this.dismiss()
                    ServiceApi.errorLog(exception.toString(), this.mContext)
                    this.closeAction(NPSCloseType.OtherError)
                }
            }
        }
    }

    companion object {
        fun newInstance(
            questionModel: QuestionResponseModel.QuestionModel,
            configModel: ConfigResponseModel.ConfigModel,
            userConfig: UserConfig,
            closeAction: ((NPSCloseType) -> Unit),
        ): NpsQuestionAlertView {
            return NpsQuestionAlertView().apply {
                this.question = questionModel
                this.config = configModel
                this.closeAction = closeAction
                this.userConfig = userConfig
            }
        }
    }
}