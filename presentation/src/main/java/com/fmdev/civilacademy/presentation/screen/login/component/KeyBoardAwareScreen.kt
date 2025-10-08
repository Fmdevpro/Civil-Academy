package com.fmdev.civilacademy.presentation.screen.login.component

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

@Composable
fun KeyboardAwareScreen() {
    AndroidView(
        factory = { context ->
            val view = View(context)

            ViewCompat.setWindowInsetsAnimationCallback(
                view,
                object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                    private var startBottom = 0f
                    private var endBottom = 0f

                    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                        startBottom = view.bottom.toFloat()
                    }

                    override fun onStart(
                        animation: WindowInsetsAnimationCompat,
                        bounds: WindowInsetsAnimationCompat.BoundsCompat
                    ): WindowInsetsAnimationCompat.BoundsCompat {
                        endBottom = view.bottom.toFloat()
                        return bounds
                    }

                    override fun onProgress(
                        insets: WindowInsetsCompat,
                        runningAnimations: MutableList<WindowInsetsAnimationCompat>
                    ): WindowInsetsCompat {
                        val imeAnimation = runningAnimations.find {
                            it.typeMask and WindowInsetsCompat.Type.ime() != 0
                        } ?: return insets

                        view.translationY =
                            (startBottom - endBottom) * (1 - imeAnimation.interpolatedFraction)

                        return insets
                    }

                    override fun onEnd(animation: WindowInsetsAnimationCompat) {
                        view.translationY = 0f
                    }
                }
            )
            view
        },
        modifier = Modifier.fillMaxSize()
    )
}
