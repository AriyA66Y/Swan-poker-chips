package com.example.poker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.poker.ui.i18n.getAppStrings
import com.example.ui.theme.FeltBorder
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun InitialLanguageDialog(
    onSelectLanguage: (language: String) -> Unit
) {
    var selectedLang by remember { mutableStateOf("fa") }
    val strings = getAppStrings(selectedLang)

    Dialog(
        onDismissRequest = { /* Force explicit user selection on first launch */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            tonalElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(24.dp))
                .testTag("initial_language_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(GoldDark.copy(alpha = 0.4f))
                        .border(1.5.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language Selection Icon",
                        tint = GoldLight,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = strings.initialLanguageTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GoldLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.initialLanguageSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Option 1: Persian (فارسی)
                LanguageOptionCard(
                    title = "فارسی (Persian)",
                    subtitle = "واحد چیپ: چیپ • چیدمان راست‌به‌چپ (RTL)",
                    flagEmoji = "🇮🇷",
                    isSelected = (selectedLang == "fa"),
                    onClick = { selectedLang = "fa" },
                    testTag = "lang_option_persian"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Option 2: English
                LanguageOptionCard(
                    title = "English",
                    subtitle = "Chip unit: chip • Left-to-Right layout (LTR)",
                    flagEmoji = "🇺🇸",
                    isSelected = (selectedLang == "en"),
                    onClick = { selectedLang = "en" },
                    testTag = "lang_option_english"
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Confirm Button
                Button(
                    onClick = { onSelectLanguage(selectedLang) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_initial_language_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(
                        text = strings.confirmLanguageBtn,
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionCard(
    title: String,
    subtitle: String,
    flagEmoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) GoldPrimary else FeltBorder,
        label = "borderColor"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) SurfaceElevated else Color(0xFF141920),
        label = "bgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag Box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF232A34)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = flagEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (isSelected) GoldLight else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = ProfitGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
