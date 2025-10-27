package com.example.practice.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FilterScreen(
    onBack: () -> Unit,
    viewModel: FilterViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf(viewModel.uiState.value.searchQuery) }
    var selectedType by remember { mutableStateOf(viewModel.uiState.value.type) }
    var minMoviesCount by remember { mutableStateOf(viewModel.uiState.value.minMoviesCount) }
    var maxMoviesCount by remember { 
        mutableStateOf(
            if (viewModel.uiState.value.maxMoviesCount == Int.MAX_VALUE) "" 
            else viewModel.uiState.value.maxMoviesCount.toString()
        ) 
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Фильтры",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Поиск по названию
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Поиск по названию",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Название студии") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Выбор типа студии
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Тип студии",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedType == "",
                        onClick = { selectedType = "" }
                    )
                    Text("Все")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedType == "Производство",
                        onClick = { selectedType = "Производство" }
                    )
                    Text("Производство")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedType == "Спецэффекты",
                        onClick = { selectedType = "Спецэффекты" }
                    )
                    Text("Спецэффекты")
                }
            }
        }
        
        // Количество фильмов
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Количество фильмов",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (minMoviesCount == 0) "" else minMoviesCount.toString(),
                        onValueChange = { 
                            val value = it.toIntOrNull() ?: 0
                            if (value >= 0) {
                                minMoviesCount = value
                            }
                        },
                        label = { Text("От") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = maxMoviesCount,
                        onValueChange = { maxMoviesCount = it },
                        label = { Text("До") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Кнопки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }
            
            Button(
                onClick = {
                    val maxCount = maxMoviesCount.toIntOrNull() ?: Int.MAX_VALUE
                    viewModel.saveFilters(
                        searchQuery = searchQuery,
                        type = selectedType,
                        minMoviesCount = minMoviesCount,
                        maxMoviesCount = maxCount
                    )
                    onBack()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Готово")
            }
        }
    }
}
