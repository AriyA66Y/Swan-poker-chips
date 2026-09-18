package com.example.poker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poker.model.Player
import com.example.poker.model.Pot
import com.example.ui.theme.FeltCard
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

@Composable
fun PotsDisplay(
    pots: List<Pot>,
    players: List<Player>,
    modifier: Modifier = Modifier,
    currencySymbol: String = "چیپ"
) {
    val totalInAllPots = pots.sumOf { it.amount }
    val mainPot = pots.firstOrNull()
    val sidePots = if (pots.size > 1) pots.drop(1) else emptyList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C2417),
                        Color(0xFF071B11)
                    )
                )
            )
            .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row: Total Pot Banner
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(GoldDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Pot Icon",
                    tint = GoldLight,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "مجموع پات میز",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "${formatNumber(totalInAllPots)} $currencySymbol",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.testTag("total_pot_value")
                )
            }
        }

        // Side pots if any
        AnimatedVisibility(
            visible = sidePots.isNotEmpty(),
            enter = fadeIn() + expandVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "پات‌های ایجاد شده (${pots.size} پات خودکار):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pots.forEachIndexed { index, pot ->
                        SinglePotBadge(
                            pot = pot,
                            players = players,
                            isMain = (index == 0),
                            currencySymbol = currencySymbol
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SinglePotBadge(
    pot: Pot,
    players: List<Player>,
    isMain: Boolean,
    currencySymbol: String
) {
    val eligibleNames = pot.eligiblePlayerIds.mapNotNull { id ->
        players.firstOrNull { it.id == id }?.name
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isMain) FeltCard else Color(0xFF1E2835))
            .border(
                1.dp,
                if (isMain) GoldPrimary.copy(alpha = 0.5f) else Color(0xFF64B5F6).copy(alpha = 0.5f),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = pot.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isMain) GoldLight else Color(0xFF90CAF9),
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${formatNumber(pot.amount)} $currencySymbol",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "واجد شرایط: ${eligibleNames.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 9.sp
            ),
            maxLines = 1
        )
    }
}
