package com.shiyao.ai_story.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shiyao.ai_story.R
import com.shiyao.ai_story.components.CommonButton

@Composable
fun PreviewScreen(navController: NavController, assetName: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
                .clickable { navController.popBackStack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("<", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.primary))
            Text(" Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.primary))
        }

        Text(
            text = "Preview",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 0.dp),
            color = colorResource(id = R.color.text_secondary)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_media_play),
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                LinearProgressIndicator(
                    progress = 0.3f,
                    color = colorResource(id = R.color.primary),
                    trackColor = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(4.dp)
                )
            }
        }

        Text(
            text = "Story: $assetName",
            color = colorResource(id = R.color.text_secondary),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        CommonButton(
            text = "Export Video",
            backgroundColor = colorResource(id = R.color.primary),
            contentColor = Color.White,
            fontSize = 20,
            horizontalPadding = 14,
            verticalPadding = 18,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
            onClick = { Toast.makeText(context, "Exporting...", Toast.LENGTH_SHORT).show() }
        )
    }
}

