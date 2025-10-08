package com.fmdev.civilacademy.presentation.screen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fmdev.civilacademy.presentation.R
import com.fmdev.civilacademy.presentation.screen.login.model.UiResult
import com.fmdev.civilacademy.presentation.theme.Gray300
import com.fmdev.civilacademy.presentation.theme.Gray600
import kotlinx.coroutines.delay

/**
 * Displays a button with a loading indicator and success/error animations,
 * managed through classes that group the content configuration and interaction lambdas
 * for a flexible, professional design.
 *
 * @param content Defines the visual and textual configuration of the button, including
 * colors, icons, text, and corner shape.
 * @param state The current button state. Can be [UiResult.Idle], [UiResult.Loading],
 * [UiResult.Success], or [UiResult.Error].
 * @param lambdas Holds the lambdas for click actions (onClick) and the end of the animation
 * (onAnimationComplete).
 *
 * @see LoadingButtonContent for visual configuration, colors, and text.
 * @see LoadingButtonLambdas for the actions associated with the button.
 * @see LoadingButtonColors for customizing button colors.
 */
@Composable
fun LoadingButton(
    content: LoadingButtonContent,
    state: UiResult,
    lambdas: LoadingButtonLambdas,
    maxWidth: Dp = 360.dp,
    enabled: Boolean = true,
    previewProgress: Float? = null
) {
    val checkProgress = remember { Animatable(0f) }
    val crossProgress = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing)),
        label = "rotation"
    )

    LaunchedEffect(state) {
        when (state) {
            is UiResult.Loading -> {
                checkProgress.snapTo(0f)
                crossProgress.snapTo(0f)
            }
            is UiResult.Success -> {
                checkProgress.animateTo(1f, tween(1000, easing = LinearOutSlowInEasing))
                delay(500)
                lambdas.onAnimationComplete()
            }
            is UiResult.Error -> {
                crossProgress.animateTo(1f, tween(1000, easing = LinearOutSlowInEasing))
                delay(500)
                lambdas.onAnimationComplete()
            }
            is UiResult.Idle -> {
                checkProgress.snapTo(0f)
                crossProgress.snapTo(0f)
            }
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (!enabled) content.colors.disabledBackgroundColor
        else when (state) {
            is UiResult.Success -> content.colors.successBackgroundColor
            is UiResult.Error -> content.colors.errorBackgroundColor
            else -> content.colors.idleBackgroundColor
        },
        animationSpec = tween(500),
        label = "backgroundColor"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(maxWidth)
            .height(50.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = content.modifier
                .clip(RoundedCornerShape(content.roundedCornerShape))
                .background(backgroundColor, RoundedCornerShape(content.roundedCornerShape))
                .clickable(
                    enabled = state == UiResult.Idle && enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = content.colors.rippleColor),
                    onClick = lambdas.onClick
                )
                .padding(horizontal = content.paddingHorizontal, vertical = 8.dp)
                .height(41.dp)
        ) {
            when (state) {
                is UiResult.Idle -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    content.leadingIcon?.let {
                        it()
                        Spacer(Modifier.size(8.dp))
                    }
                    content.textComponent()
                }

                is UiResult.Loading -> LoadingIndicator(
                    rotation = rotation,
                    color = content.colors.loadingIndicatorColor
                )

                is UiResult.Success -> CheckMarkAnimation(
                    progress = previewProgress ?: checkProgress.value,
                    color = content.colors.successIndicatorColor,
                    modifier = Modifier.size(24.dp)
                )

                is UiResult.Error -> CrossMarkAnimation(
                    progress = previewProgress ?: crossProgress.value,
                    color = content.colors.errorIndicatorColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CheckMarkAnimation(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (progress <= 1f) {
            val adjustedProgress = if (progress < 0.5f) progress * 2 else 1f
            val path1 = Path().apply {
                moveTo(x = size.width * 0.2f, y = size.height * 0.5f)
                lineTo(
                    x = size.width * 0.2f + (size.width * 0.2f * adjustedProgress),
                    y = size.height * 0.5f + (size.height * 0.2f * adjustedProgress)
                )
            }
            drawPath(
                path = path1,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        if (progress > 0.5f) {
            val adjustedProgress = (progress - 0.5f) * 2
            val path2 = Path().apply {
                moveTo(x = size.width * 0.4f, y = size.height * 0.7f)
                lineTo(
                    x = size.width * 0.4f + (size.width * 0.4f * adjustedProgress),
                    y = size.height * 0.7f - (size.height * 0.4f * adjustedProgress)
                )
            }
            drawPath(
                path = path2,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun CrossMarkAnimation(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (progress <= 1f) {
            val adjustedProgress = if (progress < 0.5f) progress * 2 else 1f
            val path1 = Path().apply {
                moveTo(x = size.width * 0.2f, y = size.height * 0.2f)
                lineTo(
                    x = size.width * 0.2f + (size.width * 0.6f * adjustedProgress),
                    y = size.height * 0.2f + (size.height * 0.6f * adjustedProgress)
                )
            }
            drawPath(
                path = path1,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
        }
        if (progress > 0.5f) {
            val adjustedProgress = (progress - 0.5f) * 2
            val path2 = Path().apply {
                moveTo(x = size.width * 0.2f, y = size.height * 0.8f)
                lineTo(
                    x = size.width * 0.2f + (size.width * 0.6f * adjustedProgress),
                    y = size.height * 0.8f - (size.height * 0.6f * adjustedProgress)
                )
            }
            drawPath(
                path = path2,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun LoadingIndicator(rotation: Float, color: Color) {
    Image(
        painter = painterResource(id = R.drawable.ic_loader),
        contentDescription = "Loading",
        colorFilter = ColorFilter.tint(color),
        modifier = Modifier
            .rotate(rotation)
            .size(24.dp)
    )
}

data class LoadingButtonLambdas(
    val onClick: () -> Unit,
    val onAnimationComplete: () -> Unit
)

data class LoadingButtonContent(
    val text: String,
    val modifier: Modifier = Modifier,
    val roundedCornerShape: Dp = 8.dp,
    val textComponent: @Composable () -> Unit = { Text(text = text, color = Color.White) },
    val leadingIcon: @Composable (() -> Unit)? = null,
    val colors: LoadingButtonColors = LoadingButtonColors(),
    val paddingHorizontal: Dp = 16.dp
)

data class LoadingButtonColors(
    val idleBackgroundColor: Color = Color.Black,
    val successBackgroundColor: Color = Color(0xFF4CAF50), // Green
    val errorBackgroundColor: Color = Color(0xFFE57373), // Light Red
    val rippleColor: Color = Color.White,
    val loadingIndicatorColor: Color = Color.White,
    val errorIndicatorColor: Color = Color.White,
    val successIndicatorColor: Color = Color.White,
    val disabledBackgroundColor: Color = Gray600,
    val disabledTextColor: Color = Gray300
)

@Composable
fun LoadingButtonPreview(
    state: UiResult,
    previewProgress: Float? = null
) {
    LoadingButton(
        content = LoadingButtonContent(
            text = "Login",
            modifier = Modifier,
            colors = LoadingButtonColors(),
            paddingHorizontal = 16.dp
        ),
        state = state,
        lambdas = LoadingButtonLambdas(
            onClick = {},
            onAnimationComplete = {}
        ),
        previewProgress = previewProgress
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewIdle() = LoadingButtonPreview(UiResult.Idle)

@Preview(showBackground = true)
@Composable
fun PreviewLoading() = LoadingButtonPreview(UiResult.Loading)

@Preview(showBackground = true)
@Composable
fun PreviewSuccess() = LoadingButtonPreview(UiResult.Success, previewProgress = 1f)

@Preview(showBackground = true)
@Composable
fun PreviewError() = LoadingButtonPreview(UiResult.Error, previewProgress = 1f)