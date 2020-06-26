package com.freezer.mathsolver

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start Main Activity

        val openMainActivity = Intent(this, MainActivity::class.java)
        startActivity(openMainActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        finish()
    }
}