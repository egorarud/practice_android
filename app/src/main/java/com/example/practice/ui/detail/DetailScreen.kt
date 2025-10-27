package com.example.practice.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.practice.ui.list.NetworkListViewModel

@Composable
fun DetailScreen(
    itemId: String
) {
    val viewModel: NetworkListViewModel = hiltViewModel()
    val detailState by viewModel.detailState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(itemId) {
        viewModel.selectItem(itemId)
    }

    // Показываем ошибку в Snackbar
    LaunchedEffect(detailState.error) {
        detailState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            detailState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            detailState.error != null -> {
                ErrorScreen(
                    errorMessage = detailState.error ?: "Неизвестная ошибка",
                    onRetry = { viewModel.selectItem(itemId) }
                )
            }
            detailState.studio != null -> {
                DetailContent(detailState.studio)
            }
            else -> {
                ErrorScreen(
                    errorMessage = "Студия не найдена",
                    onRetry = { viewModel.selectItem(itemId) }
                )
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun DetailContent(studio: com.example.practice.domain.model.Studio?) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (poster, title, chips, stats, created, updated, actions) = createRefs()

        // Постер/обложка
        Card(
            modifier = Modifier
                .constrainAs(poster) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE0E0E0))
            )
        }

        // Заголовок
        Text(
            text = studio?.title ?: "Студия не найдена",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(poster.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        // Чипы: типы
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.constrainAs(chips) {
                top.linkTo(title.bottom, margin = 8.dp)
                start.linkTo(parent.start)
            }
        ) {
            Chip(text = studio?.subType ?: "—")
            Chip(text = studio?.type ?: "—")
        }

        // Статистика
        Text(
            text = "Фильмы: ${studio?.movies?.size ?: 0}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(stats) {
                top.linkTo(chips.bottom, margin = 12.dp)
                start.linkTo(parent.start)
            }
        )

        // Даты
        Text(
            text = "Создана: ${studio?.createdAt ?: ""}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(created) {
                top.linkTo(stats.bottom, margin = 16.dp)
                start.linkTo(parent.start)
            }
        )
        Text(
            text = "Обновлена: ${studio?.updatedAt ?: ""}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(updated) {
                top.linkTo(created.bottom, margin = 4.dp)
                start.linkTo(parent.start)
            }
        )

        // Панель действий внизу
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.constrainAs(actions) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        ) {
            Button(onClick = { /* TODO: share */ }, modifier = Modifier.weight(1f)) {
                Text("Поделиться")
            }
            Button(onClick = { /* TODO: favorite */ }, modifier = Modifier.weight(1f)) {
                Text("В избранное")
            }
        }
    }
}

@Composable
private fun Chip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
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


