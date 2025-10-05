package com.example.enumerator_monitor.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.enumerator_monitor.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial state for animations
        setupInitialState()

        // Start animations
        startSplashAnimations()

        // Navigate to MainActivity after delay
        navigateToMainActivity()
    }

    private fun setupInitialState() {
        // Set initial alpha to 0 and translationY to 20dp for all elements
        binding.ivLogo.alpha = 0f
        binding.ivLogo.translationY = 30f // 20dp converted to pixels (20 * 3 = 60)

        binding.tvAppName.alpha = 0f
        binding.tvAppName.translationY = 30f

        binding.tvTagline.alpha = 0f
        binding.tvTagline.translationY = 30f
    }

    private fun startSplashAnimations() {
        // Create animations for each element
        val logoAlphaAnim = ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 0f, 1f)
        val logoTranslationAnim =
            ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_Y, 60f, 0f)

        val appNameAlphaAnim = ObjectAnimator.ofFloat(binding.tvAppName, View.ALPHA, 0f, 1f)
        val appNameTranslationAnim =
            ObjectAnimator.ofFloat(binding.tvAppName, View.TRANSLATION_Y, 60f, 0f)

        val taglineAlphaAnim = ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 0f, 1f)
        val taglineTranslationAnim =
            ObjectAnimator.ofFloat(binding.tvTagline, View.TRANSLATION_Y, 60f, 0f)

        // Set duration for all animations
        val animationDuration = 1000L // 1 second

        logoAlphaAnim.duration = animationDuration
        logoTranslationAnim.duration = animationDuration
        appNameAlphaAnim.duration = animationDuration
        appNameTranslationAnim.duration = animationDuration
        taglineAlphaAnim.duration = animationDuration
        taglineTranslationAnim.duration = animationDuration

        // Create AnimatorSet for logo (first)
        val logoAnimatorSet = AnimatorSet()
        logoAnimatorSet.playTogether(logoAlphaAnim, logoTranslationAnim)

        // Create AnimatorSet for app name (second, with delay)
        val appNameAnimatorSet = AnimatorSet()
        appNameAnimatorSet.playTogether(appNameAlphaAnim, appNameTranslationAnim)
        appNameAnimatorSet.startDelay = 300L // 300ms delay after logo

        // Create AnimatorSet for tagline (third, with delay)
        val taglineAnimatorSet = AnimatorSet()
        taglineAnimatorSet.playTogether(taglineAlphaAnim, taglineTranslationAnim)
        taglineAnimatorSet.startDelay = 300L // 600ms delay after logo

        // Start all animations
        logoAnimatorSet.start()
        appNameAnimatorSet.start()
        taglineAnimatorSet.start()
    }

    private fun navigateToMainActivity() {
        // Navigate to LoginActivity after total animation time + buffer
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500L) // 2.5 seconds total (1s animation + 0.6s delay + 0.9s buffer)
    }
}
