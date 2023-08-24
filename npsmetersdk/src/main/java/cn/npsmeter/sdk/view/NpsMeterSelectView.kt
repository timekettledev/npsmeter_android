package cn.npsmeter.sdk.view

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import cn.npsmeter.sdk.api.QuestionResponseModel
import kotlin.collections.ArrayList
import kotlin.math.min


@RequiresApi(Build.VERSION_CODES.Q)
class NpsMeterSelectView(
    question: QuestionResponseModel.QuestionModel,
    context: Context,
    config: ConfigResponseModel.ConfigModel,
    large: Boolean,
    viewWidth: Int,
    maxHeight: Int,
    checkbox: Boolean,
    val changeButton: (canSubmit: Boolean) -> Unit,
    val selectResult: (text: String) -> Unit,
) : NpsMeterQuestionView(
    question, context, config
) {

    private var recyclerView: RecyclerView
    private val list: ArrayList<NpsSelectModel> = ArrayList()

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_question_select, this)
        val resources: Resources = this.resources
        val dm: DisplayMetrics = resources.displayMetrics
        var haveSmall = false
        var totalHeight = 0F
        val ratingList: java.util.ArrayList<String> = java.util.ArrayList<String>()

        if (question.is_option_random == 1) {
            val list: java.util.ArrayList<String> = java.util.ArrayList<String>()
            list.addAll(question.rating_list);
            while (list.isNotEmpty()) {
                val num = list.size - 1
                val n = (0..num).random()
                ratingList.add(list[n])
                list.removeAt(n)
            }
        } else {
            ratingList.addAll(question.rating_list);
        }

        for (text in ratingList) {
            if (large) {
                val width = (viewWidth - 18 - 18 - 12) / 2 - 12
                val largeWidth = viewWidth - 18 - 18 - 12 - 12

                val height = this.getHeight(
                    text,
                    (width * dm.density).toInt()
                ) + 24 * dm.density + 8 * dm.density
                val largeHeight = this.getHeight(
                    text,
                    (largeWidth * dm.density).toInt()
                ) + 24 * dm.density + 8 * dm.density
                if (height == largeHeight) {
                    if (!haveSmall) {
                        totalHeight += height
                    }
                    haveSmall = !haveSmall

                    if (haveSmall) {
                        list.add(
                            NpsSelectModel(
                                text,
                                viewWidth / 2 * dm.density,
                                height,
                                double = true,
                                left = true
                            )
                        )
                    } else {
                        list.add(
                            NpsSelectModel(
                                text,
                                viewWidth / 2 * dm.density,
                                height,
                                double = true,
                                left = false
                            )
                        )
                    }
                } else {
                    haveSmall = false
                    totalHeight += largeHeight
                    list.add(
                        NpsSelectModel(
                            text,
                            viewWidth * dm.density,
                            largeHeight,
                            double = false,
                            left = false
                        )
                    )
                }
            } else {
                // 40 是两边padding各为20，24位狂内两边padding各为12
                val width = viewWidth - 40 - 24
                val height =
                    (this.getHeight(
                        text,
                        (width * dm.density).toInt()
                    ) + 24 * dm.density + 8 * dm.density)
                totalHeight += height
                list.add(
                    NpsSelectModel(
                        text, viewWidth * dm.density,
                        height,
                        double = false,
                        left = false
                    )
                )
            }
        }

        this.recyclerView = findViewById(R.id.recycler)
        val layoutManager = GridLayoutManager(getContext(), 2)
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val model = list[position]
                if (model.double) {
                    return 1
                }
                return 2
            }
        }
        recyclerView.layoutManager = layoutManager
        val videoAdapter =
            SelectNpsAdapter(
                this.list,
                this.config,
                checkbox,
                dm,
                large,
                changeButton = { canSubmit ->
                    this.changeButton(canSubmit)
                },
                selectResult = { text ->
                    this.selectResult(text)
                })
        recyclerView.adapter = videoAdapter

        val linearParams =
            recyclerView.layoutParams
        linearParams.width = (viewWidth * dm.density).toInt()
        linearParams.height = min(totalHeight, maxHeight * dm.density).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val gd = GradientDrawable()//创建drawable
            gd.setColor(this.config.textColor() and 0x5FFFFFFF)
            recyclerView.verticalScrollbarThumbDrawable = gd
        }

        recyclerView.isScrollbarFadingEnabled = false;
        recyclerView.scrollBarFadeDuration = 0;

        recyclerView.layoutParams = linearParams
    }

    private fun getHeight(text: String, width: Int): Int {
        val myTextPaint = TextPaint()
        myTextPaint.isAntiAlias = true
        myTextPaint.textSize = 16F * resources.displayMetrics.density
        val resources: Resources = this.resources
        val dm: DisplayMetrics = resources.displayMetrics

        val myStaticLayout = StaticLayout.Builder.obtain(
            text,
            0,
            text.length,
            myTextPaint,
            width
        ).setLineSpacing((1.0 * dm.density).toFloat(), 1.0F).build()
        return myStaticLayout.height
    }

    override fun answer(): Any {
        val list = ArrayList<String>()
        for (model in this.list) {
            if (model.select) {
                list.add(model.str)
            }
        }
        return list
    }
}
