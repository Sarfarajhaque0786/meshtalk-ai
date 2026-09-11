package com.meshtalk.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meshtalk.domain.model.Message
import com.meshtalk.domain.usecase.ObserveConversationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatListUiState(
    val isLoading: Boolean = true,
    val messages: List<Message> = emptyList(),
    val meshStatus: MeshStatus = MeshStatus.STARTING
)

enum class MeshStatus { STARTING, CONNECTED, NO_NEIGHBORS, OFFLINE }

/**
 * Backs the placeholder [com.meshtalk.feature.chat.ui.ChatListScreen]. Scoped to a
 * single hardcoded conversation id for now - a real conversation-list screen (backed
 * by a ConversationDao grouping messages/users) is the natural next feature once the
 * crypto/mesh stubs below it are implemented enough to produce real messages to show.
 */
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val observeConversationUseCase: ObserveConversationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeConversationUseCase(DEMO_CONVERSATION_ID).collect { messages ->
                _uiState.value = _uiState.value.copy(isLoading = false, messages = messages)
            }
        }
    }

    private companion object {
        const val DEMO_CONVERSATION_ID = "demo-conversation"
    }
}
