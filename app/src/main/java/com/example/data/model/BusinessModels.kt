package com.example.data.model

data class BusinessProfile(
    val id: String = "profile_1",
    val name: String = "Apex Retail & Tech Solutions",
    val industry: String = "E-Commerce & Digital Retail",
    val businessType: String = "Hybrid (B2B & B2C)",
    val monthlyRevenue: Double = 48500.0,
    val monthlyExpenses: Double = 31200.0,
    val productCategory: String = "Smart Workstation & Tech Accessories",
    val customerCount: Int = 1240,
    val marketingBudget: Double = 6500.0,
    val businessGoal: String = "Increase Profit Margin to 40% & Expand B2B Pipeline",
    val targetCustomer: String = "Mid-sized remote tech teams and modern hybrid professionals"
) {
    val netProfit: Double
        get() = monthlyRevenue - monthlyExpenses

    val profitMarginPercentage: Double
        get() = if (monthlyRevenue > 0) (netProfit / monthlyRevenue) * 100.0 else 0.0

    val averageRevenuePerCustomer: Double
        get() = if (customerCount > 0) monthlyRevenue / customerCount else 0.0

    val estimatedCac: Double
        get() = if (customerCount > 0) marketingBudget / (customerCount * 0.15).coerceAtLeast(1.0) else 0.0

    val estimatedLtv: Double
        get() = averageRevenuePerCustomer * 7.5

    val healthScore: Int
        get() {
            var score = 60
            if (profitMarginPercentage > 25.0) score += 20 else if (profitMarginPercentage > 15.0) score += 10
            if (netProfit > 0) score += 10
            if (customerCount > 500) score += 10
            return score.coerceIn(10, 98)
        }
}

data class FinancialDataPoint(
    val period: String,
    val sales: Double,
    val revenue: Double,
    val expenses: Double,
    val profit: Double,
    val customerCount: Int
)

enum class MessageSender {
    USER, AI
}

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFallback: Boolean = false,
    val categoryTag: String? = null
)

enum class RecommendationCategory(val displayName: String) {
    SALES("Sales"),
    MARKETING("Marketing"),
    CUSTOMER("Customer"),
    FINANCIAL("Financial"),
    INVENTORY("Inventory"),
    GROWTH("Growth"),
    RISK("Risk")
}

data class Recommendation(
    val id: String,
    val category: RecommendationCategory,
    val title: String,
    val description: String,
    val impactLevel: String, // "Critical", "High", "Medium", "Quick Win"
    val estimatedRoi: String,
    val actionSteps: List<String>,
    val isCompleted: Boolean = false
)

data class SwotItem(
    val id: String,
    val title: String,
    val description: String
)

data class SwotAnalysis(
    val strengths: List<SwotItem>,
    val weaknesses: List<SwotItem>,
    val opportunities: List<SwotItem>,
    val threats: List<SwotItem>
)

data class SalesForecast(
    val currentSales: Double,
    val predictedSales3M: Double,
    val predictedSales6M: Double,
    val predictedSales12M: Double,
    val growthPercentage: Double,
    val predictionSummary: String,
    val keyDrivers: List<String>,
    val confidenceRange: String,
    val projectedTrend: List<FinancialDataPoint>
)

data class BusinessQnAResult(
    val question: String,
    val directAnswer: String,
    val explanation: String,
    val recommendedActions: List<String>,
    val businessImpact: String,
    val isRelevant: Boolean = true
)
