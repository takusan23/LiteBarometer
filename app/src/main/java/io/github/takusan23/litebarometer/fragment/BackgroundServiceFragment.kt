package io.github.takusan23.litebarometer.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import io.github.takusan23.litebarometer.service.BarometerService
import io.github.takusan23.litebarometer.R
import kotlinx.android.synthetic.main.fragment_background_service_layout.*

class BackgroundServiceFragment : Fragment() {

    lateinit var pref_setting: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_background_service_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref_setting = PreferenceManager.getDefaultSharedPreferences(context)

        //設定読み込み
        fragment_background_service_interval_textinput.setText(
            pref_setting.getInt(
                "interval",
                60
            ).toString()
        )

        //保存ボタン
        fragment_background_service_save_button.setOnClickListener {
            val editor = pref_setting.edit()
            editor.putInt(
                "interval",
                fragment_background_service_interval_textinput.text.toString().toInt()
            )
            editor.apply()
        }

        //サービス起動
        val intent = Intent(context, BarometerService::class.java)
        fragment_background_service_start_service.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.startForegroundService(intent)
            } else {
                activity?.startService(intent)
            }
        }
        //終了
        fragment_background_service_stop_sevice.setOnClickListener {
            activity?.stopService(intent)
        }

    }
}