package io.github.takusan23.litebarometer.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import io.github.takusan23.litebarometer.R
import kotlinx.android.synthetic.main.activity_kono_app.*

class KonoAppActivity : AppCompatActivity() {

    private var isDarkMode = false

    private val version by lazy { packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA).versionName }
    private val updateAt = "2021/08/08"

    /*
    *
    * 台風第19号　やばそう
    *
    * */

    private val twitterLink = "https://twitter.com/takusan__23"
    private val mastodonLink = "https://best-friends.chat/web/accounts/20498"
    private val githubLink = "https://github.com/takusan23/LiteBarometer"

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

        setContentView(R.layout.activity_kono_app)

        supportActionBar?.title = getString(R.string.kono_app)

        kono_app_ver_textview.text = version
        kono_app_create_textview.text = "$updateAt"

        kono_app_twitter.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, twitterLink.toUri())
            startActivity(intent)
        }

        kono_app_mastodon.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, mastodonLink.toUri())
            startActivity(intent)
        }

        kono_app_github.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, githubLink.toUri())
            startActivity(intent)
        }

        if (isDarkMode) {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))
        }

    }
}
