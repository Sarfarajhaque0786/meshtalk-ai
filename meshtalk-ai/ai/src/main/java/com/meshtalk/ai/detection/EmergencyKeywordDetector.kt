package com.meshtalk.ai.detection

import com.meshtalk.ai.service.MessagePriority
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Deliberately NOT model-based: this is the fallback (and first line of defense)
 * that runs on every incoming/outgoing message regardless of whether the on-device
 * LLM is loaded, so "emergency keyword detection" never depends on a model download
 * having succeeded. A model-backed classifier can layer on top of this later for
 * subtler priority signals; this keyword list is the floor, not the ceiling.
 *
 * Keyword list is intentionally small and multilingual-extensible rather than
 * exhaustive - false negatives here are mitigated by the user always being able to
 * manually trigger SOS mode, so this is a convenience signal, not the only path to
 * emergency handling.
 */
@Singleton
class EmergencyKeywordDetector @Inject constructor() {

    private val emergencyKeywords = setOf(
        "sos", "help", "emergency", "urgent", "danger", "trapped", "injured",
        "medical", "ambulance", "fire", "flood", "earthquake", "evacuate"
    )

    fun classify(text: String): MessagePriority {
        val normalized = text.lowercase()
        val hits = emergencyKeywords.count { keyword -> normalized.contains(keyword) }
        return when {
            hits >= 2 -> MessagePriority.EMERGENCY
            hits == 1 -> MessagePriority.HIGH
            else -> MessagePriority.NORMAL
        }
    }
}
