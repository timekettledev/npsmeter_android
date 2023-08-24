package cn.npsmeter.sdk.view

import android.content.Context
import android.widget.RelativeLayout
import cn.npsmeter.sdk.api.ConfigResponseModel
import cn.npsmeter.sdk.api.QuestionResponseModel

open class NpsMeterQuestionView(
    var question: QuestionResponseModel.QuestionModel,
    context: Context,
    var config: ConfigResponseModel.ConfigModel,
) : RelativeLayout(context) {

    open fun answer(): Any? {
        return null
    }
}
