package cn.npsmeter.sdk.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import cn.npsmeter.sdk.api.QuestionResponseModel

class PicItem(
    picRating: QuestionResponseModel.PicRating, context: Context,
    var config: ConfigResponseModel.ConfigModel, answer: (value: Int) -> Unit
) : LinearLayout(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.item_pic_nps, this)

        val imageView:ImageView = findViewById(R.id.pic_image)
        val selectImageView:ImageView = findViewById(R.id.pic_select_image)
        val textView: TextView = findViewById(R.id.pic_text)
        textView.setTextColor(config.textColor())
        selectImageView.visibility = View.GONE

        if (picRating.value in 1..5) {
            when (picRating.value) {
                1 -> {
                    imageView.setImageResource(R.mipmap.face1)
                    selectImageView.setImageResource(R.mipmap.face1on)
                }
                2 -> {
                    imageView.setImageResource(R.mipmap.face2)
                    selectImageView.setImageResource(R.mipmap.face2on)
                }
                3 -> {
                    imageView.setImageResource(R.mipmap.face3)
                    selectImageView.setImageResource(R.mipmap.face3on)
                }
                4 -> {
                    imageView.setImageResource(R.mipmap.face4)
                    selectImageView.setImageResource(R.mipmap.face5on)
                }
                5 -> {
                    imageView.setImageResource(R.mipmap.face5)
                    selectImageView.setImageResource(R.mipmap.face5on)
                }
            }
        }
        textView.text = picRating.content

        this.setOnClickListener { _ ->
            selectImageView.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            textView.setTextColor(Color.BLACK)
            answer(picRating.value)
        }
    }

}