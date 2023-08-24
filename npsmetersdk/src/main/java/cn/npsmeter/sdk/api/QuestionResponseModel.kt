package cn.npsmeter.sdk.api

import java.util.*
import kotlin.collections.ArrayList


class QuestionResponseModel {

    class PicRating {
        var value: Int = 0
        var content: String = ""
    }


    class QuestionModel {
        var id: Int = 0//	题目ID
        var type: String? = null//	题目类型：nps（nps类型）、ces（费力度）、select（单选题）、checkbox（多选题）、text（文本）
        var title: String? = null//		标题
        var rating_list: ArrayList<String> = ArrayList()//	nps、ces题型分数列表;单选、多选题型选项列表
        var low_legend: String? = null//		最低分文案（仅限nps、ces题型）
        var high_legend: String? = null//	最高分文案（仅限nps、ces题型）
        var is_complete: Int = 0//		整份问卷调查是否已结束 0：（否），1：（是）
        var view_id: Int? = null//	本次访问ID
        var view_unique_id: String? = null
        var is_required: Int = 0
        var is_option_random: Int = 0
        var picRatingList: ArrayList<PicRating> = ArrayList()

        fun showSureButton(): Boolean {
            if (this.type == "checkbox" || this.type == "text") {
                return true
            }
            return false
        }

        fun canShow(): Boolean {
            if (this.type == "nps" || this.type == "ces" || this.type == "select" || this.type == "checkbox" || this.type == "text" || this.type == "face") {
                return true
            }
            return false
        }
    }

    var data: QuestionModel? = null
    var message: String? = null

    companion object {
        @JvmStatic
        fun mapToQuestion(map: Map<String, Any>): QuestionResponseModel {
            val questionResponseModel =  QuestionResponseModel();
            if (map.containsKey("data")) {
                val dataMap: Map<String, Any> = map["data"] as Map<String, Any>
                val question = QuestionModel()
                if (dataMap.containsKey("id")) {
                    question.id = (dataMap["id"] as Double).toInt()
                }
                question.type = dataMap["type"] as String?
                question.title = dataMap["title"] as String?
                question.low_legend = dataMap["low_legend"] as String?
                question.high_legend = dataMap["high_legend"] as String?
                if (dataMap.containsKey("is_complete")) {
                    question.is_complete = (dataMap["is_complete"] as Double).toInt()
                }
                if (dataMap.containsKey("view_id") && dataMap["view_id"] != "" && dataMap["view_id"] != null) {
                    question.view_id = (dataMap["view_id"] as Double).toInt()
                }
                question.view_unique_id = dataMap["view_unique_id"] as String?
                if (dataMap.containsKey("is_required")) {
                    question.is_required = (dataMap["is_required"] as Double).toInt()
                }
                if (dataMap.containsKey("is_option_random")) {
                    question.is_option_random = (dataMap["is_option_random"] as Double).toInt()
                }
                if (question.type.equals("face")) {
                    val list: List<Map<String, Any>> = dataMap["rating_list"] as List<Map<String, Any>>
                    for (mapItem in list) {
                        val picRating = PicRating()
                        picRating.value = (mapItem["value"] as Double).toInt()
                        picRating.content = mapItem["content"] as String
                        question.picRatingList.add(picRating);
                    }
                } else if (dataMap.containsKey("rating_list")){
                    question.rating_list = dataMap["rating_list"] as ArrayList<String>
                }
                questionResponseModel.data = question;
            }
            return questionResponseModel
        }
    }
}