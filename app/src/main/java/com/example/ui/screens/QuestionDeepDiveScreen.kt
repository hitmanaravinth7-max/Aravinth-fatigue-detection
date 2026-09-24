package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.data.model.BusinessQnAResult
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun QuestionDeepDiveScreen(
    profile: BusinessProfile,
    result: BusinessQnAResult?,
    isAnalyzing: Boolean,
    onSubmitQuestion: (question: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var questionText by remember { mutableStateOf("How can we increase sales while lowering our marketing customer acquisition cost?") }
    var contextNotes by remember { mutableStateOf("Targeting B2B teams in ${profile.industry}") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("question_deep_dive_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Strategic Question & Deep-Dive",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Submit any specific operational or commercial challenge to receive a 4-pillar structured executive briefing.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter Strategic Inquiry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Business Question *") },
                    placeholder = { Text("e.g. How can I increase my sales?") },
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deepdive_question_input")
                )

                OutlinedTextField(
                    value = contextNotes,
                    onValueChange = { contextNotes = it },
                    label = { Text("Optional Context or Current Constraints") },
                    placeholder = { Text("e.g. Current marketing budget is tight, focus on organic.") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onSubmitQuestion(questionText, contextNotes) },
                    enabled = questionText.isNotBlank() && !isAnalyzing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("deepdive_submit_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing Executive Output...")
                    } else {
                        Icon(Icons.Default.Analytics, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate 4-Pillar Strategic Brief")
                    }
                }
            }
        }

        // Structured 4-Pillar Output Card
        if (result != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (result.isRelevant) SuccessGreen.copy(alpha = 0.15f) else WarningAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (result.isRelevant) "Strategic Brief Ready" else "Commercial Scope Notice",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (result.isRelevant) SuccessGreen else WarningAmber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val formatted = """
                                INQUIRY: ${result.question}
                                
                                1. DIRECT ANSWER:
                                ${result.directAnswer}
                                
                                2. EXPLANATION:
                                ${result.explanation}
                                
                                3. RECOMMENDED ACTIONS:
                                ${result.recommendedActions.joinToString("\n• ")}
                                
                                4. BUSINESS IMPACT:
                                ${result.businessImpact}
                            """.trimIndent()
                            clipboard.setPrimaryClip(ClipData.newPlainText("Executive Brief", formatted))
                        }) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy Output", tint = BluePrimary)
                        }
                    }

                    // Section 1: Direct Answer
                    PillarItem(
                        number = "1",
                        title = "Direct Answer",
                        content = result.directAnswer,
                        badgeColor = BluePrimary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Section 2: Explanation
                    PillarItem(
                        number = "2",
                        title = "Strategic Explanation & Context",
                        content = result.explanation,
                        badgeColor = MaterialTheme.colorScheme.secondary
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Section 3: Recommended Actions
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PillarBadge(number = "3", color = SuccessGreen)
                            Text(
                                text = "Recommended Immediate & Strategic Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        result.recommendedActions.forEach { action ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Section 4: Expected Business Impact
                    PillarItem(
                        number = "4",
                        title = "Expected Business Impact",
                        content = result.businessImpact,
                        badgeColor = WarningAmber
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PillarBadge(number: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = "Part $number",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun PillarItem(
    number: String,
    title: String,
    content: String,
    badgeColor: androidx.compose.ui.graphics.Color
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PillarBadge(number = number, color = badgeColor)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}
