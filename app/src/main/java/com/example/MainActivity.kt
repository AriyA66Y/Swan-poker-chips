package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.example.poker.ui.components.InitialLanguageDialog
import com.example.poker.ui.i18n.LocalAppStrings
import com.example.poker.ui.i18n.getAppStrings
import com.example.poker.ui.screens.AnalyticsScreen
import com.example.poker.ui.screens.PlayersScreen
import com.example.poker.ui.screens.TableScreen
import com.example.poker.viewmodel.PokerViewModel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceDark

enum class PokerTab(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    TABLE(Icons.Filled.Casino, Icons.Outlined.Casino, "nav_table_tab"),
    ANALYTICS(Icons.Filled.Analytics, Icons.Outlined.Analytics, "nav_analytics_tab"),
    PLAYERS(Icons.Filled.Group, Icons.Outlined.Group, "nav_players_tab")
}

class MainActivity : ComponentActivity() {
    private val viewModel: PokerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsState()
            val isEnglish = (state.settings.language == "en")
            val layoutDirection = if (isEnglish) LayoutDirection.Ltr else LayoutDirection.Rtl
            val appStrings = getAppStrings(state.settings.language)

            MyApplicationTheme {
                CompositionLocalProvider(
                    LocalLayoutDirection provides layoutDirection,
                    LocalAppStrings provides appStrings
                ) {
                    PokerApp(viewModel = viewModel)

                    if (state.isFirstLaunchLanguagePromptPending) {
                        InitialLanguageDialog(
                            onSelectLanguage = { selectedLang ->
                                viewModel.selectInitialLanguage(selectedLang)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PokerApp(
    viewModel: PokerViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var currentTab by rememberSaveable { mutableStateOf(PokerTab.TABLE) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = GoldLight,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                PokerTab.entries.forEach { tab ->
                    val isSelected = (currentTab == tab)
                    val tabTitle = when (tab) {
                        PokerTab.TABLE -> strings.navTable
                        PokerTab.ANALYTICS -> strings.navAnalytics
                        PokerTab.PLAYERS -> strings.navPlayers
                    }
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tabTitle
                            )
                        },
                        label = {
                            Text(
                                text = tabTitle,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldPrimary,
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                PokerTab.TABLE -> TableScreen(viewModel = viewModel)
                PokerTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                PokerTab.PLAYERS -> PlayersScreen(viewModel = viewModel)
            }
        }
    }
}
