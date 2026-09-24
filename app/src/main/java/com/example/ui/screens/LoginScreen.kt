package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, name: String) -> Unit,
    onDemoLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("founder@apexretail.io") }
    var password by remember { mutableStateOf("Apex2026!Secure") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isCreateAccountMode by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("Aravinth Kumar") }
    var companyName by remember { mutableStateOf("Apex Retail & Tech Solutions") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordSuccess by remember { mutableStateOf(false) }

    fun validateAndSubmit() {
        var isValid = true
        if (email.isBlank()) {
            emailError = "Email address cannot be empty."
            isValid = false
        } else if (!email.contains("@") || !email.contains(".")) {
            emailError = "Please enter a valid business email."
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "Password cannot be empty."
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters."
            isValid = false
        } else {
            passwordError = null
        }

        if (isValid) {
            val displayName = if (isCreateAccountMode && fullName.isNotBlank()) fullName else "Aravinth Kumar"
            onLoginSuccess(email.trim(), displayName)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo & Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(BluePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Logo",
                    tint = BluePrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AI-Powered Business Consultant",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (isCreateAccountMode) "Register your enterprise account" else "Executive Portal & Strategy Suite",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Authentication Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (isCreateAccountMode) "Create Account" else "Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isCreateAccountMode) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_fullname_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Company Name") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_company_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError != null) emailError = null
                        },
                        label = { Text("Business Email") },
                        leadingIcon = { Icon(Icons.Outlined.Mail, contentDescription = null) },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(emailError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordError != null) {
                                Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    // Forgot Password link
                    if (!isCreateAccountMode) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(
                                onClick = {
                                    forgotPasswordEmail = email
                                    forgotPasswordSuccess = false
                                    showForgotPasswordDialog = true
                                },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text("Forgot Password?", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Submit Button
                    Button(
                        onClick = { validateAndSubmit() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text(
                            text = if (isCreateAccountMode) "Create Enterprise Account" else "Log In to Dashboard",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1-Click Demo Login
                    OutlinedButton(
                        onClick = onDemoLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("demo_login_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "One-Tap Demo Login (No Setup Needed)",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle Create Account / Login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCreateAccountMode) "Already have an account?" else "Don't have an account?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = {
                                isCreateAccountMode = !isCreateAccountMode
                                emailError = null
                                passwordError = null
                            },
                            modifier = Modifier.testTag("toggle_auth_mode_button")
                        ) {
                            Text(
                                text = if (isCreateAccountMode) "Sign In" else "Create Account",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer note
            Text(
                text = "End-to-End Enterprise Encryption • AI Consultant Engine v3.5",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    if (forgotPasswordSuccess) {
                        Text(
                            "A password reset link and verification code have been dispatched to $forgotPasswordEmail.",
                            color = SuccessGreen,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            "Enter your registered business email and we will send you secure recovery instructions.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = forgotPasswordEmail,
                            onValueChange = { forgotPasswordEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (forgotPasswordSuccess) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Close")
                    }
                } else {
                    Button(
                        onClick = {
                            if (forgotPasswordEmail.isNotBlank()) {
                                forgotPasswordSuccess = true
                            }
                        }
                    ) {
                        Text("Send Reset Link")
                    }
                }
            },
            dismissButton = {
                if (!forgotPasswordSuccess) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
