package com.example.practice.ui.profile

import android.net.Uri
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.practice.domain.model.Profile

@Composable
fun ProfileScreen(
    onEdit: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (profile.avatarUri.isBlank()) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "avatar placeholder",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(120.dp)
                        .clickable { onEdit() }
                )
            } else {
                val painter = rememberAsyncImagePainter(model = profile.avatarUri)
                Image(
                    painter = painter,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(120.dp)
                        .clickable { onEdit() },
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(text = profile.fullName.ifBlank { "Ваше имя" }, style = MaterialTheme.typography.titleLarge)
            if (profile.position.isNotBlank()) {
                Text(text = profile.position, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                val url = profile.resumeUrl
                if (url.isBlank()) {
                    Toast.makeText(context, "Ссылка на резюме не задана", Toast.LENGTH_SHORT).show()
                } else {
                    enqueueDownloadAndOpen(context, url)
                }
            }) {
                Text("Резюме")
            }
        }

        FloatingActionButton(
            onClick = onEdit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
        }
    }
}



private fun enqueueDownloadAndOpen(context: Context, url: String) {
    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle("Резюме")
        .setDescription("Загрузка файла")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "resume.pdf")
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val downloadId = dm.enqueue(request)

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) ?: -1L
            if (id == downloadId) {
                try {
                    // Проверим статус и получите Uri надёжно
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = dm.query(query)
                    cursor.use {
                        if (it != null && it.moveToFirst()) {
                            val statusIdx = it.getColumnIndex(DownloadManager.COLUMN_STATUS)
                            val status = if (statusIdx != -1) it.getInt(statusIdx) else -1
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                val contentUri = dm.getUriForDownloadedFile(downloadId)
                                val finalUri = contentUri ?: run {
                                    val localIdx = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                                    val local = if (localIdx != -1) it.getString(localIdx) else null
                                    if (local != null) Uri.parse(local) else null
                                }
                                if (finalUri != null) {
                                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(finalUri, "application/pdf")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    // Запустим chooser на случай отсутствия дефолтного PDF-вьюера
                                    val chooser = Intent.createChooser(openIntent, "Открыть резюме")
                                    context.startActivity(chooser)
                                    return
                                }
                            }
                        }
                    }
                    // Фоллбэк: откроем системные загрузки
                    val downloadsIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(downloadsIntent)
                } catch (_: ActivityNotFoundException) {
                    val downloadsIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(downloadsIntent)
                } finally {
                    try {
                        context.unregisterReceiver(this)
                    } catch (_: IllegalArgumentException) {
                        // уже отписан
                    }
                }
            }
        }
    }

    val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    if (Build.VERSION.SDK_INT >= 33) {
        context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
    } else {
        context.registerReceiver(receiver, filter)
    }
}
