package cn.npsmeter.sdk.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.EditText
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import cn.npsmeter.sdk.api.QuestionResponseModel

class NpsMeterTextView(
    question: QuestionResponseModel.QuestionModel,
    context: Context, config: ConfigResponseModel.ConfigModel,
    changeButton: (canSubmit: Boolean) -> Unit
) : NpsMeterQuestionView(
    question, context, config
) {

    private var editText: EditText

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_question_text, this)

        this.editText = findViewById(R.id.edit)

        val dm: DisplayMetrics = resources.displayMetrics

        val strokeWidth = 2 * dm.density // 3dp 边框宽度
        val roundRadius = 15 // 8dp 圆角半径
        val strokeColor: Int = this.config.textColor() and 0x4dFFFFFF
        val fillColor: Int = this.config.backgroundColor()
        val gd = GradientDrawable() //创建drawable
        gd.setColor(fillColor)
        gd.cornerRadius = roundRadius.toFloat()
        gd.setStroke(strokeWidth.toInt(), strokeColor)
        this.editText.background = gd
        this.editText.setTextColor(this.config.textColor())
        this.editText.setHintTextColor(this.config.textColor() and 0x4CFFFFFF)

        if (question.is_required == 1) {
            changeButton(false)
            this.editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    if (p0 != null) {
                        if (p0.isNotEmpty()) {
                            changeButton(true)
                        } else {
                            changeButton(false)
                        }
                    }
                }
            })
        } else {
            changeButton(true)
        }
    }

    override fun answer(): Any {
        return this.editText.text.toString()
    }
}
