package com.shiyao.ai_story

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TraditionalActivity : AppCompatActivity() {

    private lateinit var btnGoToCreate: Button
    private lateinit var btnGoToAssets: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traditional)

        // 初始化控件
        initViews()

        // 设置事件监听
        setListeners()
    }

    /**
     * 初始化控件
     */
    private fun initViews() {
        btnGoToCreate = findViewById(R.id.btn_create)
        btnGoToAssets = findViewById(R.id.btn_assets)
    }

    /**
     * 设置事件监听
     */
    private fun setListeners() {
        // 跳转到 Compose 主界面
        btnGoToCreate.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 跳转到另一个 XML 界面
        btnGoToAssets.setOnClickListener {
            // 这里可以跳转到另一个 XML Activity
            // val intent = Intent(this, DetailActivity::class.java)
            // startActivity(intent)
        }
    }
}
