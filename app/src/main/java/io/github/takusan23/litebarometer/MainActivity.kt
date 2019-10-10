package io.github.takusan23.litebarometer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.takusan23.litebarometer.Fragment.BarometerFragment
import io.github.takusan23.litebarometer.Fragment.BarometerListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Fragment設置
        supportActionBar?.title = getString(R.string.now_barometer)
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.main_activity_fragment_linearlayout, BarometerFragment())
        trans.commit()

        main_activity_bottom_nav_bar.setOnNavigationItemSelectedListener {
            when (main_activity_bottom_nav_bar.selectedItemId) {
                R.id.main_activity_menu_barometer_history -> {
                    supportActionBar?.title = getString(R.string.now_barometer)
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(R.id.main_activity_fragment_linearlayout, BarometerFragment())
                    trans.commit()
                }
                R.id.main_activity_menu_now_barometer -> {
                    supportActionBar?.title = getString(R.string.barometer_history)
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(R.id.main_activity_fragment_linearlayout, BarometerListFragment())
                    trans.commit()
                }
            }
            true
        }

    }
}
