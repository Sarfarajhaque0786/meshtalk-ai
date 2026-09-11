package com.meshtalk.ai.service

import com.meshtalk.ai.detection.EmergencyKeywordDetector
import com.meshtalk.core.common.util.MeshError
import com.meshtalk.core.common.util.MeshResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default [AiService] implementation. Model-backed methods (smart replies,
 * summarization, translation, transcription, TTS) are stubbed pending the model
 * integration decision (which of Gemma/Phi/Llama-derivative + which runtime -
 * ONNX Runtime vs TFLite vs MediaPipe LLM Inference API - is a real evaluation to
 * run against target device RAM budgets, not a default to hardcode here).
 *
 * [classifyPriority] and [detectSpamOrPhishing] have working non-model fallbacks
 * today, consistent with the "AI layer is offline-first too" principle noted on
 * [AiService].
 */
@Singleton
class OnDeviceAiService @Inject constructor(
    private val emergencyKeywordDetector: EmergencyKeywordDetector
) : AiService {

    override suspend fun suggestSmartReplies(conversationContext: List<String>): MeshResult<List<String>> =
        notImplemented("suggestSmartReplies")

    override suspend fun summarize(messages: List<String>): MeshResult<String> =
        notImplemented("summarize")

    override suspend fun translate(text: String, targetLanguageTag: String): MeshResult<String> =
        notImplemented("translate")

    override suspend fun transcribeVoice(audioPcm16: ByteArray): MeshResult<String> =
        notImplemented("transcribeVoice")

    override suspend fun synthesizeSpeech(text: String, languageTag: String): MeshResult<ByteArray> =
        notImplemented("synthesizeSpeech")

    override suspend fun classifyPriority(text: String): MeshResult<MessagePriority> =
        MeshResult.Success(emergencyKeywordDetector.classify(text))

    override suspend fun detectSpamOrPhishing(text: String): MeshResult<ThreatAssessment> {
        val reasons = mutableListOf<ThreatReason>()
        val urlPattern = Regex("""https?://\S+""")
        val suspiciousTlds = setOf(".xyz", ".top", ".click", ".zip")

        urlPattern.findAll(text).forEach { match ->
            if (suspiciousTlds.any { tld -> match.value.contains(tld) }) {
                reasons.add(ThreatReason.MALICIOUS_URL)
            }
        }
        val urgencyPhrases = listOf("verify your account", "click here now", "limited time", "act now")
        if (urgencyPhrases.any { phrase -> text.lowercase().contains(phrase) }) {
            reasons.add(ThreatReason.PHISHING_LANGUAGE)
        }

        return MeshResult.Success(
            ThreatAssessment(
                isSuspicious = reasons.isNotEmpty(),
                reasons = reasons,
                confidence = if (reasons.isEmpty()) 0f else 0.5f // heuristic-only, not model-calibrated
            )
        )
    }

    override suspend fun parseNaturalLanguageCommand(utterance: String): MeshResult<AiCommand?> =
        notImplemented("parseNaturalLanguageCommand")

    private fun <T> notImplemented(method: String): MeshResult<T> =
        MeshResult.Failure(MeshError.Unknown(NotImplementedError("AiService.$method requires a bundled on-device model")))
}
