package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.UserSession
import kotlinx.coroutines.launch

data class NavigationDrawerEntry(
    val screen: AppScreen,
    val icon: ImageVector,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationScaffold(
    currentScreen: AppScreen,
    userSession: UserSession,
    onNavigate: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        NavigationDrawerEntry(AppScreen.DASHBOARD, Icons.Default.Dashboard),
        NavigationDrawerEntry(AppScreen.BUSINESS_PROFILE, Icons.Default.Business),
        NavigationDrawerEntry(AppScreen.AI_CONSULTANT, Icons.Default.Psychology, "AI"),
        NavigationDrawerEntry(AppScreen.ANALYTICS, Icons.Default.BarChart),
        NavigationDrawerEntry(AppScreen.SALES_PREDICTION, Icons.AutoMirrored.Filled.TrendingUp),
        NavigationDrawerEntry(AppScreen.SWOT_ANALYSIS, Icons.Default.GridView),
        NavigationDrawerEntry(AppScreen.RECOMMENDATIONS, Icons.Default.Lightbulb, "New"),
        NavigationDrawerEntry(AppScreen.QUESTION_INPUT, Icons.Default.QuestionAnswer),
        NavigationDrawerEntry(AppScreen.REPORTS, Icons.Default.Assessment),
        NavigationDrawerEntry(AppScreen.SETTINGS, Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(300.dp)
                    .testTag("nav_drawer_sheet"),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AI Business Consultant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = if (userSession.company.isNotBlank()) userSession.company else "Apex Retail & Tech Solutions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Navigation Items
                drawerItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.screen.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        badge = if (item.badge != null) {
                            {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (item.badge == "AI") BluePrimary else SuccessGreen
                                ) {
                                    Text(
                                        text = item.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        } else null,
                        selected = isSelected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(item.screen)
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                            .testTag("nav_item_${item.screen.route}")
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Bottom Drawer Logout
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    label = { Text("Log Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("drawer_logout_button")
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentScreen.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Apex Strategy Portal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("menu_drawer_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if (currentScreen != AppScreen.AI_CONSULTANT) {
                            IconButton(
                                onClick = { onNavigate(AppScreen.AI_CONSULTANT) },
                                modifier = Modifier.testTag("topbar_ai_consultant_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "AI Consultant",
                                    tint = BluePrimary
                                )
                            }
                        }
                        IconButton(
                            onClick = { onNavigate(AppScreen.SETTINGS) },
                            modifier = Modifier.testTag("topbar_settings_button")
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    val bottomItems = listOf(
                        Triple(AppScreen.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
                        Triple(AppScreen.AI_CONSULTANT, Icons.Default.Psychology, "Advisor"),
                        Triple(AppScreen.ANALYTICS, Icons.Default.BarChart, "Analytics"),
                        Triple(AppScreen.RECOMMENDATIONS, Icons.Default.Lightbulb, "Actions"),
                        Triple(AppScreen.SETTINGS, Icons.Default.Settings, "Settings")
                    )

                    bottomItems.forEach { (screen, icon, label) ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick = { onNavigate(screen) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("bottom_nav_${screen.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}
