package com.fmdev.civilacademy

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Property
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fmdev.civilacademy.presentation.AppContent
import com.fmdev.civilacademy.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpEdgeToEdge()
        setupSplashScreen()
        setContent {
            SetSystemUi()
            AppContent(mainViewModel = mainViewModel)
        }
    }

    @Composable
    fun SetSystemUi() {
        val isSystemUIVisible by mainViewModel.isSystemUIVisible.collectAsState()

        LaunchedEffect(isSystemUIVisible) {
            if (isSystemUIVisible) showSystemUI() else hideSystemUI()
        }
    }

    private fun setUpEdgeToEdge() {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun hideSystemUI() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        WindowCompat.getInsetsController(window, window.decorView)
            .show(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupSplashScreen() {
        installSplashScreen().apply {
//            setKeepOnScreenCondition {  }

            if (Build.VERSION.SDK_INT >= 34) {
                setOnExitAnimationListener { screen ->
                    animateSplashScreenExit(screen)
                }
            }
        }
    }

    private fun animateSplashScreenExit(screen: SplashScreenViewProvider) {
        fun createZoomAnimator(property: Property<View, Float>): ObjectAnimator {
            return ObjectAnimator.ofFloat(screen.iconView, property, 1.0f, 0.0f).apply {
                interpolator = OvershootInterpolator()
                duration = 500L
                doOnEnd { screen.remove() }
            }
        }
        createZoomAnimator(View.SCALE_X).start()
        createZoomAnimator(View.SCALE_Y).start()
    }
}