package com.example.poker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

@Composable
fun PokerChipIcon(
    modifier: Modifier = Modifier,
    baseColor: Color = GoldPrimary,
    size: Int = 24
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(baseColor, baseColor.copy(alpha = 0.8f), Color(0xFF1B1B1B))
                )
            )
            .border(1.5.dp, GoldLight.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size((size * 0.65).dp)
                .clip(CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        )
    }
}

@Composable
fun ChipAmountBadge(
    amount: Long,
    modifier: Modifier = Modifier,
    currencySymbol: String = "چیپ",
    containerColor: Color = Color(0xFF132D1D),
    accentColor: Color = GoldPrimary
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PokerChipIcon(size = 18, baseColor = accentColor)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = formatNumber(amount),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = accentColor,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = currencySymbol,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        )
    }
}

fun formatNumber(number: Long): String {
    return String.format("%,d", number)
}
