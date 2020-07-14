package com.spiraldev.todoapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.spiraldev.todoapp.R
import com.spiraldev.todoapp.core.base.BaseActivity
import com.spiraldev.todoapp.databinding.ActivityMainBinding
import dagger.android.support.DaggerAppCompatActivity


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}