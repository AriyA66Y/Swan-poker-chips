package com.example.poker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.poker.model.Player
import com.example.poker.model.Pot
import com.example.ui.theme.AllInPurple
import com.example.ui.theme.BlueCall
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.FoldGrey
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceElevated
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

@Composable
fun BettingControls(
    player: Player,
    currentHighestBet: Long,
    minRaise: Long,
    pots: List<Pot>,
    onFold: () -> Unit,
    onCheck: () -> Unit,
    onCall: () -> Unit,
    onBetOrRaise: (Long) -> Unit,
    onAllIn: () -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "چیپ"
) {
    val callDiff = max(0L, currentHighestBet - player.currentStreetBet)
    val canCheck = (callDiff == 0L)
    val actualCallAmount = min(callDiff, player.chips)

    val minBetOrRaiseTotal = if (currentHighestBet == 0L) {
        minRaise
    } else {
        currentHighestBet + minRaise
    }

    val maxBetTotal = player.currentStreetBet + player.chips
    var isRaisePanelOpen by remember { mutableStateOf(false) }

    val safeMin = min(minBetOrRaiseTotal, maxBetTotal)
    var sliderValue by remember(player.id, currentHighestBet) {
        mutableFloatStateOf(safeMin.toFloat())
    }

    val totalPotAmount = pots.sumOf { it.amount }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(SurfaceElevated)
            .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(14.dp)
    ) {
        // Player info banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(ProfitGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "نوبت: ${player.name}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Text(
                text = "موجودی: ${formatNumber(player.chips)} $currencySymbol",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Raise Slider & Quick Buttons panel
        AnimatedVisibility(
            visible = isRaisePanelOpen,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0C1F15))
                    .border(1.dp, FeltBorder, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                val currentTarget = sliderValue.roundToLong()
                val addedAmount = max(0L, currentTarget - player.currentStreetBet)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentHighestBet == 0L) "مبلغ بت:" else "مبلغ نهایی ریز:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f))
                    )
                    Text(
                        text = "${formatNumber(currentTarget)} $currencySymbol (+$addedAmount)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                if (maxBetTotal > safeMin) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = safeMin.toFloat()..maxBetTotal.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier.testTag("raise_slider")
                    )
                }

                // Quick presets buttons (Min, 2x, 1/2 Pot, Pot, All-in)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickBetButton(text = "حداقل (Min)") {
                        sliderValue = safeMin.toFloat()
                    }
                    QuickBetButton(text = "۲ برابر بلایند") {
                        val v = min(maxBetTotal, currentHighestBet + minRaise * 2)
                        sliderValue = max(safeMin, v).toFloat()
                    }
                    if (totalPotAmount > 0) {
                        QuickBetButton(text = "نصف پات (½)") {
                            val v = min(maxBetTotal, currentHighestBet + (totalPotAmount / 2))
                            sliderValue = max(safeMin, v).toFloat()
                        }
                        QuickBetButton(text = "کل پات (Pot)") {
                            val v = min(maxBetTotal, currentHighestBet + totalPotAmount)
                            sliderValue = max(safeMin, v).toFloat()
                        }
                    }
                    QuickBetButton(text = "آل‌این (Max)", isAllIn = true) {
                        sliderValue = maxBetTotal.toFloat()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Confirm Raise Button
                Button(
                    onClick = {
                        isRaisePanelOpen = false
                        onBetOrRaise(sliderValue.roundToLong())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("confirm_raise_button"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ثبت افزایش شرط",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Primary 4 Action Buttons Row: FOLD, CHECK/CALL, BET/RAISE, ALL-IN
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Fold
            OutlinedButton(
                onClick = onFold,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("action_fold_button"),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = LossRed
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(LossRed.copy(alpha = 0.7f)))
            ) {
                Text(
                    text = "فولد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }

            // Check or Call
            if (canCheck) {
                Button(
                    onClick = onCheck,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(48.dp)
                        .testTag("action_check_button"),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen)
                ) {
                    Text(
                        text = "چک",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
            } else {
                Button(
                    onClick = onCall,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("action_call_button"),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueCall)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "کال",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Text(
                            text = formatNumber(actualCallAmount),
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Bet or Raise button
            if (player.chips > callDiff) {
                Button(
                    onClick = {
                        isRaisePanelOpen = !isRaisePanelOpen
                    },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(48.dp)
                        .testTag("action_raise_toggle_button"),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRaisePanelOpen) GoldLight else GoldPrimary
                    )
                ) {
                    Text(
                        text = if (currentHighestBet == 0L) "بت" else "ریز",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }

            // All-in quick action
            Button(
                onClick = onAllIn,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("action_all_in_button"),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AllInPurple)
            ) {
                Text(
                    text = "آل‌این",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun QuickBetButton(
    text: String,
    isAllIn: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isAllIn) AllInPurple.copy(alpha = 0.3f) else Color(0xFF1B3826))
            .border(1.dp, if (isAllIn) AllInPurple else GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (isAllIn) Color(0xFFE1BEE7) else GoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
    }
}
