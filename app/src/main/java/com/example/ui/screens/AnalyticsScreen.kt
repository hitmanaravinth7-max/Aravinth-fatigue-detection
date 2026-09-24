package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.data.model.FinancialDataPoint
import com.example.ui.components.ComparisonBarChart
import com.example.ui.components.LineTrendChart
import com.example.ui.components.MetricCard
import com.example.ui.theme.*

@Composable
fun AnalyticsScreen(
    profile: BusinessProfile,
    trends: List<FinancialDataPoint>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Sales & Revenue", "Expenses", "Profitability", "Marketing & Growth")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Business Intelligence & Analytics",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Comprehensive analytics engine tracking commercial unit economics for ${profile.name}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Tab Selector
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Sales & Revenue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Monthly Run-Rate",
                        value = "$${String.format("%,.0f", profile.monthlyRevenue)}",
                        deltaText = "8.4%",
                        isPositiveDelta = true,
                        subtitle = "Gross billings",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Annual Forecast",
                        value = "$${String.format("%,.0f", profile.monthlyRevenue * 12)}",
                        deltaText = "14.2%",
                        isPositiveDelta = true,
                        subtitle = "ARR benchmark",
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )
                }

                LineTrendChart(
                    title = "Monthly Revenue Growth",
                    dataPoints = trends,
                    valueExtractor = { it.revenue },
                    lineColor = BluePrimary
                )

                LineTrendChart(
                    title = "Monthly Sales Volume",
                    dataPoints = trends,
                    valueExtractor = { it.sales },
                    lineColor = BlueSecondary
                )
            }

            1 -> {
                // Tab 1: Expenses Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Total OpEx",
                        value = "$${String.format("%,.0f", profile.monthlyExpenses)}",
                        deltaText = "${String.format("%.1f", (profile.monthlyExpenses / profile.monthlyRevenue.coerceAtLeast(1.0)) * 100)}%",
                        isPositiveDelta = false,
                        subtitle = "Of gross revenue",
                        icon = Icons.Default.CreditCard,
                        iconColor = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Marketing Allocation",
                        value = "$${String.format("%,.0f", profile.marketingBudget)}",
                        deltaText = "${String.format("%.1f", (profile.marketingBudget / profile.monthlyExpenses.coerceAtLeast(1.0)) * 100)}%",
                        isPositiveDelta = true,
                        subtitle = "Of total expenses",
                        icon = Icons.Default.Campaign,
                        iconColor = InfoCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                ComparisonBarChart(
                    title = "Revenue vs. Expense Variance",
                    dataPoints = trends
                )

                // Expense Categories Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Operating Expense Breakdown by Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        val expenseCategories = listOf(
                            Triple("Payroll & Contractor Compensation", profile.monthlyExpenses * 0.42, BluePrimary),
                            Triple("Direct Marketing & Customer Acquisition", profile.marketingBudget, BlueSecondary),
                            Triple("COGS & Warehouse Logistics", profile.monthlyExpenses * 0.22, WarningAmber),
                            Triple("Cloud Infrastructure & SaaS Tools", profile.monthlyExpenses * 0.10, InfoCyan),
                            Triple("Office, Legal & Administrative", profile.monthlyExpenses * 0.05, Color.Gray)
                        )

                        expenseCategories.forEach { (cat, amount, color) ->
                            val pct = (amount / profile.monthlyExpenses.coerceAtLeast(1.0)) * 100
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cat, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                    Text("$${String.format("%,.0f", amount)} (${String.format("%.1f", pct)}%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (pct / 100f).toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = color,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Tab 2: Profitability
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Operating Profit",
                        value = "$${String.format("%,.0f", profile.netProfit)}",
                        deltaText = "${String.format("%.1f", profile.profitMarginPercentage)}%",
                        isPositiveDelta = profile.netProfit >= 0,
                        subtitle = "Net Margin",
                        icon = Icons.Default.AccountBalance,
                        iconColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Gross Margin",
                        value = "64.8%",
                        deltaText = "2.1%",
                        isPositiveDelta = true,
                        subtitle = "Industry top quartile",
                        icon = Icons.AutoMirrored.Filled.ShowChart,
                        iconColor = BluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                LineTrendChart(
                    title = "Monthly Net Profit Progression",
                    dataPoints = trends,
                    valueExtractor = { it.profit },
                    lineColor = SuccessGreen
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Executive Profitability Assessment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "With a net profit margin of ${String.format("%.1f", profile.profitMarginPercentage)}%, your unit economics are in the upper tier for the ${profile.industry} sector.\n\n" +
                                    "Recommended Next Step: Implementing a 5% value-based price optimization on higher-tier SKUs can expand your bottom line by +$${String.format("%,.0f", profile.monthlyRevenue * 0.05)} monthly without churn impact.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            3 -> {
                // Tab 3: Marketing & Customer Growth
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Customer Base",
                        value = String.format("%,d", profile.customerCount),
                        deltaText = "12.4%",
                        isPositiveDelta = true,
                        subtitle = "Active accounts",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "CAC : LTV",
                        value = "1 : 3.4",
                        deltaText = "Healthy",
                        isPositiveDelta = true,
                        subtitle = "$${String.format("%.0f", profile.estimatedCac)} vs $${String.format("%.0f", profile.estimatedLtv)}",
                        icon = Icons.Default.PieChart,
                        iconColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                LineTrendChart(
                    title = "Customer Acquisition Growth",
                    dataPoints = trends,
                    valueExtractor = { it.customerCount.toDouble() },
                    valueFormatter = { String.format("%,.0f clients", it) },
                    lineColor = InfoCyan
                )

                // Marketing Channel Performance Table Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Marketing Channel Performance Matrix",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val channels = listOf(
                            Triple("Google Search (Commercial Intent)", "3.8x ROAS", "$2,400 Spend • 62 Conv."),
                            Triple("B2B LinkedIn & Outbound Lead Gen", "4.2x ROAS", "$1,800 Spend • 38 Conv."),
                            Triple("Email Marketing & Retention Flows", "7.1x ROAS", "$450 Spend • 110 Conv."),
                            Triple("Social Brand Retargeting", "2.6x ROAS", "$1,250 Spend • 28 Conv."),
                            Triple("Partner Referrals & Affiliates", "5.4x ROAS", "$600 Spend • 34 Conv.")
                        )

                        channels.forEach { (channel, roas, details) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(channel, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text(details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SuccessGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = roas,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
