package com.v2ray.devicekit

import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

object SettingsUi {

    fun install(fragment: PreferenceFragmentCompat) {
        val screen = fragment.preferenceScreen
            ?: fragment.preferenceManager.createPreferenceScreen(fragment.requireContext()).also {
                fragment.preferenceScreen = it
            }

        val inflatedOk = runCatching {
            fragment.preferenceManager.inflateFromResource(
                fragment.requireContext(),
                R.xml.pref_devicekit,
                screen,
            )
        }.isSuccess

        if (!inflatedOk) {
            runCatching { fragment.addPreferencesFromResource(R.xml.pref_devicekit) }
        }

        installSummaryProviders(fragment)

        UiBinder.bind(fragment)
    }

    private fun installSummaryProviders(fragment: PreferenceFragmentCompat) {
        val listKeys = listOf(
            PrefKeys.HWID_OS,
            PrefKeys.UA_PRESET,
            PrefKeys.UA_V2RAYTUN_PLATFORM,
            PrefKeys.UA_FLCLASHX_PLATFORM,
        )

        for (key in listKeys) {
            fragment.findPreference<ListPreference>(key)?.summaryProvider =
                androidx.preference.Preference.SummaryProvider<ListPreference> { it.entry ?: "" }
        }

        val editKeys = listOf(
            PrefKeys.HWID_VAL,
            PrefKeys.HWID_OS_VER,
            PrefKeys.HWID_MODEL,
            PrefKeys.HWID_LOCALE,
            PrefKeys.UA_HAPP_VERSION,
            PrefKeys.UA_V2RAYNG_VERSION,
            PrefKeys.UA_FLCLASHX_VERSION,
            PrefKeys.UA_CUSTOM,
        )

        for (key in editKeys) {
            fragment.findPreference<EditTextPreference>(key)?.summaryProvider =
                androidx.preference.Preference.SummaryProvider<EditTextPreference> { it.text.orEmpty() }
        }
    }
}
