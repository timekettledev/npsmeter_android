package cn.npsmeter.sdk.view

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import cn.npsmeter.sdk.api.QuestionResponseModel
import kotlin.collections.ArrayList


class NpsMeterNpsView(
    question: QuestionResponseModel.QuestionModel,
    context: Context,
    config: ConfigResponseModel.ConfigModel,
    large: Boolean,
    viewWidth: Int,
    answer: (rating: Int) -> Unit
) : NpsMeterQuestionView(
    question, context, config
) {

    private val buttonArray: ArrayList<TextView> = ArrayList()

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_nps, this)

        val leftTextView: TextView = findViewById(R.id.left_text)
        val leftBottomTextView: TextView = findViewById(R.id.left_bottom_text)
        val rightTextView: TextView = findViewById(R.id.right_text)
        val rightBottomTextView: TextView = findViewById(R.id.right_bottom_text)

        var maxNum = 10
        var minNum = 0

        var buttonWidth = (viewWidth - 20 * 2 - 14 * 5) / 6

        val space = if (this.question.type == "ces") {
            if (large) {
                8
            } else {
                11
            }
        } else {
            if (large) {
                5
            } else {
                7
            }
        }

        if (this.question.type == "ces") {
            minNum = 1
            maxNum = 5

            buttonWidth = if (large) {
                44
            } else {
                (viewWidth - 34 * 2 - 4 * 22) / 5
            }
        } else {
            if (large) {
                buttonWidth = (viewWidth - 9 * 2 - 10 * 10) / 11
            } else {
                if (config.type() == 2) {
                    val padding = 9;
                    buttonWidth = ((viewWidth - padding * 2 - 10 * space) / 11.0).toInt();
                }
            }
        }
        val topButtonLine: LinearLayout = findViewById(R.id.top_button_line)
        val bottomButtonLine: LinearLayout = findViewById(R.id.bottom_button_line)

        val resources: Resources = this.resources
        val dm: DisplayMetrics = resources.displayMetrics

        for (index in minNum..maxNum) {
            val button = TextView(context)
            button.gravity = Gravity.CENTER
            button.text = index.toString()
            button.setTextColor(config.textColor() and 0xbfFFFFFF.toInt())
            val roundRadius = 4 * dm.density
            val fillColor = config.textColor() and 0x0FFFFFFF
            val gd = GradientDrawable()//创建drawable
            gd.setColor(fillColor)
            gd.cornerRadius = roundRadius
            button.background = gd


            val layoutParams = LinearLayout.LayoutParams(
                (buttonWidth * dm.density).toInt(),
                (44 * dm.density).toInt()
            )

            layoutParams.setMargins(space, 0, space, 0) //4个参数按顺序分别是左上右下

            button.layoutParams = layoutParams

            if (index < 6 || large || config.type() == 2) {
                topButtonLine.addView(button)
            } else {
                bottomButtonLine.addView(button)
            }
            buttonArray.add(button)
            button.setOnClickListener { v ->
                val buttonIndex = (v as TextView).text.toString().toInt()
                for (i in 0..buttonIndex - minNum) {
                    val forButton = buttonArray[i]
                    val buttonGd = GradientDrawable()//创建drawable
                    buttonGd.setColor(config.primaryColor())
                    buttonGd.cornerRadius = 4 * dm.density
                    forButton.background = buttonGd
                    forButton.setTextColor(0xFFFFFFFF.toInt())
                }
                answer(v.text.toString().toInt())
            }
        }

        if (large || maxNum == 5) {
            bottomButtonLine.visibility = GONE
        }
        if (large && maxNum == 5) {
            leftTextView.text = this.question.low_legend
            rightTextView.text = this.question.high_legend
            leftBottomTextView.visibility = GONE
            rightBottomTextView.visibility = GONE
            leftTextView.setTextColor(this.config.textColor())
            rightTextView.setTextColor(this.config.textColor())
        } else {
            leftBottomTextView.text = this.question.low_legend
            rightBottomTextView.text = this.question.high_legend
            leftTextView.visibility = GONE
            rightTextView.visibility = GONE
            leftBottomTextView.setTextColor(this.config.textColor())
            rightBottomTextView.setTextColor(this.config.textColor())
        }
    }
}