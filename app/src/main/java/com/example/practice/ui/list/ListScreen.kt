package com.example.practice.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListScreen(
    viewModel: ListViewModel,
    onItemClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Киностудии (${uiState.total})",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = uiState.items,
                    key = { it.id }
                ) { studio ->
                    ListItem(
                        headlineContent = { Text(studio.title) },
                        supportingContent = { Text("${studio.subType} • ${studio.type}") },
                        trailingContent = { Text("Фильмы: ${studio.movies.id}") },
                        modifier = Modifier
                            .clickable { onItemClick(studio.id) }
                    )
                }
            }
        }
    }
}


