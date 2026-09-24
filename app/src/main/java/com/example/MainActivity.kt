package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppNavigationScaffold
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BusinessViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BusinessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val userSession by viewModel.userSession.collectAsStateWithLifecycle()
                val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
                val recommendations by viewModel.recommendations.collectAsStateWithLifecycle()
                val swotAnalysis by viewModel.swotAnalysis.collectAsStateWithLifecycle()
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
                val salesForecast by viewModel.salesForecast.collectAsStateWithLifecycle()
                val marketingMult by viewModel.marketingMultiplier.collectAsStateWithLifecycle()
                val conversionBoost by viewModel.conversionBoost.collectAsStateWithLifecycle()
                val questionResult by viewModel.questionResult.collectAsStateWithLifecycle()
                val isAnalyzingQuestion by viewModel.isAnalyzingQuestion.collectAsStateWithLifecycle()
                val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(toastMessage) {
                    toastMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.dismissToast()
                    }
                }

                // If not logged in or on LOGIN screen, show LoginScreen
                if (!userSession.isLoggedIn || currentScreen == AppScreen.LOGIN) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoginScreen(
                            onLoginSuccess = { email, name ->
                                viewModel.login(email, name)
                            },
                            onDemoLogin = {
                                viewModel.demoLogin()
                            }
                        )
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        )
                    }
                } else {
                    // Back handler: navigate back to Dashboard if on a sub-screen
                    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
                        viewModel.navigateTo(AppScreen.DASHBOARD)
                    }

                    AppNavigationScaffold(
                        currentScreen = currentScreen,
                        userSession = userSession,
                        onNavigate = { viewModel.navigateTo(it) },
                        onLogout = { viewModel.logout() }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                AppScreen.DASHBOARD -> {
                                    DashboardScreen(
                                        profile = profile,
                                        trends = viewModel.getHistoricalTrends(),
                                        recommendations = recommendations,
                                        onNavigate = { viewModel.navigateTo(it) },
                                        onToggleRecommendation = { viewModel.toggleRecommendation(it) }
                                    )
                                }
                                AppScreen.BUSINESS_PROFILE -> {
                                    BusinessProfileScreen(
                                        currentProfile = profile,
                                        onSaveProfile = { viewModel.updateBusinessProfile(it) },
                                        onResetDemo = { viewModel.resetProfileToDemo() }
                                    )
                                }
                                AppScreen.AI_CONSULTANT -> {
                                    ConsultantChatScreen(
                                        messages = chatMessages,
                                        isAiThinking = isAiThinking,
                                        onSendMessage = { viewModel.sendConsultantMessage(it) },
                                        onClearChat = { viewModel.clearChat() }
                                    )
                                }
                                AppScreen.ANALYTICS -> {
                                    AnalyticsScreen(
                                        profile = profile,
                                        trends = viewModel.getHistoricalTrends()
                                    )
                                }
                                AppScreen.SALES_PREDICTION -> {
                                    SalesPredictionScreen(
                                        forecast = salesForecast,
                                        marketingMultiplier = marketingMult,
                                        conversionBoost = conversionBoost,
                                        onSlidersChanged = { m, c ->
                                            viewModel.updatePredictionSliders(m, c)
                                        }
                                    )
                                }
                                AppScreen.SWOT_ANALYSIS -> {
                                    SwotScreen(
                                        profile = profile,
                                        swot = swotAnalysis,
                                        onRegenerate = { viewModel.updateBusinessProfile(profile) }
                                    )
                                }
                                AppScreen.RECOMMENDATIONS -> {
                                    RecommendationsScreen(
                                        recommendations = recommendations,
                                        onToggleCompleted = { viewModel.toggleRecommendation(it) }
                                    )
                                }
                                AppScreen.QUESTION_INPUT -> {
                                    QuestionDeepDiveScreen(
                                        profile = profile,
                                        result = questionResult,
                                        isAnalyzing = isAnalyzingQuestion,
                                        onSubmitQuestion = { q, notes ->
                                            viewModel.submitStructuredQuestion(q, notes)
                                        }
                                    )
                                }
                                AppScreen.REPORTS -> {
                                    ReportsScreen(
                                        profile = profile,
                                        swot = swotAnalysis,
                                        recommendations = recommendations
                                    )
                                }
                                AppScreen.SETTINGS -> {
                                    SettingsScreen(
                                        userSession = userSession,
                                        profile = profile,
                                        onResetDemoData = { viewModel.resetProfileToDemo() },
                                        onClearChatHistory = { viewModel.clearChat() },
                                        onLogout = { viewModel.logout() }
                                    )
                                }
                                AppScreen.LOGIN -> {
                                    // Handled above
                                }
                            }

                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
