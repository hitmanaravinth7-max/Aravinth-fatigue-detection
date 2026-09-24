package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.BusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen(val title: String, val route: String) {
    LOGIN("Login", "login"),
    DASHBOARD("Dashboard", "dashboard"),
    BUSINESS_PROFILE("Business Profile", "profile"),
    AI_CONSULTANT("AI Consultant", "consultant"),
    ANALYTICS("Business Analytics", "analytics"),
    SALES_PREDICTION("Sales Prediction", "prediction"),
    SWOT_ANALYSIS("SWOT Analysis", "swot"),
    RECOMMENDATIONS("AI Recommendations", "recommendations"),
    QUESTION_INPUT("Business Q&A", "qna"),
    REPORTS("Executive Reports", "reports"),
    SETTINGS("Settings", "settings")
}

data class UserSession(
    val isLoggedIn: Boolean = false,
    val email: String = "",
    val name: String = "",
    val company: String = ""
)

class BusinessViewModel(
    private val repository: BusinessRepository = BusinessRepository()
) : ViewModel() {

    val businessProfile: StateFlow<BusinessProfile> = repository.businessProfile
    val recommendations: StateFlow<List<Recommendation>> = repository.recommendations
    val swotAnalysis: StateFlow<SwotAnalysis> = repository.swotAnalysis

    private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _userSession = MutableStateFlow(UserSession())
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    // Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_msg",
                sender = MessageSender.AI,
                message = "Welcome to your AI-Powered Business Consultant. I have analyzed your business profile for Apex Retail & Tech Solutions.\n\nAsk me anything regarding sales optimization, cost reduction, marketing channels, customer retention, or strategic risks.",
                categoryTag = "Overview"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Sales Prediction Simulation
    private val _marketingMultiplier = MutableStateFlow(1.0f)
    val marketingMultiplier: StateFlow<Float> = _marketingMultiplier.asStateFlow()

    private val _conversionBoost = MutableStateFlow(0.0f)
    val conversionBoost: StateFlow<Float> = _conversionBoost.asStateFlow()

    private val _salesForecast = MutableStateFlow(repository.calculatePrediction(1.0f, 0.0f))
    val salesForecast: StateFlow<SalesForecast> = _salesForecast.asStateFlow()

    // Structured Question Deep Dive
    private val _questionResult = MutableStateFlow<BusinessQnAResult?>(null)
    val questionResult: StateFlow<BusinessQnAResult?> = _questionResult.asStateFlow()

    private val _isAnalyzingQuestion = MutableStateFlow(false)
    val isAnalyzingQuestion: StateFlow<Boolean> = _isAnalyzingQuestion.asStateFlow()

    // UI Feedback
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun dismissToast() {
        _toastMessage.value = null
    }

    fun login(email: String, name: String = "Aravinth Kumar") {
        _userSession.value = UserSession(
            isLoggedIn = true,
            email = email,
            name = name,
            company = businessProfile.value.name
        )
        _currentScreen.value = AppScreen.DASHBOARD
        _toastMessage.value = "Welcome back, $name!"
    }

    fun demoLogin() {
        login("executive@consultant-demo.com", "Demo Executive")
    }

    fun logout() {
        _userSession.value = UserSession()
        _currentScreen.value = AppScreen.LOGIN
        _toastMessage.value = "Successfully logged out."
    }

    fun updateBusinessProfile(updated: BusinessProfile) {
        repository.updateProfile(updated)
        // Refresh forecast
        _salesForecast.value = repository.calculatePrediction(_marketingMultiplier.value, _conversionBoost.value)
        _toastMessage.value = "Business profile updated successfully!"
    }

    fun resetProfileToDemo() {
        repository.resetToDemoProfile()
        _marketingMultiplier.value = 1.0f
        _conversionBoost.value = 0.0f
        _salesForecast.value = repository.calculatePrediction(1.0f, 0.0f)
        _toastMessage.value = "Reset to standard demo enterprise profile."
    }

    fun toggleRecommendation(id: String) {
        repository.toggleRecommendationCompleted(id)
    }

    fun sendConsultantMessage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.USER,
            message = trimmed
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            try {
                val responseText = repository.askConsultant(trimmed)
                val aiMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = MessageSender.AI,
                    message = responseText
                )
                _chatMessages.value = _chatMessages.value + aiMsg
            } catch (e: Exception) {
                val fallbackMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = MessageSender.AI,
                    message = "I have reviewed your inquiry regarding '$trimmed'. Based on current operational margins of ${String.format("%.1f", businessProfile.value.profitMarginPercentage)}%, focus on high-impact customer retention and strict vendor cost control.",
                    isFallback = true
                )
                _chatMessages.value = _chatMessages.value + fallbackMsg
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.AI,
                message = "Conversation history cleared. How can I assist ${businessProfile.value.name} today?"
            )
        )
        _toastMessage.value = "Chat history cleared."
    }

    fun updatePredictionSliders(marketingMult: Float, conversionPct: Float) {
        _marketingMultiplier.value = marketingMult
        _conversionBoost.value = conversionPct
        _salesForecast.value = repository.calculatePrediction(marketingMult, conversionPct)
    }

    fun submitStructuredQuestion(question: String, notes: String) {
        val trimmed = question.trim()
        if (trimmed.isBlank()) {
            _toastMessage.value = "Please enter a business question to analyze."
            return
        }

        _isAnalyzingQuestion.value = true
        viewModelScope.launch {
            try {
                val result = repository.analyzeBusinessQuestion(trimmed, notes)
                _questionResult.value = result
            } catch (e: Exception) {
                _questionResult.value = BusinessQnAResult(
                    question = trimmed,
                    directAnswer = "Prioritize unit economics and customer retention.",
                    explanation = "A balanced approach between customer acquisition and cash reserve management protects against market volatility.",
                    recommendedActions = listOf(
                        "Audit recurring operational costs",
                        "Optimize marketing spend to preserve positive ROI",
                        "Survey core customer satisfaction"
                    ),
                    businessImpact = "Guarantees sustained cash flow runway and healthy operating margins."
                )
            } finally {
                _isAnalyzingQuestion.value = false
            }
        }
    }

    fun getHistoricalTrends(): List<FinancialDataPoint> {
        return repository.getHistoricalTrends(businessProfile.value)
    }
}
