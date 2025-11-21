package com.example.newfrontcdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView // 记得导入这个

class AssetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assets_library)

        // 1. 初始化 RecyclerView
        val rvAssets = findViewById<RecyclerView>(R.id.rv_assets)
        rvAssets.layoutManager = GridLayoutManager(this, 2)

        // 2. 模拟数据
        val mockData = listOf(
            AssetBean("Journey Through Woods", "Apr 21, 2024", android.R.drawable.ic_menu_gallery),
            AssetBean("Sunset at the Summit", "Apr 20, 2024", android.R.drawable.ic_menu_camera),
            AssetBean("Morning Fog", "Apr 19, 2024", android.R.drawable.ic_menu_compass),
            AssetBean("Ocean View", "Apr 18, 2024", android.R.drawable.ic_menu_mapmode),
            AssetBean("Journey Through Woods", "Apr 21, 2024", android.R.drawable.ic_menu_gallery),
            AssetBean("Sunset at the Summit", "Apr 20, 2024", android.R.drawable.ic_menu_camera),
            AssetBean("Morning Fog", "Apr 19, 2024", android.R.drawable.ic_menu_compass),
            AssetBean("Ocean View", "Apr 18, 2024", android.R.drawable.ic_menu_mapmode),
            AssetBean("Journey Through Woods", "Apr 21, 2024", android.R.drawable.ic_menu_gallery),
            AssetBean("Sunset at the Summit", "Apr 20, 2024", android.R.drawable.ic_menu_camera),
            AssetBean("Morning Fog", "Apr 19, 2024", android.R.drawable.ic_menu_compass),
            AssetBean("Ocean View", "Apr 18, 2024", android.R.drawable.ic_menu_mapmode),
            AssetBean("Journey Through Woods", "Apr 21, 2024", android.R.drawable.ic_menu_gallery),
            AssetBean("Sunset at the Summit", "Apr 20, 2024", android.R.drawable.ic_menu_camera),
            AssetBean("Morning Fog", "Apr 19, 2024", android.R.drawable.ic_menu_compass),
            AssetBean("Ocean View", "Apr 18, 2024", android.R.drawable.ic_menu_compass)
        )

        // 3. 设置适配器和跳转
        val adapter = AssetsAdapter(mockData) { asset ->
            // 跳转到预览页
            val intent = Intent(this, PreviewActivity::class.java)
            intent.putExtra("ASSET_NAME", asset.name) // 传递数据
            startActivity(intent)
        }
        rvAssets.adapter = adapter

        // ==========================================
        // 4. 更新：底部导航栏逻辑 (适配 BottomNavigationView)
        // ==========================================
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        // 设置默认选中 "Assets" 这一项
        bottomNav.selectedItemId = R.id.nav_assets

        // 设置点击监听
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_create -> {
                    // 点击 Create 按钮
                    Toast.makeText(this, "Go to Create Page", Toast.LENGTH_SHORT).show()
                    // 实际项目中这里写: startActivity(Intent(this, CreateActivity::class.java))
                    true
                }

                R.id.nav_assets -> {
                    // 点击 Assets 按钮 (当前页)，不做操作
                    true
                }

                else -> false
            }
        }

        // ==========================================
        // 5. 更新：筛选按钮逻辑 (适配新 ID: btn_filter_icon)
        // ==========================================
        findViewById<View>(R.id.btn_filter_icon).setOnClickListener {
            Toast.makeText(this, "Filter clicked", Toast.LENGTH_SHORT).show()
        }
    }
}