package com.fmdev.civilacademy.presentation.screen.login.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fmdev.civilacademy.presentation.R
import com.fmdev.civilacademy.shared.constant.StringConstants.EMPTY_STRING

@Composable
fun CustomLoginLogo(isDarkTheme: Boolean, isKeyboardVisible: Boolean, modifier: Modifier) {

    val colorFilter = if (isDarkTheme) null
    else ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
    val logoAlphaLight by animateFloatAsState(
        targetValue = if (isKeyboardVisible) 0f else 1f, label = EMPTY_STRING,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )
    val logoAlphaDark by animateFloatAsState(targetValue = if (isKeyboardVisible) 0f else .19f, label = EMPTY_STRING)
    val modifierCopy = if (!isDarkTheme) {
        modifier.size(120.dp).alpha(logoAlphaLight).background(Color.Transparent)
            .padding(top = 0.dp).offset(y = 120.dp, x = (0).dp)
    } else {
        Modifier.size(550.dp).alpha(logoAlphaDark).background(Color.Transparent)
            .padding(top = 0.dp).offset(y = (-100).dp, x = (-100).dp)
            .graphicsLayer { rotationX = 180f }
    }

    Image(
        painter = painterResource(id = R.drawable.ic_brain_logo),
        contentDescription = stringResource(id = R.string.login_screen__content_description__brain_image),
        colorFilter = colorFilter,
        modifier = modifierCopy,
        alignment = if (isDarkTheme) Alignment.BottomCenter else Alignment.TopCenter
    )

}