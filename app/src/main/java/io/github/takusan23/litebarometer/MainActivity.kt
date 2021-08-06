package io.github.takusan23.litebarometer

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import io.github.takusan23.litebarometer.activity.KonoAppActivity
import io.github.takusan23.litebarometer.activity.LicenceActivity
import io.github.takusan23.litebarometer.fragment.BackgroundServiceFragment
import io.github.takusan23.litebarometer.fragment.BarometerFragment
import io.github.takusan23.litebarometer.fragment.BarometerListFragment
import io.github.takusan23.litebarometer.fragment.OfflineWeatherFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //でーま設定
        val theme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (theme) {
            Configuration.UI_MODE_NIGHT_YES -> {
                //ダークモード有効時
                isDarkMode = true
                setTheme(R.style.OLEDTheme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                //ダークモード無効時
                setTheme(R.style.AppTheme)
            }
        }

        //テーマを選んでからsetContentViewを
        setContentView(R.layout.activity_main)

        //Fragment設置
        supportActionBar?.title = getString(R.string.now_barometer)
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.main_activity_fragment_linearlayout, BarometerFragment())
        trans.commit()

        main_activity_bottom_nav_bar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_activity_menu_now_barometer -> {
                    supportActionBar?.title = getString(R.string.now_barometer)
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(R.id.main_activity_fragment_linearlayout, BarometerFragment())
                    trans.commit()
                }
                R.id.main_activity_menu_barometer_history -> {
                    supportActionBar?.title = getString(R.string.barometer_history)
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(R.id.main_activity_fragment_linearlayout, BarometerListFragment())
                    trans.commit()
                }
                R.id.main_activity_menu_background_service -> {
                    supportActionBar?.title = getString(R.string.background)
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(
                        R.id.main_activity_fragment_linearlayout,
                        BackgroundServiceFragment()
                    )
                    trans.commit()
                }
                R.id.main_activity_menu_offline_weather->{
                    supportActionBar?.title = getString(R.string.offline_weather)
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(
                        R.id.main_activity_fragment_linearlayout,
                        OfflineWeatherFragment()
                    )
                    trans.commit()
                }
                R.id.main_activity_menu_graph -> {
                    supportActionBar?.title = getString(R.string.graph_menu)
                    val trans = supportFragmentManager.beginTransaction()
                    trans.commit()
                }
            }
            true
        }

        if (isDarkMode) {
            //ダークモードでやること
            supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))
            main_activity_bottom_nav_bar.background = ColorDrawable(Color.parseColor("#000000"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_drop_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_activity_menu_licence -> {
                val intent = Intent(this, LicenceActivity::class.java)
                startActivity(intent)
            }
            R.id.main_activity_menu_kono_app -> {
                val intent = Intent(this, KonoAppActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
