package com.v2ray.devicekit

import androidx.preference.PreferenceFragmentCompat

object SettingsUi {

    fun install(fragment: PreferenceFragmentCompat) {
        fragment.addPreferencesFromResource(R.xml.pref_devicekit)

        UiBinder.bind(fragment)
    }
}
