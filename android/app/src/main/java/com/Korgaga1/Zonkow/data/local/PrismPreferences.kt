package com.Korgaga1.Zonkow.data.local

import android.content.Context
import android.content.SharedPreferences

class PrismPreferences(context: Context) {

    private val store: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = store.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        store.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = store.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        store.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String = store.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        store.edit().putString(key, value).apply()
    }

    fun remove(keys: List<String>) {
        val editor = store.edit()
        keys.forEach { editor.remove(it) }
        editor.apply()
    }

    companion object {
        private const val STORE_NAME = "prism_drop_circuit_store"
        const val KEY_BEST_SCORE = "best_score"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_RUNS = "runs_completed"
        const val KEY_DROPS = "lifetime_drops"
        const val KEY_RUN_INDEX = "run_index"
        const val KEY_TIER = "tier"
        const val KEY_HIGH_CONTRAST = "high_contrast_marks"
        const val KEY_REDUCED_MOTION = "reduced_motion"
    }
}
