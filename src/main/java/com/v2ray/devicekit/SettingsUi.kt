package com.v2ray.devicekit

import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

object SettingsUi {

    fun install(fragment: PreferenceFragmentCompat) {
        fragment.addPreferencesFromResource(R.xml.pref_devicekit)

        installSummaryProviders(fragment)

        UiBinder.bind(fragment)
    }

    private fun installSummaryProviders(fragment: PreferenceFragmentCompat) {
        val listKeys = listOf(
            PrefKeys.OS,
            PrefKeys.USER_AGENT_PRESET,
            PrefKeys.V2RAYTUN_PLATFORM,
            PrefKeys.FLCLASHX_PLATFORM,
        )

        for (key in listKeys) {
            fragment.findPreference<ListPreference>(key)?.summaryProvider =
                androidx.preference.Preference.SummaryProvider<ListPreference> { it.entry ?: "" }
        }

        val editKeys = listOf(
            PrefKeys.HWID,
            PrefKeys.OS_VER,
            PrefKeys.MODEL,
            PrefKeys.LOCALE,
            PrefKeys.USER_AGENT_HAPP_VERSION,
            PrefKeys.USER_AGENT_V2RAYNG_VERSION,
            PrefKeys.USER_AGENT_FLCLASHX_VERSION,
            PrefKeys.USER_AGENT,
        )

        for (key in editKeys) {
            fragment.findPreference<EditTextPreference>(key)?.summaryProvider =
                androidx.preference.Preference.SummaryProvider<EditTextPreference> { it.text.orEmpty() }
        }
    }
}
