package com.fmdev.civilacademy.presentation.screen.login.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fmdev.civilacademy.presentation.screen.component.LoadingButton
import com.fmdev.civilacademy.presentation.screen.component.LoadingButtonColors
import com.fmdev.civilacademy.presentation.screen.component.LoadingButtonContent
import com.fmdev.civilacademy.presentation.screen.component.LoadingButtonLambdas
import com.fmdev.civilacademy.presentation.screen.login.model.UiResult
import com.fmdev.civilacademy.presentation.theme.ErrorLight
import com.fmdev.civilacademy.presentation.theme.Gray200
import com.fmdev.civilacademy.presentation.theme.Gray300
import com.fmdev.civilacademy.presentation.theme.Primary

@Composable
fun LoginLoadingButton(
    modifier: Modifier,
    loadingState: UiResult,
    onclick: () -> Unit,
    onAnimationComplete: () -> Unit,
    isDarkTheme: Boolean,
    text: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    paddingHorizontal: Dp = 16.dp,
    enabled: Boolean = true,
    textComponent: @Composable (() -> Unit)? = null
) {
    LoadingButton(
        enabled = enabled,
        state = loadingState,
        lambdas = (
                LoadingButtonLambdas(
                    onClick = { onclick() },
                    onAnimationComplete = onAnimationComplete
                )
        ),
        content = LoadingButtonContent(
            text = text,
            modifier = modifier.border(
                if (!isDarkTheme && loadingState is UiResult.Success)
                    BorderStroke(2.dp, Color.Black)
                else BorderStroke(width = 0.dp, color = Color.Transparent),
                shape = RoundedCornerShape(13.dp)
            ),
            colors = LoadingButtonColors(
                idleBackgroundColor = if (isDarkTheme) Primary else Color.Black,
                successBackgroundColor = Primary,
                errorBackgroundColor = ErrorLight,
                loadingIndicatorColor = if (isDarkTheme) Color.Black else Gray200,
                errorIndicatorColor = Color.Black,
                successIndicatorColor = Color.Black,
            ),
            textComponent = textComponent ?: {
                Text(
                    text = text,
                    fontSize = if (isDarkTheme) 20.sp else 25.sp,
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        !enabled -> Gray300
                        isDarkTheme -> Color.Black
                        else -> Gray200
                    }
                )
            },
            leadingIcon = leadingIcon,
            roundedCornerShape = 13.dp,
            paddingHorizontal = paddingHorizontal,
        )
    )
}