package vn.callshield.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
private val Context.dataStore by preferencesDataStore(name = "callshield_settings")
class BlocklistRepository(private val context: Context) {
    private val entriesKey = stringPreferencesKey("block_entries")

    val entries: Flow<List<BlockEntry>> = context.dataStore.data.map { prefs ->
        decode(prefs[entriesKey].orEmpty())
    }
    suspend fun add(brand: String, phone: String, note: String = "") {
        val normalized = PhoneNormalizer.normalize(phone)
        require(brand.isNotBlank()) { "Vui lòng nhập tên thương hiệu." }
        require(normalized.length >= 3) { "Số điện thoại chưa hợp lệ." }

        context.dataStore.edit { prefs ->
            val current = decode(prefs[entriesKey].orEmpty()).toMutableList()
            val exists = current.any {
                it.phone == normalized && it.brand.equals(brand.trim(), ignoreCase = true)
            }
            if (!exists) {
                current.add(
                    BlockEntry(
                        id = UUID.randomUUID().toString(),
                        brand = brand.trim(),
                        phone = normalized,
                        note = note.trim()
                    )
                )
            }
            prefs[entriesKey] = encode(current)
        }
    }
    suspend fun toggle(id: String, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            val updated = decode(prefs[entriesKey].orEmpty()).map {
                if (it.id == id) it.copy(enabled = enabled) else it
            }
            prefs[entriesKey] = encode(updated)
        }
    }
    suspend fun remove(id: String) {
        context.dataStore.edit { prefs ->
            prefs[entriesKey] = encode(
                decode(prefs[entriesKey].orEmpty()).filterNot { it.id == id }
            )
        }
    }
    suspend fun refreshScreeningCache() {
        context.dataStore.edit { prefs ->
            val all = decode(prefs[entriesKey].orEmpty())
            val blocked = all.filter { it.enabled }.map { it.phone }.toSet()
            context.getSharedPreferences("callshield_cache", Context.MODE_PRIVATE)
                .edit()
                .putStringSet("blocked_numbers", blocked)
                .putStringSet("blocked_labels", all.filter { it.enabled }.map { "${it.phone}\t${it.brand}" }.toSet())
                .apply()
        }
    }
    private fun encode(entries: List<BlockEntry>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject()
                    .put("id", entry.id)
                    .put("brand", entry.brand)
                    .put("phone", entry.phone)
                    .put("note", entry.note)
                    .put("enabled", entry.enabled)
            )
        }
        return array.toString()
    }
    private fun decode(raw: String): List<BlockEntry> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                BlockEntry(
                    id = item.optString("id"),
                    brand = item.optString("brand"),
                    phone = item.optString("phone"),
                    note = item.optString("note"),
                    enabled = item.optBoolean("enabled", true)
                )
            }
        }.getOrDefault(emptyList())
    }
}