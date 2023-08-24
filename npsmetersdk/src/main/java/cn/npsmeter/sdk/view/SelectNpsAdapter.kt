package cn.npsmeter.sdk.view

import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.npsmeter.sdk.R
import cn.npsmeter.sdk.api.ConfigResponseModel
import java.util.*


class SelectNpsAdapter(
    selectList: ArrayList<NpsSelectModel>,
    val config: ConfigResponseModel.ConfigModel,
    private val checkbox: Boolean,
    private val dm: DisplayMetrics,
    private val large: Boolean,
    val changeButton: (canSubmit: Boolean) -> Unit,
    val selectResult: (text: String) -> Unit,
) :

    RecyclerView.Adapter<SelectNpsAdapter.ViewHolder>() {

    private val list: ArrayList<NpsSelectModel> = selectList

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text)
        val relative: LinearLayout = view.findViewById(R.id.relative)
        var background: RelativeLayout = view.findViewById(R.id.background)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_nps, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = this.list[position]
        holder.textView.text = model.str
        val linearParams =
            holder.background.layoutParams
        linearParams.width = model.width.toInt()
        linearParams.height = model.height.toInt()
        holder.background.layoutParams = linearParams

        val gd = GradientDrawable()//创建drawable
        if (model.select) {
            gd.setColor(this.config.primaryColor())
            holder.textView.setTextColor(0xffffffff.toInt())
        } else {
            holder.textView.setTextColor(config.textColor())
            gd.setColor(this.config.textColor() and 0x0FFFFFFF)
        }
        gd.cornerRadius = 4 * dm.density
        holder.relative.background = gd

        val relativeParams = RelativeLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        if (large) {
            if (model.double) {
                if (model.left) {
                    relativeParams.setMargins((18 * dm.density).toInt(),
                        (4 * dm.density).toInt(),
                        (6 * dm.density).toInt(),
                        (4 * dm.density).toInt()
                    )
                } else {
                    relativeParams.setMargins((6 * dm.density).toInt(),
                        (4 * dm.density).toInt(),
                        (18 * dm.density).toInt(),
                        (4 * dm.density).toInt()
                    )
                }
            } else {
                relativeParams.setMargins((18 * dm.density).toInt(),
                    (4 * dm.density).toInt(),
                    (18 * dm.density).toInt(),
                    (4 * dm.density).toInt()
                )
            }
        } else {
            relativeParams.setMargins((20 * dm.density).toInt(),
                (4 * dm.density).toInt(),
                (20 * dm.density).toInt(),
                (4 * dm.density).toInt()
            )
        }
        holder.relative.layoutParams = relativeParams

        holder.background.setOnClickListener {
            model.select = !model.select
            if (checkbox) {
                this.notifyDataSetChanged()
                var haveResult = false
                for (item in this.list) {
                    if (item.select) {
                        haveResult = true
                    }
                }
                this.changeButton(haveResult)
            } else {
                this.notifyDataSetChanged()
                this.selectResult(model.str)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}