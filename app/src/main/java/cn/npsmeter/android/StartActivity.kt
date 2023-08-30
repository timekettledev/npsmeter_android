package cn.npsmeter.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import cn.npsmeter.sdk.NPSCloseType
import cn.npsmeter.sdk.NpsMeter

class StartActivity : AppCompatActivity() {
    private lateinit var button: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }
    }


}