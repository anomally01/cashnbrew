package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AuthManager
import com.example.ui.theme.CaramelPrimary
import com.example.ui.theme.CaramelPrimaryContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OnSurfaceWarm
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.OutlineWarm
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceDark

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showManagerDialog by remember { mutableStateOf(false) }

    val handleDigitClick: (String) -> Unit = { digit ->
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            errorMessage = null

            if (newPin.length == 4) {
                val success = AuthManager.loginWithPin(newPin)
                if (success) {
                    onLoginSuccess()
                } else {
                    errorMessage = "Invalid Staff PIN. (Try 1234)"
                    enteredPin = ""
                }
            }
        }
    }

    val handleBackspace: () -> Unit = {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        // Top Hero Background Area with Moody Coffee Beans
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAANXtHDWPteIa0xiPn9DFXlhgv2WvXFU4qe3HE9sKtHS3za2R7FgWBMQa94B2HmWHlGSgU2CrFqXTt01mmwlNuZjbULTh3ccap1HuJvI0aE8EbhXFnR7L0xMcwEeYMZLeIX2z82W06vRNBjvNvx7ZmRYwfq_t9er5ksUjoi2IU9iNvxVTOqXxjQVzuLgSFCHXIKZO_DOTprG7uvH16lNE6UHXasls4dwwqmZUAk78kkcJyqTtEuMI7CQ",
                contentDescription = "Moody Roasted Coffee Beans",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay for smooth transition into SurfaceDark
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                SurfaceDark.copy(alpha = 0.5f),
                                SurfaceDark
                            )
                        )
                    )
            )

            // Brand Header centered over the fade
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gold Brand Icon
                Icon(
                    imageVector = Icons.Filled.Coffee,
                    contentDescription = "Logo",
                    tint = CaramelPrimary,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cash and Brew",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = OnSurfaceWarm
                )
                Text(
                    text = "Staff Terminal Access",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        // Bottom PIN Pad & Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // PIN Dots Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (isFilled) CaramelPrimaryContainer else SurfaceContainerHigh)
                            .border(
                                width = 1.dp,
                                color = if (isFilled) CaramelPrimary else OutlineVariant,
                                shape = CircleShape
                            )
                            .then(
                                if (isFilled) Modifier.shadow(8.dp, CircleShape, spotColor = CaramelPrimary)
                                else Modifier
                            )
                    )
                }
            }

            // Error feedback
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = ErrorRed,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (errorMessage == null) {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 3x4 PIN Pad Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PinButton(digit = "1", onClick = { handleDigitClick("1") })
                    PinButton(digit = "2", onClick = { handleDigitClick("2") })
                    PinButton(digit = "3", onClick = { handleDigitClick("3") })
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PinButton(digit = "4", onClick = { handleDigitClick("4") })
                    PinButton(digit = "5", onClick = { handleDigitClick("5") })
                    PinButton(digit = "6", onClick = { handleDigitClick("6") })
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PinButton(digit = "7", onClick = { handleDigitClick("7") })
                    PinButton(digit = "8", onClick = { handleDigitClick("8") })
                    PinButton(digit = "9", onClick = { handleDigitClick("9") })
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Empty space on bottom-left for alignment
                    Box(modifier = Modifier.size(64.dp))
                    PinButton(digit = "0", onClick = { handleDigitClick("0") })
                    // Backspace button
                    IconButton(
                        onClick = handleBackspace,
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("pin_backspace")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Backspace",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Manager Key Alternate Login Button
            OutlinedButton(
                onClick = { showManagerDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("manager_login_button"),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, OutlineWarm)
            ) {
                Text(
                    text = "Sign in with Manager Key",
                    color = OnSurfaceWarm,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    // Manager Credentials Dialog
    if (showManagerDialog) {
        var username by remember { mutableStateOf("admin") }
        var password by remember { mutableStateOf("123456") }
        var dialogError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showManagerDialog = false },
            containerColor = SurfaceContainer,
            title = {
                Text(
                    text = "Manager Authorization",
                    color = OnSurfaceWarm,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enter manager credentials to unlock the terminal.",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CaramelPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CaramelPrimary,
                            unfocusedBorderColor = OutlineVariant,
                            focusedTextColor = OnSurfaceWarm,
                            unfocusedTextColor = OnSurfaceWarm
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CaramelPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CaramelPrimary,
                            unfocusedBorderColor = OutlineVariant,
                            focusedTextColor = OnSurfaceWarm,
                            unfocusedTextColor = OnSurfaceWarm
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (dialogError != null) {
                        Text(
                            text = dialogError ?: "",
                            color = ErrorRed,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = AuthManager.login(username, password)
                        if (success) {
                            showManagerDialog = false
                            onLoginSuccess()
                        } else {
                            dialogError = "Invalid credentials. Use admin / 123456"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CaramelPrimaryContainer,
                        contentColor = OnPrimaryContainer
                    ),
                    modifier = Modifier.testTag("submit_manager_login")
                ) {
                    Text("Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManagerDialog = false }) {
                    Text("Cancel", color = OnSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun PinButton(
    digit: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(SurfaceContainer)
            .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CaramelPrimary)
            ) { onClick() }
            .testTag("pin_button_$digit"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = OnSurfaceWarm
        )
    }
}
