package com.spiraldev.todoapp.core.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity() {

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(
            this,
            message,
            duration
        ).show()
    }
}