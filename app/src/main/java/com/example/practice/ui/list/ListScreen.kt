package com.example.practice.ui.list

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.practice.data.cache.FilterBadgeCache
import com.example.practice.data.cache.FilterBadgeCacheEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

@Composable
fun ListScreen(
    onItemClick: (String) -> Unit,
    onFiltersClick: () -> Unit = {}
) {
    val viewModel: NetworkListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    // Получаем FilterBadgeCache через LocalContext
    val context = androidx.compose.ui.platform.LocalContext.current
    val hiltEntryPoint = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            FilterBadgeCacheEntryPoint::class.java
        )
    }
    val filterBadgeCache = remember { hiltEntryPoint.filterBadgeCache() }
    val hasActiveFilters by filterBadgeCache.hasActiveFilters.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddToFavoritesDialog by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Показываем ошибку в Snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Киностудии (${uiState.total})",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (hasActiveFilters) {
                                Badge()
                            }
                        }
                    ) {
                        Button(
                            onClick = onFiltersClick,
                            enabled = !uiState.isLoading
                        ) {
                            Text("Фильтры")
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.refreshItems() },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Обновить")
                    }
                }
            }

            when {
                uiState.isLoading && uiState.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null && uiState.items.isEmpty() -> {
                    ErrorScreen(
                        errorMessage = uiState.error!!,
                        onRetry = { viewModel.refreshItems() }
                    )
                }
                uiState.items.isEmpty() -> {
                    EmptyScreen(
                        message = "Список студий пуст",
                        onRetry = { viewModel.refreshItems() }
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            items = uiState.items,
                            key = { it.id }
                        ) { studio ->
                            ListItem(
                                headlineContent = { Text(studio.title) },
                                supportingContent = { Text("${studio.subType} • ${studio.type}") },
                                trailingContent = { Text("Фильмы: ${studio.movies.size}") },
                                modifier = Modifier
                                    .pointerInput(studio.id) {
                                        detectTapGestures(
                                            onTap = { onItemClick(studio.id) },
                                            onLongPress = { 
                                                showAddToFavoritesDialog = studio.id 
                                            }
                                        )
                                    }
                            )
                        }
                        
                        // Кнопка "Загрузить еще" если есть дополнительные страницы
                        if (uiState.hasMorePages) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator()
                                    } else {
                                        Button(
                                            onClick = { viewModel.loadMoreItems() }
                                        ) {
                                            Text("Загрузить еще")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Диалог добавления в избранное
        showAddToFavoritesDialog?.let { studioId ->
            val studio = uiState.items.find { it.id == studioId }
            AlertDialog(
                onDismissRequest = { showAddToFavoritesDialog = null },
                title = { Text("Добавить в избранное") },
                text = { 
                    Text("Вы хотите добавить студию \"${studio?.title ?: ""}\" в избранное?") 
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.addToFavorites(studioId)
                                snackbarHostState.showSnackbar("Добавлено в избранное")
                            }
                            showAddToFavoritesDialog = null
                        }
                    ) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddToFavoritesDialog = null }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Произошла ошибка",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Попробовать снова")
        }
    }
}

@Composable
private fun EmptyScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📭",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Обновить")
        }
    }
}


