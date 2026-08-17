package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateExtremeMetalTrack(prompt: String): String? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext null
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            
            val systemInstruction = """
                You are SUNO HYPER 5.5, the supreme extreme heavy metal & avant-garde audio-visual AI engine.
                The theme is dark, sick and twisted with demons and angels, neon purple/green/black/turquoise aesthetic.
                Generate a complete extreme metal composition with:
                1. Track Title
                2. Subgenre (e.g., Cyber-Deathcore, Demonic Djent, Symphonic Blackened Angelic Metal)
                3. BPM (between 180 and 280)
                4. Guitar Tuning (Drop A, Drop E, Drop F#)
                5. Full structured lyrics containing [INTRO], [VERSE 1: Demonic Screams], [PRE-CHORUS: Angelic Falsetto], [CHORUS: Demon-Angel Duality], [BREAKDOWN: Drop-Z Chug], [OUTRO: Celestial Apocalypse].
                6. Five cinematic video scenes with prompt descriptions for visual generation.
                Format clearly in structured text.
            """.trimIndent()

            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Generate an extreme metal opus about: $prompt")
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstruction)
                        })
                    })
                })
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext null
            }

            val responseStr = response.body?.string() ?: return@withContext null
            val responseJson = JSONObject(responseStr)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return@withContext parts.getJSONObject(0).optString("text")
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }
}
