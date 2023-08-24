package cn.npsmeter.sdk.api

import android.graphics.Color


class ConfigResponseModel {
    class ConfigModel {
        var id: String = ""//	问卷ID
        private var primary_color: String = "#0089FF"//主色调
        private var background_color: String = "#FFFFFF"//背景颜色
        private var text_color: String = "#2A3155"//文字颜色
        var thanks_fields: String = "谢谢您的反馈" //感谢语
        var show_logo: Int = 0    //是否展示底部logo：1（是）、0（否）
        var repeat_duration: Int = 0;//多少天后可再次弹出
        var custom_fatigue: Int = 0//是否开启疲劳控制
        var position: String = "xcx_new"
        var open_cookies_config: Int = 0
        var from_first_day: Int = 0
        var is_open_min_fatigue: Int = 0
        var min_fatigue_duration: Int = 0
        var is_show_thanks_icon: Int = 0
        var thanks_icon: String? = null


        fun backgroundColor(): Int {
            return Color.parseColor(this.background_color)
        }

        fun textColor(): Int {
            return Color.parseColor(this.text_color)
        }

        fun primaryColor(): Int {
            return Color.parseColor(this.primary_color)
        }

        fun type(): Int {
            when (position) {
                "xcx_new" -> {
                    return 1;
                }//两行
                "bc" -> {
                    return 2;
                }//一行
                "ct" -> {
                    return 3;
                }//居中
            }
            return 1
        }
    }

    var data: ConfigModel? = null
    var message: String? = null
}