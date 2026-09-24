package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.SalesForecast
import com.example.ui.components.LineTrendChart
import com.example.ui.components.MetricCard
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun SalesPredictionScreen(
    forecast: SalesForecast,
    marketingMultiplier: Float,
    conversionBoost: Float,
    onSlidersChanged: (marketingMultiplier: Float, conversionBoost: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var localMarketing by remember(marketingMultiplier) { mutableStateOf(marketingMultiplier) }
    var localConversion by remember(conversionBoost) { mutableStateOf(conversionBoost) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("sales_prediction_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Predictive Sales & Revenue Forecast",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Simulated predictive modeling engine projecting revenue expansion based on marketing capital elasticity and conversion lift.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // KPI Highlights
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Current Monthly Sales",
                value = "$${String.format("%,.0f", forecast.currentSales)}",
                subtitle = "Baseline run-rate",
                icon = Icons.Default.Storefront,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Predicted Sales (12M)",
                value = "$${String.format("%,.0f", forecast.predictedSales12M)}",
                deltaText = "${String.format("%.1f", forecast.growthPercentage)}%",
                isPositiveDelta = forecast.growthPercentage >= 0,
                subtitle = "Projected horizon",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        // Milestone cards (3M, 6M, 12M)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Forecast Milestones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("3 Months", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "$${String.format("%,.0f", forecast.predictedSales3M)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                        Text("+8.0%", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("6 Months", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "$${String.format("%,.0f", forecast.predictedSales6M)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BlueSecondary
                        )
                        Text("+19.0%", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("12 Months", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "$${String.format("%,.0f", forecast.predictedSales12M)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text("+${String.format("%.1f", forecast.growthPercentage)}%", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                    }
                }
            }
        }

        // Projected Trend Chart
        LineTrendChart(
            title = "Projected Sales Trajectory",
            dataPoints = forecast.projectedTrend,
            valueExtractor = { it.sales },
            lineColor = SuccessGreen
        )

        // Interactive Simulation Scenario Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scenario Simulator Controls",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        localMarketing = 1.0f
                        localConversion = 0.0f
                        onSlidersChanged(1.0f, 0.0f)
                    }) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset Sliders")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Slider 1: Marketing Multiplier
                Text(
                    text = "Marketing Spend Scale: ${String.format("%.1f", localMarketing)}x",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = localMarketing,
                    onValueChange = {
                        localMarketing = it
                        onSlidersChanged(localMarketing, localConversion)
                    },
                    valueRange = 0.5f..2.5f,
                    steps = 8,
                    modifier = Modifier.testTag("marketing_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Slider 2: Conversion Boost
                Text(
                    text = "Conversion Optimization Lift: +${String.format("%.0f", localConversion)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = localConversion,
                    onValueChange = {
                        localConversion = it
                        onSlidersChanged(localMarketing, localConversion)
                    },
                    valueRange = 0.0f..50.0f,
                    steps = 10,
                    modifier = Modifier.testTag("conversion_slider")
                )
            }
        }

        // Prediction Summary & Drivers
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Executive Prediction Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = forecast.predictionSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Key Growth Model Drivers:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                forecast.keyDrivers.forEach { driver ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = driver,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Confidence interval
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Model Confidence Range: ${forecast.confidenceRange}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Transparent Disclaimer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Predictive Model Notice: Projections are computed based on mathematical elasticity curves, historical momentum, and input scenario parameters. They serve for executive scenario planning and are not guaranteed real-world commitments.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
