package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.Recommendation
import com.example.ui.components.ComparisonBarChart
import com.example.ui.components.LineTrendChart
import com.example.ui.components.MetricCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun DashboardScreen(
    profile: BusinessProfile,
    trends: List<FinancialDataPoint>,
    recommendations: List<Recommendation>,
    onNavigate: (AppScreen) -> Unit,
    onToggleRecommendation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTrendPeriod by remember { mutableStateOf("6M") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Business Identity Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${profile.industry} • ${profile.businessType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    // Health Score Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Text(
                                text = "Score: ${profile.healthScore}/100",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Goal: ${profile.businessGoal}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Consultation Trigger
                Button(
                    onClick = { onNavigate(AppScreen.AI_CONSULTANT) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("dashboard_ask_ai_button")
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consult AI Business Advisor", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Top 5 Key Performance Metrics Grid
        Text(
            text = "Executive Key Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Sales",
                value = "$${String.format("%,.0f", profile.monthlyRevenue * 2.8)}",
                deltaText = "14.2%",
                isPositiveDelta = true,
                subtitle = "Quarterly cumulative",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconColor = BluePrimary,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Monthly Revenue",
                value = "$${String.format("%,.0f", profile.monthlyRevenue)}",
                deltaText = "8.6%",
                isPositiveDelta = true,
                subtitle = "vs last month",
                icon = Icons.Default.AttachMoney,
                iconColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Monthly Expenses",
                value = "$${String.format("%,.0f", profile.monthlyExpenses)}",
                deltaText = "3.1%",
                isPositiveDelta = false,
                subtitle = "${String.format("%.0f", (profile.monthlyExpenses / profile.monthlyRevenue.coerceAtLeast(1.0)) * 100)}% of revenue",
                icon = Icons.Default.CreditCard,
                iconColor = WarningAmber,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Net Profit",
                value = "$${String.format("%,.0f", profile.netProfit)}",
                deltaText = "${String.format("%.1f", profile.profitMarginPercentage)}%",
                isPositiveDelta = profile.netProfit >= 0,
                subtitle = "Operating margin",
                icon = Icons.Default.AccountBalance,
                iconColor = if (profile.netProfit >= 0) SuccessGreen else DangerRed,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Active Customers",
                value = String.format("%,d", profile.customerCount),
                deltaText = "12.4%",
                isPositiveDelta = true,
                subtitle = "ARPU: $${String.format("%.0f", profile.averageRevenuePerCustomer)}",
                icon = Icons.Default.People,
                iconColor = InfoCyan,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Marketing Budget",
                value = "$${String.format("%,.0f", profile.marketingBudget)}",
                deltaText = "CAC ~$${String.format("%.0f", profile.estimatedCac)}",
                isPositiveDelta = true,
                subtitle = "LTV ~$${String.format("%.0f", profile.estimatedLtv)}",
                icon = Icons.Default.Campaign,
                iconColor = BlueTertiary,
                modifier = Modifier.weight(1f)
            )
        }

        // Sales Trend Chart with Period Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sales & Revenue Trajectory",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("1M", "6M", "1Y").forEach { period ->
                    FilterChip(
                        selected = selectedTrendPeriod == period,
                        onClick = { selectedTrendPeriod = period },
                        label = { Text(period, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }

        LineTrendChart(
            title = "Revenue Momentum",
            dataPoints = trends,
            valueExtractor = { it.revenue },
            lineColor = BluePrimary
        )

        // Revenue vs Expenses Comparison
        ComparisonBarChart(
            title = "Revenue vs. Expense History",
            dataPoints = trends
        )

        // Business Performance Summary & Diagnostic
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Business Performance Diagnostic",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SuccessGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (profile.profitMarginPercentage > 20) "HEALTHY" else "OPTIMIZATION NEEDED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (profile.profitMarginPercentage > 20) SuccessGreen else WarningAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "• Profitability: Generating $${String.format("%,.0f", profile.netProfit)} monthly with a healthy ${String.format("%.1f", profile.profitMarginPercentage)}% net margin.\n" +
                            "• Efficiency: Customer acquisition cost (CAC) is ~$${String.format("%.0f", profile.estimatedCac)} against an estimated LTV of ~$${String.format("%.0f", profile.estimatedLtv)} (healthy 3.2x ratio).\n" +
                            "• Key Priority: Optimize marketing channel allocation and lower vendor expense overhead to hit your goal: '${profile.businessGoal}'.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigate(AppScreen.SWOT_ANALYSIS) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View SWOT")
                    }
                    Button(
                        onClick = { onNavigate(AppScreen.SALES_PREDICTION) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sales Forecast")
                    }
                }
            }
        }

        // Recent Recommendations Preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent AI Strategic Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { onNavigate(AppScreen.RECOMMENDATIONS) }) {
                Text("View All (${recommendations.size})")
            }
        }

        recommendations.take(3).forEach { rec ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleRecommendation(rec.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (rec.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Checkbox(
                        checked = rec.isCompleted,
                        onCheckedChange = { onToggleRecommendation(rec.id) }
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = rec.category.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "ROI: ${rec.estimatedRoi}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = rec.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (rec.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
