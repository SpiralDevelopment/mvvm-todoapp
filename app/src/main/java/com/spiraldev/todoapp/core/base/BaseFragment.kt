package com.spiraldev.todoapp.core.base

import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.spiraldev.todoapp.core.di.ViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject


interface InitViews {
    fun initializeViews()
    fun observeViewModel()
}

abstract class BaseFragment : DaggerFragment(), InitViews {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(
            context,
            message,
            duration
        ).show()
    }

    fun showSnackBar(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(
            view,
            message,
            duration
        ).show()
    }

    protected fun showSnackBarWithAction(
        view: View,
        message: String,
        actionButton: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionTextColor: Int = Color.rgb(17, 122, 101),
        onClick: () -> Unit
    ) {
        Snackbar.make(
            view,
            message,
            duration
        ).setActionTextColor(actionTextColor)
            .setAction(actionButton) {
                onClick()
            }.show()
    }
}