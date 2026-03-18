package com.apps.motivasiapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1F41BB))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = subtitle,
                color = Color(0xFFE8EFFE),
                lineHeight = 20.sp,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ProgressSection(
    modifier: Modifier = Modifier,
    completedCount: Int,
    totalCount: Int
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1F41BB).copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Progres Hari Ini: $completedCount/$totalCount",
                color = Color(0xFF1F41BB),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0xFFE8EFFE), shape = RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (totalCount == 0) 0f else completedCount.toFloat() / totalCount)
                        .fillMaxHeight()
                        .background(Color(0xFF1F41BB), shape = RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
fun MotivationalQuote(
    modifier: Modifier = Modifier,
    quote: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFF9C4),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = quote,
            color = Color(0xFF333333),
            fontWeight = FontWeight.Medium,
            lineHeight = 22.sp,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color(0xFF1F1F1F),
        modifier = modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    message: String = "Tidak ada data"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color(0xFF999999),
            fontSize = 14.sp
        )
    }
}
