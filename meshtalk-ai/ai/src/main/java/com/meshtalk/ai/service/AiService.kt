package com.meshtalk.ai.service

import com.meshtalk.core.common.util.MeshResult

/**
 * Single entry point for every on-device AI capability in the product spec. Kept as
 * one interface (rather than one per feature) because most of these share the same
 * underlying small on-device LLM/runtime and the same "must degrade gracefully
 * offline / on low-end hardware" constraint - callers (chat UI, notification
 * pipeline, voice command handler) shouldn't need to know which model backs which
 * feature.
 *
 * Every method returns [MeshResult] rather than throwing, and every implementation
 * must have a cheap fallback path (e.g. keyword-based emergency detection) for
 * devices where the full model can't be loaded (RAM-constrained, first launch before
 * model download/verification completes) - offline-first applies to the AI layer
 * too, not just networking.
 */
interface AiService {
    suspend fun suggestSmartReplies(conversationContext: List<String>): MeshResult<List<String>>
    suspend fun summarize(messages: List<String>): MeshResult<String>
    suspend fun translate(text: String, targetLanguageTag: String): MeshResult<String>
    suspend fun transcribeVoice(audioPcm16: ByteArray): MeshResult<String>
    suspend fun synthesizeSpeech(text: String, languageTag: String): MeshResult<ByteArray>
    suspend fun classifyPriority(text: String): MeshResult<MessagePriority>
    suspend fun detectSpamOrPhishing(text: String): MeshResult<ThreatAssessment>
    suspend fun parseNaturalLanguageCommand(utterance: String): MeshResult<AiCommand?>
}

enum class MessagePriority { NORMAL, HIGH, EMERGENCY }

data class ThreatAssessment(
    val isSuspicious: Boolean,
    val reasons: List<ThreatReason>,
    val confidence: Float
)

enum class ThreatReason { SPAM_PATTERN, MALICIOUS_URL, PHISHING_LANGUAGE, IMPERSONATION_SIGNAL }

/** Parsed result of an utterance like "Send location to Rahul" or "Translate to Bengali". */
sealed interface AiCommand {
    data class SendMessageTo(val recipientName: String, val content: String) : AiCommand
    data class SendLocationTo(val recipientName: String) : AiCommand
    data object CreateEmergencyBroadcast : AiCommand
    data class SummarizeUnread(val conversationId: String?) : AiCommand
    data class TranslateTo(val languageTag: String, val text: String?) : AiCommand
}
