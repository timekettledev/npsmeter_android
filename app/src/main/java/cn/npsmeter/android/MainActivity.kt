package cn.npsmeter.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import cn.npsmeter.sdk.NPSCloseType
import cn.npsmeter.sdk.NpsMeter

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var button: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.editText = findViewById(R.id.edit)
        this.button = findViewById(R.id.button)

        this.button.setOnClickListener {
            NpsMeter.show(
                this.editText.text.toString(),
                "Android Demo ID 1",
                "Android Demo licoba",
                "Android Demo Remark ",
                supportFragmentManager,
                baseContext,
                0,
                {
                    val ts = Toast.makeText(baseContext, "展示问卷", Toast.LENGTH_SHORT)
                    ts.show()
                },
                { type ->
                    when (type) {

                        NPSCloseType.Finish -> {
                            val ts = Toast.makeText(baseContext, "完成问卷", Toast.LENGTH_SHORT)
                            ts.show()
                        }

                        NPSCloseType.User -> {
                            val ts = Toast.makeText(baseContext, "用户关闭", Toast.LENGTH_SHORT)
                            ts.show()
                        }

                        NPSCloseType.OtherError -> {
                            val ts = Toast.makeText(baseContext, "其他异常", Toast.LENGTH_SHORT)
                            ts.show()
                        }

                        NPSCloseType.DownFail -> {
                            val ts = Toast.makeText(baseContext, "下载失败", Toast.LENGTH_SHORT)
                            ts.show()
                        }

                        NPSCloseType.MinFatigue -> {
                            val ts = Toast.makeText(
                                baseContext,
                                "距离上次弹出问卷时间过短",
                                Toast.LENGTH_SHORT
                            )
                            ts.show()
                        }

                        NPSCloseType.FirstDay -> {
                            val ts = Toast.makeText(
                                baseContext,
                                "距离首次下载配置时间过短",
                                Toast.LENGTH_SHORT
                            )
                            ts.show()
                        }

                        NPSCloseType.HaveShowForId -> {
                            val ts =
                                Toast.makeText(baseContext, "已经显示过该问卷", Toast.LENGTH_SHORT)
                            ts.show()
                        }

                        NPSCloseType.RequestAnswerError -> {
                            val ts =
                                Toast.makeText(baseContext, "回答问卷请求出错", Toast.LENGTH_SHORT)
                            ts.show()
                        }

                        NPSCloseType.AppCancel -> {
                            val ts = Toast.makeText(baseContext, "APP端取消", Toast.LENGTH_SHORT)
                            ts.show()
                        }
                    }
                }
            )
        }
    }


    override fun onResume() {
        super.onResume()
        this.editText.setText("4c629f862336c4d2")  // W3 Android
//        this.editText.setText("caa5ca97e21ac279")  // 官方Demo

    }
}