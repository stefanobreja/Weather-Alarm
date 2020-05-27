package com.obi.weatheralarm.presentation.main

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.obi.weatheralarm.databinding.ItemAlarmBinding
import com.obi.weatheralarm.extensions.getFormattedTime
import com.obi.weatheralarm.presentation.main.model.Alarm

class AlarmListAdapter(
    private val activity: Activity,
    private val items: List<Alarm>,
    private val onDisableClickCallBack: AlarmClickInterface
) :
    RecyclerView.Adapter<AlarmListAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAlarmBinding.inflate(inflater)
        return VH(activity, binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], onDisableClickCallBack)
    }

    class VH(private val activity: Activity, private var binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Alarm,
            onItemClickCallback: AlarmClickInterface
        ) {
            binding.activeSwitch.apply {
                isChecked = item.isActive
                setOnClickListener {
                    onItemClickCallback.onSetAlarmCheckClicked(item.apply {
                        isActive = binding.activeSwitch.isChecked
                    })
                }
            }
            if (item.isWeatherActive)
                binding.weatherAlarm.text = "Weather Alarm"
            else binding.weatherAlarm.text = item.name

            binding.time.text = activity.getFormattedTime(item.timeInMilliseconds)
            binding.activeSwitch.isChecked = item.isActive
            setChips(binding.chipGroup, item)

            binding.cardContainer.setOnClickListener {
                onItemClickCallback.onAlarmClicked(item)
            }
        }

        private fun setChips(
            chipGroup: ChipGroup,
            item: Alarm
        ) {
            for (i in 1 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (item.days.contains(chip.id)) {
                    chip.isEnabled = false
                    chip.isChecked = true
                } else {
                    chip.isEnabled = false
                }
            }
        }
    }

    interface AlarmClickInterface {
        fun onSetAlarmCheckClicked(alarm: Alarm)
        fun onAlarmClicked(id: Alarm)
    }
}
