package io.github.takusan23.litebarometer.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.github.takusan23.litebarometer.R
import kotlinx.android.synthetic.main.fragment_offline_weather.*
import java.util.*
import kotlin.math.roundToInt


class OfflineWeatherFragment : Fragment() {

    //位置情報
    lateinit var locationManager: LocationManager
    //高度
    var altitude: Double = 0.0
    //気圧センサー
    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener
    //気圧
    var pressure: Float = 0F

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offline_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //現在位置の取得（高度取得）に位置情報の権限が必要なのでリクエスト
        // requestPermission()

        //気圧取得
        getBarometricPressure()

    }

    //気圧取得
    fun getBarometricPressure() {
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //今回は気圧計
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_PRESSURE)
        //受け取る
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //つかわん
            }

            override fun onSensorChanged(event: SensorEvent?) {
                //値はここで受けとる
                //今回は気圧計のみだからいいけどほかにも登録するときは分岐してね
                if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
                    //気圧計の値
                    pressure = event.values[0]

                    fragment_offline_weather_textview.text = "${pressure.roundToInt()} hPa"

                    if (1013 <= pressure) {
                        //1013より高かったら晴れ
                        fragment_offline__weather_imageview.setImageDrawable(
                            context?.getDrawable(
                                R.drawable.ic_weather_sun
                            )
                        )
                        fragment_offline__weather_textview.text =
                            getString(R.string.weather_sun)
                    } else {
                        //低かったらあめ？
                        fragment_offline__weather_imageview.setImageDrawable(
                            context?.getDrawable(
                                R.drawable.ic_weather_rain
                            )
                        )
                        fragment_offline__weather_textview.text =
                            getString(R.string.weather_rain)
                    }

                }
            }
        }
        //登録
        sensorManager.registerListener(
            sensorEventListener,
            sensorList[0],  //配列のいっこめ。気圧計
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )

    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //権限ないとき
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1000
            )
        } else {
            //権限ある
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //権限取得成功
                getLocation()
            }
        }
    }

    private fun getLocation() {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE

/*
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 100, 10f, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            altitude = location.altitude

                            //海面更正気圧計算

                            val calcPressure = calcPressure()

                            fragment_offline_weather_kaimen_hosei_kiatsu_textview.text =
                                "${calcPressure.roundToInt()} hPa"

                            if (1013 <= calcPressure) {
                                //1013より高かったら晴れ
                                fragment_offline__weather_imageview.setImageDrawable(
                                    context?.getDrawable(
                                        R.drawable.ic_weather_sun
                                    )
                                )
                                fragment_offline__weather_textview.text =
                                    getString(R.string.weather_sun)
                            } else {
                                //低かったらあめ？
                                fragment_offline__weather_imageview.setImageDrawable(
                                    context?.getDrawable(
                                        R.drawable.ic_weather_rain
                                    )
                                )
                                fragment_offline__weather_textview.text =
                                    getString(R.string.weather_rain)
                            }


                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }
                })
*/
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorEventListener)
    }

    //海面更正気圧を計算する
    fun calcPressure(): Double {
        val temp = getAverageTemp() //気温
        val bunbo = 0.0065 * altitude
        val bunshi = temp + 0.0065 + 273.15
        val kakko = 1 - (bunbo / bunshi)
        val ruizyou = Math.pow(kakko, -5.257)
        return pressure * ruizyou
    }

    fun getAverageTemp(): Float {
        val calender = Calendar.getInstance()
        val list = arrayListOf<Float>(
            5.6f,
            7.2f,
            10.6f,
            13.6f,
            20.0f,
            21.8f,
            24.1f,
            28.4f,
            25.1f,
            19.4f,
            13.1f,
            8.5f
        )
        val month = calender.get(Calendar.MONTH)
        return list[month]
    }

}