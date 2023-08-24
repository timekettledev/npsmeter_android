package cn.npsmeter.sdk.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import cn.npsmeter.sdk.api.QuestionResponseModel

class PicQuestionView(
    question: QuestionResponseModel.QuestionModel,
    context: Context, config: ConfigResponseModel.ConfigModel,
    answer: (value: Int) -> Unit
) : NpsMeterQuestionView(
    question, context, config
) {
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_pic_question, this)
        val topButtonLine: LinearLayout = findViewById(R.id.button_line)
        for (picRating in question.picRatingList) {
            topButtonLine.addView(PicItem(picRating, context, config, answer))
        }
    }
}