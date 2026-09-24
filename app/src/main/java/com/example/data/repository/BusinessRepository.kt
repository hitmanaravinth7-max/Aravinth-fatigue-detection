package com.example.data.repository

import com.example.data.model.*
import com.example.data.service.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BusinessRepository(
    private val geminiService: GeminiService = GeminiService()
) {

    private val defaultProfile = BusinessProfile(
        id = "demo_profile_1",
        name = "Apex Retail & Tech Solutions",
        industry = "E-Commerce & Digital Retail",
        businessType = "Hybrid (B2B & B2C)",
        monthlyRevenue = 48500.0,
        monthlyExpenses = 31200.0,
        productCategory = "Smart Workstation & Tech Accessories",
        customerCount = 1240,
        marketingBudget = 6500.0,
        businessGoal = "Increase Net Profit Margin to 40% & Scale B2B Pipeline",
        targetCustomer = "Remote tech professionals, hybrid offices, and IT procurement leads"
    )

    private val _businessProfile = MutableStateFlow(defaultProfile)
    val businessProfile: StateFlow<BusinessProfile> = _businessProfile.asStateFlow()

    private val _recommendations = MutableStateFlow<List<Recommendation>>(emptyList())
    val recommendations: StateFlow<List<Recommendation>> = _recommendations.asStateFlow()

    private val _swotAnalysis = MutableStateFlow(generateSwotFromProfile(defaultProfile))
    val swotAnalysis: StateFlow<SwotAnalysis> = _swotAnalysis.asStateFlow()

    init {
        refreshAllIntelligence(defaultProfile)
    }

    fun updateProfile(updated: BusinessProfile) {
        _businessProfile.value = updated
        refreshAllIntelligence(updated)
    }

    fun resetToDemoProfile() {
        updateProfile(defaultProfile)
    }

    private fun refreshAllIntelligence(profile: BusinessProfile) {
        _recommendations.value = generateRecommendations(profile)
        _swotAnalysis.value = generateSwotFromProfile(profile)
    }

    fun toggleRecommendationCompleted(recId: String) {
        _recommendations.value = _recommendations.value.map {
            if (it.id == recId) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    suspend fun askConsultant(prompt: String): String {
        return geminiService.consult(prompt, _businessProfile.value)
    }

    fun getHistoricalTrends(profile: BusinessProfile): List<FinancialDataPoint> {
        val rev = profile.monthlyRevenue
        val exp = profile.monthlyExpenses
        val cust = profile.customerCount

        return listOf(
            FinancialDataPoint("Apr", rev * 0.72, rev * 0.76, exp * 0.88, (rev * 0.76) - (exp * 0.88), (cust * 0.75).toInt()),
            FinancialDataPoint("May", rev * 0.79, rev * 0.82, exp * 0.90, (rev * 0.82) - (exp * 0.90), (cust * 0.81).toInt()),
            FinancialDataPoint("Jun", rev * 0.85, rev * 0.89, exp * 0.92, (rev * 0.89) - (exp * 0.92), (cust * 0.88).toInt()),
            FinancialDataPoint("Jul", rev * 0.91, rev * 0.94, exp * 0.95, (rev * 0.94) - (exp * 0.95), (cust * 0.92).toInt()),
            FinancialDataPoint("Aug", rev * 0.95, rev * 0.97, exp * 0.98, (rev * 0.97) - (exp * 0.98), (cust * 0.96).toInt()),
            FinancialDataPoint("Sep (Current)", rev * 0.98, rev, exp, rev - exp, cust)
        )
    }

    fun calculatePrediction(
        marketingMultiplier: Float = 1.0f,
        conversionBoostPercentage: Float = 0.0f
    ): SalesForecast {
        val profile = _businessProfile.value
        val baseMonthly = profile.monthlyRevenue
        val growthFactor = 1.0 + ((marketingMultiplier - 1.0) * 0.22) + (conversionBoostPercentage / 100.0 * 0.35)

        val pred3M = baseMonthly * (1.0 + (0.08 * growthFactor))
        val pred6M = baseMonthly * (1.0 + (0.19 * growthFactor))
        val pred12M = baseMonthly * (1.0 + (0.38 * growthFactor))
        val netGrowthPct = ((pred12M - baseMonthly) / baseMonthly.coerceAtLeast(1.0)) * 100.0

        val drivers = listOf(
            "Marketing spend allocation: ${String.format("%.1fx", marketingMultiplier)} baseline",
            "Conversion rate uplift parameter: +${String.format("%.1f", conversionBoostPercentage)}%",
            "Seasonal trajectory in ${profile.industry}",
            "Customer expansion in ${profile.businessType} segment"
        )

        val trendPoints = listOf(
            FinancialDataPoint("Current", baseMonthly, baseMonthly, profile.monthlyExpenses, baseMonthly - profile.monthlyExpenses, profile.customerCount),
            FinancialDataPoint("M+3", pred3M, pred3M, profile.monthlyExpenses * 1.04, pred3M - (profile.monthlyExpenses * 1.04), (profile.customerCount * 1.08).toInt()),
            FinancialDataPoint("M+6", pred6M, pred6M, profile.monthlyExpenses * 1.09, pred6M - (profile.monthlyExpenses * 1.09), (profile.customerCount * 1.18).toInt()),
            FinancialDataPoint("M+12", pred12M, pred12M, profile.monthlyExpenses * 1.16, pred12M - (profile.monthlyExpenses * 1.16), (profile.customerCount * 1.34).toInt())
        )

        return SalesForecast(
            currentSales = baseMonthly,
            predictedSales3M = pred3M,
            predictedSales6M = pred6M,
            predictedSales12M = pred12M,
            growthPercentage = netGrowthPct,
            predictionSummary = "Simulated predictive model forecasts annual sales scaling from $${String.format("%,.0f", baseMonthly)} to $${String.format("%,.0f", pred12M)} (+${String.format("%.1f", netGrowthPct)}% growth). Note: Forecast is calculated via linear momentum and elasticity simulation for strategic scenario testing.",
            keyDrivers = drivers,
            confidenceRange = "$${String.format("%,.0f", pred12M * 0.92)} — $${String.format("%,.0f", pred12M * 1.08)} (±8% confidence interval)",
            projectedTrend = trendPoints
        )
    }

    suspend fun analyzeBusinessQuestion(question: String, notes: String = ""): BusinessQnAResult {
        val trimmed = question.trim()
        val lower = trimmed.lowercase()
        val profile = _businessProfile.value

        // Check if question is business related
        val businessKeywords = listOf(
            "sale", "sales", "revenue", "profit", "expense", "cost", "marketing", "customer", "churn",
            "retention", "price", "pricing", "margin", "product", "inventory", "b2b", "b2c", "market",
            "competitor", "business", "growth", "strategy", "brand", "hire", "employee", "team", "cash",
            "roi", "cac", "ltv", "conversion", "supplier", "vendor", "ecommerce", "retail", "expand", "risk"
        )
        val isBusinessRelated = businessKeywords.any { lower.contains(it) }

        if (!isBusinessRelated && trimmed.length > 5) {
            return BusinessQnAResult(
                question = trimmed,
                directAnswer = "This question appears outside the scope of commercial business operations.",
                explanation = "The AI-Powered Business Consultant specializes exclusively in enterprise strategy, revenue optimization, customer acquisition, financial health, marketing economics, and operational efficiency.",
                recommendedActions = listOf(
                    "Please submit inquiries related to your sales pipeline, cost reduction, pricing models, or growth strategies.",
                    "Review the Dashboard or AI Recommendations for tailored prompts regarding ${profile.industry}."
                ),
                businessImpact = "Focusing strategic inquiries on commercial leverage points guarantees maximum return on executive time.",
                isRelevant = false
            )
        }

        // Generate high-quality structured response
        val directAnswer: String
        val explanation: String
        val actions: List<String>
        val impact: String

        when {
            lower.contains("sale") || lower.contains("increase") || lower.contains("grow") -> {
                directAnswer = "Implement high-intent account retargeting and introduce curated product bundling to lift Average Order Value (AOV) by 18-24%."
                explanation = "With ${profile.name}'s current monthly run-rate of $${String.format("%,.0f", profile.monthlyRevenue)}, acquiring net-new customers is typically 5x to 7x more expensive than increasing transaction density within your existing base of ${String.format("%,d", profile.customerCount)} clients."
                actions = listOf(
                    "Launch automated post-purchase cross-sell bundles for ${profile.productCategory} within 48 hours of primary checkout.",
                    "Set up an outbound B2B pilot offering 15% tiered volume licensing for corporate teams.",
                    "Audit website sales funnel drop-offs on mobile checkouts to eliminate payment friction."
                )
                impact = "Estimated incremental monthly revenue of +$${String.format("%,.0f", profile.monthlyRevenue * 0.22)} within 60 days."
            }
            lower.contains("expense") || lower.contains("cost") || lower.contains("reduce") -> {
                directAnswer = "Execute an immediate zero-based operational audit targeting SaaS subscriptions, duplicate vendor tools, and low-ROAS advertising."
                explanation = "At $${String.format("%,.0f", profile.monthlyExpenses)} monthly operating expense, operational overhead consumes ${String.format("%.1f", (profile.monthlyExpenses / profile.monthlyRevenue.coerceAtLeast(1.0)) * 100)}% of top-line revenue, which dampens reinvestment speed."
                actions = listOf(
                    "Audit software licenses and cancel recurring seats unused for >30 days.",
                    "Renegotiate supplier payment terms from Net-30 to Net-60 to preserve cash reserves.",
                    "Reallocate 30% of paid ad budget into organic SEO and referral mechanisms."
                )
                impact = "Monthly cash savings of $${String.format("%,.0f", profile.monthlyExpenses * 0.15)}, immediately boosting bottom-line profit margins by +4.8%."
            }
            lower.contains("marketing") || lower.contains("ad") -> {
                directAnswer = "Concentrate 70% of your $${String.format("%,.0f", profile.marketingBudget)} marketing budget on high-converting search and B2B channels, reserving 30% for audience retargeting."
                explanation = "Broad awareness campaigns in ${profile.industry} suffer from high bounce rates. Direct-intent marketing targeted at ${profile.targetCustomer} provides verifiable customer acquisition economics."
                actions = listOf(
                    "Create 3 customer case studies proving time-to-value for ${profile.productCategory}.",
                    "Implement remarketing pixels with a 14-day recency window for warm website visitors.",
                    "Test a 10% discount first-order incentive paired with email newsletter capture."
                )
                impact = "Expected CAC reduction from ~$${String.format("%.0f", profile.estimatedCac)} to ~$${String.format("%.0f", profile.estimatedCac * 0.78)} with a 3.8x blended ROAS."
            }
            lower.contains("retention") || lower.contains("churn") || lower.contains("customer") -> {
                directAnswer = "Establish a proactive onboarding check-in sequence and launch an executive loyalty tier for top accounts."
                explanation = "Your active customer count of ${profile.customerCount} represents substantial dormant equity. Reducing churn by just 5% can dramatically expand profitability over 12 months."
                actions = listOf(
                    "Send automated Day 3 and Day 14 satisfaction check-ins with direct support access.",
                    "Implement a quarterly VIP rebate or priority support desk for repeat accounts.",
                    "Conduct exit interviews for departing customers to eliminate root-cause churn vectors."
                )
                impact = "Anticipated 28% reduction in quarterly customer churn and an increase in lifetime value (LTV) from $${String.format("%.0f", profile.estimatedLtv)} to $${String.format("%.0f", profile.estimatedLtv * 1.25)}."
            }
            else -> {
                directAnswer = "Align execution milestones around your primary goal: '${profile.businessGoal}' while protecting current net profit margin of ${String.format("%.1f", profile.profitMarginPercentage)}%."
                explanation = "Sustainable business consulting mandates balancing aggressive top-line growth with disciplined bottom-line unit economics. For ${profile.name}, strategic focus and unit profitability take precedence over uncoordinated expansion."
                actions = listOf(
                    "Establish weekly KPI tracking for top-line revenue, blended CAC, and customer satisfaction.",
                    "Standardize operational SOPs to allow delegation without quality degradation.",
                    "Schedule bi-weekly reviews with key department leads to track goal progression."
                )
                impact = "Ensures steady trajectory toward corporate goal milestones while avoiding cash flow bottlenecks."
            }
        }

        return BusinessQnAResult(
            question = trimmed,
            directAnswer = directAnswer,
            explanation = explanation,
            recommendedActions = actions,
            businessImpact = impact,
            isRelevant = true
        )
    }

    private fun generateRecommendations(profile: BusinessProfile): List<Recommendation> {
        val list = mutableListOf<Recommendation>()

        // 1. Sales
        list.add(
            Recommendation(
                id = "rec_sales_1",
                category = RecommendationCategory.SALES,
                title = "Launch B2B Bundling for ${profile.productCategory}",
                description = "Create bundled packages with tiered corporate discounts to incentivize bulk buying from ${profile.targetCustomer}.",
                impactLevel = "High",
                estimatedRoi = "+18% to +24% AOV",
                actionSteps = listOf(
                    "Package core products with complementary accessory kits",
                    "Add dedicated B2B 'Request Invoice' checkout option",
                    "Offer 12% discount on orders containing 3+ bundled units"
                )
            )
        )
        list.add(
            Recommendation(
                id = "rec_sales_2",
                category = RecommendationCategory.SALES,
                title = "Automated Cart Recovery & SMS Follow-up",
                description = "Recover estimated 14% of abandoned shopping sessions through sequenced email and SMS reminders.",
                impactLevel = "Quick Win",
                estimatedRoi = "+$3,200/mo Revenue",
                actionSteps = listOf(
                    "Send initial friendly reminder within 45 minutes of abandon",
                    "Include 5% expiring discount code on 24-hour follow up",
                    "Enable 1-tap checkout links in recovery messages"
                )
            )
        )

        // 2. Marketing
        list.add(
            Recommendation(
                id = "rec_mkt_1",
                category = RecommendationCategory.MARKETING,
                title = "Reallocate Paid Ad Budget Toward High-Intent Search",
                description = "Shift 40% of social media ad spend to high-intent Google search keywords specifically targeting commercial buyers.",
                impactLevel = "High",
                estimatedRoi = "3.9x Target ROAS",
                actionSteps = listOf(
                    "Pause underperforming broad interest social ad campaigns",
                    "Target long-tail purchase intent terms in ${profile.industry}",
                    "Deploy dedicated landing pages with customer testimonials"
                )
            )
        )
        list.add(
            Recommendation(
                id = "rec_mkt_2",
                category = RecommendationCategory.MARKETING,
                title = "Customer Referral Incentive Program",
                description = "Reward current ${profile.customerCount} customers with a $25 store credit for each verified friend or colleague introduced.",
                impactLevel = "Medium",
                estimatedRoi = "70% Lower CAC",
                actionSteps = listOf(
                    "Generate unique referral links for all registered accounts",
                    "Promote program on post-purchase confirmation screens",
                    "Automate credit distribution upon successful referral order"
                )
            )
        )

        // 3. Customer
        list.add(
            Recommendation(
                id = "rec_cust_1",
                category = RecommendationCategory.CUSTOMER,
                title = "Proactive 14-Day Onboarding Sequence",
                description = "Improve product adoption and lower early return rates by delivering tailored how-to guides and warranty tips.",
                impactLevel = "High",
                estimatedRoi = "32% Churn Reduction",
                actionSteps = listOf(
                    "Trigger Day-2 welcome email with setup instructions",
                    "Send Day-7 best practices email highlighting key features",
                    "Send Day-14 check-in offering priority support assistance"
                )
            )
        )

        // 4. Financial
        list.add(
            Recommendation(
                id = "rec_fin_1",
                category = RecommendationCategory.FINANCIAL,
                title = "Renegotiate Vendor Payment Terms to Net-60",
                description = "Extend supplier and manufacturing invoice horizons to maintain stronger liquid cash reserves.",
                impactLevel = "Critical",
                estimatedRoi = "+$14,000 Liquid Buffer",
                actionSteps = listOf(
                    "Review top 3 supplier agreements by order volume",
                    "Leverage timely payment history to request Net-60 terms",
                    "Utilize freed working capital to capture early bulk supply discounts"
                )
            )
        )
        list.add(
            Recommendation(
                id = "rec_fin_2",
                category = RecommendationCategory.FINANCIAL,
                title = "Implement 5% Premium Pricing Adjustment",
                description = "A modest 5% adjustment across premium product lines will directly elevate net profit without reducing sales volume.",
                impactLevel = "Quick Win",
                estimatedRoi = "+$${String.format("%,.0f", profile.monthlyRevenue * 0.05)} Pure Profit",
                actionSteps = listOf(
                    "Benchmark current rates against top 3 market competitors",
                    "Apply price updates to premium tiers first",
                    "Monitor 14-day conversion rate elasticity"
                )
            )
        )

        // 5. Inventory
        list.add(
            Recommendation(
                id = "rec_inv_1",
                category = RecommendationCategory.INVENTORY,
                title = "Automated Just-in-Time (JIT) Stock Reordering",
                description = "Set dynamic safety stock thresholds to prevent stockouts while reducing warehouse holding capital by 22%.",
                impactLevel = "Medium",
                estimatedRoi = "22% Holding Cost Cut",
                actionSteps = listOf(
                    "Classify inventory into ABC categories based on turnover velocity",
                    "Automate reorder trigger points based on 10-day lead times",
                    "Liquidate dead stock exceeding 90 days of shelf age"
                )
            )
        )

        // 6. Growth
        list.add(
            Recommendation(
                id = "rec_gro_1",
                category = RecommendationCategory.GROWTH,
                title = "Launch Corporate Procurement Portal",
                description = "Build a lightweight wholesale and bulk order portal enabling corporate accounts to order directly with PO billing.",
                impactLevel = "Critical",
                estimatedRoi = "+35% B2B Revenue",
                actionSteps = listOf(
                    "Create dedicated B2B signup and tax exemption form",
                    "Offer tiered pricing tables for orders above 25 units",
                    "Assign a dedicated account manager contact email"
                )
            )
        )

        // 7. Risk
        list.add(
            Recommendation(
                id = "rec_risk_1",
                category = RecommendationCategory.RISK,
                title = "Diversify Primary Supplier Dependency",
                description = "Onboard a secondary backup supplier for core components in ${profile.productCategory} to mitigate supply chain disruptions.",
                impactLevel = "Critical",
                estimatedRoi = "Zero Downtime Guarantee",
                actionSteps = listOf(
                    "Request sample shipments and compliance audits from secondary vendor",
                    "Allocate 15% of monthly procurement to maintain active account status",
                    "Establish standardized quality acceptance criteria"
                )
            )
        )

        return list
    }

    private fun generateSwotFromProfile(profile: BusinessProfile): SwotAnalysis {
        val margin = profile.profitMarginPercentage
        val cust = profile.customerCount

        val strengths = listOf(
            SwotItem("s1", "Solid Profit Margin (${String.format("%.1f", margin)}%)", "Current net profit of $${String.format("%,.0f", profile.netProfit)} affords healthy capital retention and self-funded growth."),
            SwotItem("s2", "Established Customer Community", "Active base of ${String.format("%,d", cust)} clients provides stable recurring order momentum and testimonials."),
            SwotItem("s3", "Hybrid Model Flexibility", "${profile.businessType} structure captures both immediate retail margins and high-volume corporate contracts.")
        )

        val weaknesses = listOf(
            SwotItem("w1", "High Operating Expense Ratio", "Operating expenses represent ${String.format("%.1f", (profile.monthlyExpenses / profile.monthlyRevenue.coerceAtLeast(1.0)) * 100)}% of top-line revenue, requiring ongoing expense governance."),
            SwotItem("w2", "Marketing Channel Concentration", "Heavy reliance on digital advertising ($${String.format("%,.0f", profile.marketingBudget)}/mo) creates vulnerability to ad platform algorithm changes."),
            SwotItem("w3", "Category Dependency", "Narrow concentration in ${profile.productCategory} limits basket diversity for non-tech segments.")
        )

        val opportunities = listOf(
            SwotItem("o1", "B2B Corporate Expansion", "Growing adoption of hybrid work opens corporate procurement budgets for ${profile.targetCustomer}."),
            SwotItem("o2", "Subscription & Maintenance Services", "Introducing recurring product warranties or automated replenishment could stabilize seasonal fluctuations."),
            SwotItem("o3", "Direct Partner Affiliates", "Co-marketing with software companies serving the same tech demographic provides zero-CAC lead generation.")
        )

        val threats = listOf(
            SwotItem("t1", "Customer Acquisition Ad Inflation", "Rising competition in ${profile.industry} continues to drive up digital auction costs and CAC."),
            SwotItem("t2", "Global Supply & Freight Volatility", "Cross-border component shipping costs and transit delays can impact delivery turnaround."),
            SwotItem("t3", "Competitor Margin Compression", "Aggressive low-cost marketplace alternatives attempting to undercut pricing on ${profile.productCategory}.")
        )

        return SwotAnalysis(strengths, weaknesses, opportunities, threats)
    }
}
