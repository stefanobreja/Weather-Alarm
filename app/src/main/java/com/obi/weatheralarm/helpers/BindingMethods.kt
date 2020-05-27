package com.obi.weatheralarm.helpers

import android.view.View
import androidx.databinding.BindingAdapter
import com.obi.weatheralarm.presentation.main.model.Alarm

@BindingAdapter("app:setAlarmVisibility")
fun setAlarmVisibility(view: View, list: List<Alarm>?) {
    if (list.isNullOrEmpty()) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}