package io.github.takusan23.litebarometer.Activity

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.takusan23.litebarometer.R
import kotlinx.android.synthetic.main.activity_licence.*

class LicenceActivity : AppCompatActivity() {

    private var isDarkMode: Boolean = false

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

        setContentView(R.layout.activity_licence)

        supportActionBar?.title = getString(R.string.licence)

        val licence = """
PhilJay/MPAndroidChart

Copyright 2019 Philipp Jahoda

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
        
----------------

material-components/material-components-android

material-components/material-components-android is licensed under the

Apache License 2.0

----------------
        
天気アイコンに以下のサイトの素材を利用しました。

https://icooon-mono.com/
        
        """.trimIndent()

        licence_textview.text = licence

        if (isDarkMode) {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))
        }

    }
}
