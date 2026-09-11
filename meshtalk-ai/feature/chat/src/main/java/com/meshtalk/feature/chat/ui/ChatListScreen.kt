package com.meshtalk.feature.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meshtalk.domain.model.DeliveryStatus
import com.meshtalk.domain.model.Message
import com.meshtalk.domain.model.MessageContent
import com.meshtalk.feature.chat.viewmodel.ChatListUiState
import com.meshtalk.feature.chat.viewmodel.ChatListViewModel
import com.meshtalk.feature.chat.viewmodel.MeshStatus

/**
 * First real screen in the app - deliberately minimal (a message list, no compose
 * box, no attachments yet) so it's something that can actually build and run once
 * :crypto/:mesh stubs start returning real data, rather than a fully-designed screen
 * sitting on top of nothing.
 */
@Composable
fun ChatListScreen(viewModel: ChatListViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    ChatListContent(uiState)
}

@Composable
private fun ChatListContent(uiState: ChatListUiState) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MeshTalk AI") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.messages.isEmpty() -> EmptyState(uiState.meshStatus)
                else -> MessageList(uiState.messages)
            }
        }
    }
}

@Composable
private fun EmptyState(status: MeshStatus) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No messages yet",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = meshStatusLabel(status),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun meshStatusLabel(status: MeshStatus): String = when (status) {
    MeshStatus.STARTING -> "Starting mesh network..."
    MeshStatus.CONNECTED -> "Connected - waiting for messages"
    MeshStatus.NO_NEIGHBORS -> "No nearby devices found yet"
    MeshStatus.OFFLINE -> "Mesh network is off"
}

@Composable
private fun MessageList(messages: List<Message>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        items(messages, key = { it.id }) { message ->
            MessageRow(message)
        }
    }
}

@Composable
private fun MessageRow(message: Message) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = when (val content = message.content) {
                is MessageContent.Text -> content.body
                is MessageContent.Media -> "[media]"
                is MessageContent.Location -> "[location shared]"
                is MessageContent.EmergencyBroadcast -> "\u26A0\uFE0F ${content.body}"
            },
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = deliveryStatusLabel(message.deliveryStatus) + if (message.hopCount > 0) " \u00B7 ${message.hopCount} hop(s)" else "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun deliveryStatusLabel(status: DeliveryStatus): String = when (status) {
    DeliveryStatus.QUEUED -> "Queued"
    DeliveryStatus.RELAYING -> "Relaying"
    DeliveryStatus.DELIVERED -> "Delivered"
    DeliveryStatus.READ -> "Read"
    DeliveryStatus.FAILED -> "Failed"
}
