package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.BusinessProfile
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    currentProfile: BusinessProfile,
    onSaveProfile: (BusinessProfile) -> Unit,
    onResetDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(currentProfile) { mutableStateOf(currentProfile.name) }
    var industry by remember(currentProfile) { mutableStateOf(currentProfile.industry) }
    var businessType by remember(currentProfile) { mutableStateOf(currentProfile.businessType) }
    var monthlyRevenueText by remember(currentProfile) { mutableStateOf(currentProfile.monthlyRevenue.toInt().toString()) }
    var monthlyExpensesText by remember(currentProfile) { mutableStateOf(currentProfile.monthlyExpenses.toInt().toString()) }
    var productCategory by remember(currentProfile) { mutableStateOf(currentProfile.productCategory) }
    var customerCountText by remember(currentProfile) { mutableStateOf(currentProfile.customerCount.toString()) }
    var marketingBudgetText by remember(currentProfile) { mutableStateOf(currentProfile.marketingBudget.toInt().toString()) }
    var businessGoal by remember(currentProfile) { mutableStateOf(currentProfile.businessGoal) }
    var targetCustomer by remember(currentProfile) { mutableStateOf(currentProfile.targetCustomer) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successNotification by remember { mutableStateOf(false) }

    // Live preview values
    val parsedRevenue = monthlyRevenueText.toDoubleOrNull() ?: 0.0
    val parsedExpenses = monthlyExpensesText.toDoubleOrNull() ?: 0.0
    val parsedCustomers = customerCountText.toIntOrNull() ?: 0
    val parsedMarketing = marketingBudgetText.toDoubleOrNull() ?: 0.0
    val liveProfit = parsedRevenue - parsedExpenses
    val liveMargin = if (parsedRevenue > 0) (liveProfit / parsedRevenue) * 100 else 0.0

    val industryOptions = listOf(
        "E-Commerce & Digital Retail",
        "SaaS & B2B Software",
        "Professional Services & Consulting",
        "Hospitality & Food Services",
        "Healthcare & Wellness",
        "Manufacturing & Wholesale",
        "Creator & Digital Media"
    )
    var industryExpanded by remember { mutableStateOf(false) }

    val businessTypeOptions = listOf("B2B", "B2C", "D2C", "Hybrid (B2B & B2C)", "Marketplace")
    var typeExpanded by remember { mutableStateOf(false) }

    fun validateAndSave() {
        if (name.isBlank()) {
            errorMessage = "Business Name cannot be empty."
            return
        }
        if (parsedRevenue < 0 || parsedExpenses < 0) {
            errorMessage = "Revenue and Expenses must be positive numbers."
            return
        }
        if (parsedCustomers < 0) {
            errorMessage = "Customer count cannot be negative."
            return
        }
        if (businessGoal.isBlank()) {
            errorMessage = "Please enter a core business goal."
            return
        }

        errorMessage = null
        val updated = currentProfile.copy(
            name = name.trim(),
            industry = industry.trim(),
            businessType = businessType.trim(),
            monthlyRevenue = parsedRevenue,
            monthlyExpenses = parsedExpenses,
            productCategory = productCategory.trim(),
            customerCount = parsedCustomers,
            marketingBudget = parsedMarketing,
            businessGoal = businessGoal.trim(),
            targetCustomer = targetCustomer.trim()
        )
        onSaveProfile(updated)
        successNotification = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("business_profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Health & Financial Metric Strip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Live Financial Projection Preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Monthly Net Profit", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "$${String.format("%,.0f", liveProfit)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (liveProfit >= 0) SuccessGreen else MaterialTheme.colorScheme.error
                        )
                    }
                    Column {
                        Text("Profit Margin", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "${String.format("%.1f", liveMargin)}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text("ARPU", style = MaterialTheme.typography.labelSmall)
                        val arpu = if (parsedCustomers > 0) parsedRevenue / parsedCustomers else 0.0
                        Text(
                            text = "$${String.format("%.0f", arpu)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        if (successNotification) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Profile and strategic intelligence synced successfully!", color = SuccessGreen, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Form Fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Enterprise Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Business Name *") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_business_name_input")
                )

                // Industry Dropdown
                ExposedDropdownMenuBox(
                    expanded = industryExpanded,
                    onExpandedChange = { industryExpanded = !industryExpanded }
                ) {
                    OutlinedTextField(
                        value = industry,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Industry *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = industryExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = industryExpanded,
                        onDismissRequest = { industryExpanded = false }
                    ) {
                        industryOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    industry = opt
                                    industryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Business Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = businessType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Business Model *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        businessTypeOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    businessType = opt
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = productCategory,
                    onValueChange = { productCategory = it },
                    label = { Text("Primary Product / Service Category") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                Text(
                    text = "Financial Metrics & Operations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = monthlyRevenueText,
                        onValueChange = { monthlyRevenueText = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Monthly Revenue ($) *") },
                        leadingIcon = { Text("$", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_revenue_input")
                    )

                    OutlinedTextField(
                        value = monthlyExpensesText,
                        onValueChange = { monthlyExpensesText = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Monthly Expenses ($) *") },
                        leadingIcon = { Text("$", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_expenses_input")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = customerCountText,
                        onValueChange = { customerCountText = it.filter { char -> char.isDigit() } },
                        label = { Text("Customer Count *") },
                        leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_customers_input")
                    )

                    OutlinedTextField(
                        value = marketingBudgetText,
                        onValueChange = { marketingBudgetText = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Marketing Budget ($)") },
                        leadingIcon = { Text("$", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_marketing_input")
                    )
                }

                HorizontalDivider()

                Text(
                    text = "Strategic Direction",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = businessGoal,
                    onValueChange = { businessGoal = it },
                    label = { Text("Primary Business Goal *") },
                    leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) },
                    placeholder = { Text("e.g. Scale net margins to 35% & expand B2B client base") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_goal_input")
                )

                OutlinedTextField(
                    value = targetCustomer,
                    onValueChange = { targetCustomer = it },
                    label = { Text("Target Customer Profile") },
                    leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                    placeholder = { Text("e.g. Mid-sized remote tech teams and IT procurement leads") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Actions
                Button(
                    onClick = { validateAndSave() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("profile_save_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Recalculate AI Strategy", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onResetDemo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_reset_demo_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset to Apex Retail Demo Profile")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
