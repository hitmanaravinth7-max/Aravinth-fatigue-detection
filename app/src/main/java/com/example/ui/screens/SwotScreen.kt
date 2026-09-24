package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.data.model.SwotAnalysis
import com.example.data.model.SwotItem
import com.example.ui.theme.DangerRed
import com.example.ui.theme.InfoCyan
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun SwotScreen(
    profile: BusinessProfile,
    swot: SwotAnalysis,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Strength") }
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }

    val strengthsList = remember(swot) { mutableStateListOf(*swot.strengths.toTypedArray()) }
    val weaknessesList = remember(swot) { mutableStateListOf(*swot.weaknesses.toTypedArray()) }
    val opportunitiesList = remember(swot) { mutableStateListOf(*swot.opportunities.toTypedArray()) }
    val threatsList = remember(swot) { mutableStateListOf(*swot.threats.toTypedArray()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("swot_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Automated SWOT Matrix",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Strategic quadrant breakdown for ${profile.name}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onRegenerate) {
                Icon(Icons.Default.Refresh, contentDescription = "Regenerate SWOT", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Action row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Strategic Note")
            }

            OutlinedButton(
                onClick = onRegenerate,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI Re-Analyze")
            }
        }

        // Quadrant 1: Strengths
        SwotQuadrantCard(
            title = "STRENGTHS (Internal)",
            icon = Icons.Default.CheckCircle,
            themeColor = SuccessGreen,
            items = strengthsList
        )

        // Quadrant 2: Weaknesses
        SwotQuadrantCard(
            title = "WEAKNESSES (Internal)",
            icon = Icons.Default.Warning,
            themeColor = WarningAmber,
            items = weaknessesList
        )

        // Quadrant 3: Opportunities
        SwotQuadrantCard(
            title = "OPPORTUNITIES (External)",
            icon = Icons.Default.Lightbulb,
            themeColor = InfoCyan,
            items = opportunitiesList
        )

        // Quadrant 4: Threats
        SwotQuadrantCard(
            title = "THREATS (External)",
            icon = Icons.Default.Security,
            themeColor = DangerRed,
            items = threatsList
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Custom SWOT Insight") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Strength", "Weakness", "Opportunity", "Threat").forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.take(4), style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Insight Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Details & Description") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val item = SwotItem("custom_${System.currentTimeMillis()}", newTitle.trim(), newDesc.trim())
                            when (selectedCategory) {
                                "Strength" -> strengthsList.add(item)
                                "Weakness" -> weaknessesList.add(item)
                                "Opportunity" -> opportunitiesList.add(item)
                                "Threat" -> threatsList.add(item)
                            }
                            newTitle = ""
                            newDesc = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SwotQuadrantCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    themeColor: Color,
    items: List<SwotItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(themeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            items.forEachIndexed { idx, item ->
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        text = "${idx + 1}. ${item.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
                if (idx < items.size - 1) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
