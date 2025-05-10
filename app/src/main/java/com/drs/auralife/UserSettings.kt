package com.drs.auralife

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.drs.auralife.ui.main.UserSettingsFragment

class UserSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, UserSettingsFragment.newInstance()).commitNow()
        }
    }
}
