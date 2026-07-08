package com.ajla.digitalnialbum.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private const val MAX_PACKETS_PER_DAY = 5
    }

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val FAVORITE_TEAM = stringPreferencesKey("favorite_team")
        val PACKETS_OPENED_TODAY = intPreferencesKey("packets_opened_today")
        val LAST_PACKET_DAY = longPreferencesKey("last_packet_day")
        val DISPLAY_MODE = stringPreferencesKey("display_mode")
        val TOKENS = intPreferencesKey("tokens")
    }

    @Suppress("unused")
    val isOnboardingDone: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.ONBOARDING_DONE] ?: false
        }

    val favoriteTeam: Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.FAVORITE_TEAM]
        }

    suspend fun completeOnboarding(favoriteTeam: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_DONE] = true
            prefs[Keys.FAVORITE_TEAM] = favoriteTeam
        }
    }

    val remainingPacketsToday: Flow<Int> =
        context.dataStore.data.map { prefs ->
            val today = currentDayStamp()
            val lastDay = prefs[Keys.LAST_PACKET_DAY] ?: 0L
            val openedToday =
                if (lastDay == today) prefs[Keys.PACKETS_OPENED_TODAY] ?: 0 else 0

            (MAX_PACKETS_PER_DAY - openedToday).coerceAtLeast(0)
        }

    suspend fun recordPacketOpened() {
        val today = currentDayStamp()

        context.dataStore.edit { prefs ->
            val lastDay = prefs[Keys.LAST_PACKET_DAY] ?: 0L
            val openedToday =
                if (lastDay == today) prefs[Keys.PACKETS_OPENED_TODAY] ?: 0 else 0

            prefs[Keys.LAST_PACKET_DAY] = today
            prefs[Keys.PACKETS_OPENED_TODAY] = openedToday + 1
        }
    }

    val displayMode: Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.DISPLAY_MODE] ?: "LIST"
        }

    suspend fun setDisplayMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DISPLAY_MODE] = mode
        }
    }

    val tokens: Flow<Int> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.TOKENS] ?: 0
        }

    suspend fun addTokens(amount: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TOKENS] = (prefs[Keys.TOKENS] ?: 0) + amount
        }
    }

    suspend fun spendTokens(amount: Int): Boolean {
        var success = false

        context.dataStore.edit { prefs ->
            val current = prefs[Keys.TOKENS] ?: 0

            if (current >= amount) {
                prefs[Keys.TOKENS] = current - amount
                success = true
            }
        }

        return success
    }

    private fun currentDayStamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}