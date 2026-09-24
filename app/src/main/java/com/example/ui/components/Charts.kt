package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinancialDataPoint
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun LineTrendChart(
    title: String,
    dataPoints: List<FinancialDataPoint>,
    valueExtractor: (FinancialDataPoint) -> Double,
    valueFormatter: (Double) -> String = { "$${String.format("%,.0f", it)}" },
    lineColor: Color = BluePrimary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val latest = dataPoints.lastOrNull()?.let { valueExtractor(it) } ?: 0.0
                Text(
                    text = valueFormatter(latest),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = lineColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (dataPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trend data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val values = dataPoints.map { valueExtractor(it) }
                val maxVal = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
                val minVal = (values.minOrNull() ?: 0.0).coerceAtLeast(0.0)
                val range = (maxVal - minVal).coerceAtLeast(1.0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height - 30.dp.toPx() // Reserve space for x labels
                        val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

                        // Draw 3 horizontal grid lines
                        val gridPaint = Color.LightGray.copy(alpha = 0.25f)
                        for (i in 0..3) {
                            val y = height * (i / 3f)
                            drawLine(
                                color = gridPaint,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val points = values.mapIndexed { index, v ->
                            val norm = ((v - minVal) / range).toFloat()
                            val x = index * stepX
                            val y = height - (norm * (height * 0.85f))
                            Offset(x, y)
                        }

                        // Draw gradient fill below line
                        if (points.size >= 2) {
                            val fillPath = Path().apply {
                                moveTo(points.first().x, height)
                                lineTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    val p0 = points[i - 1]
                                    val p1 = points[i]
                                    val controlX = (p0.x + p1.x) / 2
                                    cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                                }
                                lineTo(points.last().x, height)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor.copy(alpha = 0.35f),
                                        lineColor.copy(alpha = 0.0f)
                                    ),
                                    startY = 0f,
                                    endY = height
                                )
                            )

                            // Draw smooth bezier curve line
                            val strokePath = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    val p0 = points[i - 1]
                                    val p1 = points[i]
                                    val controlX = (p0.x + p1.x) / 2
                                    cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                                }
                            }
                            drawPath(
                                path = strokePath,
                                color = lineColor,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )

                            // Draw data dots
                            points.forEach { pt ->
                                drawCircle(
                                    color = Color.White,
                                    radius = 4.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = lineColor,
                                    radius = 2.5.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }
                }

                // X-Axis labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dataPoints.forEach { pt ->
                        Text(
                            text = pt.period,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonBarChart(
    title: String,
    dataPoints: List<FinancialDataPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(BluePrimary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Revenue", style = MaterialTheme.typography.labelSmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(WarningAmber)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Expenses", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val maxVal = dataPoints.maxOfOrNull { maxOf(it.revenue, it.expenses) }?.coerceAtLeast(1.0) ?: 1.0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 24.dp.toPx()
                    val groupWidth = width / dataPoints.size.coerceAtLeast(1)
                    val barWidth = (groupWidth * 0.28f).coerceAtMost(24.dp.toPx())

                    // Baseline grid
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(0f, height),
                        end = Offset(width, height),
                        strokeWidth = 1.dp.toPx()
                    )

                    dataPoints.forEachIndexed { i, pt ->
                        val groupCenterX = (i * groupWidth) + (groupWidth / 2)

                        // Revenue Bar
                        val revHeight = ((pt.revenue / maxVal) * height).toFloat()
                        val revX = groupCenterX - barWidth - 2.dp.toPx()
                        val revY = height - revHeight
                        drawRoundRect(
                            color = BluePrimary,
                            topLeft = Offset(revX, revY),
                            size = Size(barWidth, revHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Expense Bar
                        val expHeight = ((pt.expenses / maxVal) * height).toFloat()
                        val expX = groupCenterX + 2.dp.toPx()
                        val expY = height - expHeight
                        drawRoundRect(
                            color = WarningAmber,
                            topLeft = Offset(expX, expY),
                            size = Size(barWidth, expHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dataPoints.forEach { pt ->
                    Text(
                        text = pt.period,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
