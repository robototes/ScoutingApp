package com.scouting.app.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager

fun Context.getPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

fun <T : ViewModel> Context.getViewModel(type: Class<T>) : T = ViewModelProvider(this as ComponentActivity).get(type)