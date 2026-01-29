package com.v2ray.devicekit

import androidx.preference.PreferenceFragmentCompat

object SettingsUi {

    fun install(fragment: PreferenceFragmentCompat) {
        val screen = fragment.preferenceScreen
            ?: fragment.preferenceManager.createPreferenceScreen(fragment.requireContext()).also {
                fragment.preferenceScreen = it
            }

        fragment.preferenceManager.inflateFromResource(
            fragment.requireContext(),
            R.xml.pref_devicekit,
            screen,
        )

        UiBinder.bind(fragment)
    }
}
