package com.example.newfrontcdemo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        // 1. 获取传递过来的资产名称
        val assetName = intent.getStringExtra("ASSET_NAME") ?: "Unknown Story"

        // 2. 更新界面信息
        findViewById<TextView>(R.id.tv_asset_info).text = "Current Story: $assetName"

        // 3. 返回按钮逻辑 (点击整个 LinearLayout 容器)
        findViewById<View>(R.id.btn_back_container).setOnClickListener {
            finish() // 关闭当前页面，返回上一页
        }

        // 4. 导出按钮逻辑
        findViewById<View>(R.id.btn_export).setOnClickListener {
            // 这里可以弹出一个 Loading 提示，或者跳转
            Toast.makeText(this, "Exporting $assetName...", Toast.LENGTH_LONG).show()
        }
    }
}