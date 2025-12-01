package com.example.practice.ui.profile

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.practice.feature.profile.R
import com.example.practice.notification.FavoritePairReminderScheduler
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onDone: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val notificationPermissionCallback = remember { mutableStateOf<(() -> Unit)?>(null) }

    val fullNameText = rememberSaveable { mutableStateOf(profile.fullName) }
    val positionText = rememberSaveable { mutableStateOf(profile.position) }
    val resumeUrlText = rememberSaveable { mutableStateOf(profile.resumeUrl) }
    val favoritePairTimeText = rememberSaveable { mutableStateOf(profile.favoritePairTime) }

    // Флаги, чтобы не перетирать пользовательский ввод при обновлении profile
    val isFullNameDirty = remember { mutableStateOf(false) }
    val isPositionDirty = remember { mutableStateOf(false) }
    val isResumeUrlDirty = remember { mutableStateOf(false) }
    val isFavoritePairTimeDirty = remember { mutableStateOf(false) }

    val favoritePairTimeError = remember { mutableStateOf<Int?>(null) }
    val timeRegex = remember { Regex("^([01]\\d|2[0-3]):[0-5]\\d$") }

    LaunchedEffect(profile.fullName) {
        if (!isFullNameDirty.value) fullNameText.value = profile.fullName
    }
    LaunchedEffect(profile.position) {
        if (!isPositionDirty.value) positionText.value = profile.position
    }
    LaunchedEffect(profile.resumeUrl) {
        if (!isResumeUrlDirty.value) resumeUrlText.value = profile.resumeUrl
    }
    LaunchedEffect(profile.favoritePairTime) {
        if (!isFavoritePairTimeDirty.value) {
            favoritePairTimeText.value = profile.favoritePairTime
            favoritePairTimeError.value = validateFavoritePairTime(profile.favoritePairTime, timeRegex)
        }
    }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateAvatar(uri.toString())
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { viewModel.updateAvatar(it.toString()) }
        }
    }

    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            cameraImageUri.value = uri
            takePictureLauncher.launch(uri)
        }
    }

    val requestReadImagesPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val callback = notificationPermissionCallback.value
        notificationPermissionCallback.value = null
        if (granted) {
            callback?.invoke()
        } else {
            Toast.makeText(
                context,
                R.string.favorite_pair_notifications_permission_denied,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun chooseAvatarFromGallery() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            requestReadImagesPermission.launch(permission)
        }
    }

    fun takePhoto() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            val uri = createImageUri(context)
            cameraImageUri.value = uri
            takePictureLauncher.launch(uri)
        } else {
            requestCameraPermission.launch(permission)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") },
                actions = {
                    val isSaveEnabled = favoritePairTimeError.value == null && favoritePairTimeText.value.isNotBlank()
                    TextButton(
                        onClick = {
                            val currentTimeValue = favoritePairTimeText.value.trim()
                            favoritePairTimeError.value = validateFavoritePairTime(currentTimeValue, timeRegex)
                            if (favoritePairTimeError.value != null) return@TextButton

                            fun performSave() {
                                coroutineScope.launch {
                                    viewModel.updateFullName(fullNameText.value)
                                    viewModel.updatePosition(positionText.value)
                                    viewModel.updateResumeUrl(resumeUrlText.value)
                                    viewModel.updateFavoritePairTime(currentTimeValue)
                                    viewModel.save()

                                    val scheduled = FavoritePairReminderScheduler.schedule(
                                        context = context,
                                        fullName = fullNameText.value,
                                        favoritePairTime = currentTimeValue
                                    )
                                    if (!scheduled) {
                                        Toast.makeText(
                                            context,
                                            "Не удалось запланировать уведомление. Проверьте разрешения.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    onDone()
                                }
                            }

                            val notificationsGranted = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            } else {
                                true
                            }

                            if (!notificationsGranted) {
                                notificationPermissionCallback.value = { performSave() }
                                Toast.makeText(
                                    context,
                                    R.string.favorite_pair_notifications_permission_rationale,
                                    Toast.LENGTH_LONG
                                ).show()
                                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                                return@TextButton
                            }

                            performSave()
                        },
                        enabled = isSaveEnabled
                    ) { Text("Готово") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val avatarMenuExpanded = remember { mutableStateOf(false) }
            Box {
                if (profile.avatarUri.isBlank()) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "avatar placeholder",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(120.dp)
                            .clickable { avatarMenuExpanded.value = true },
                    )
                } else {
                    val painter = rememberAsyncImagePainter(model = profile.avatarUri)
                    Image(
                        painter = painter,
                        contentDescription = "avatar",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(120.dp)
                            .clickable { avatarMenuExpanded.value = true },
                        contentScale = ContentScale.Crop
                    )
                }

                DropdownMenu(
                    expanded = avatarMenuExpanded.value,
                    onDismissRequest = { avatarMenuExpanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Выбрать из галереи") },
                        onClick = {
                            avatarMenuExpanded.value = false
                            chooseAvatarFromGallery()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Сделать фото") },
                        onClick = {
                            avatarMenuExpanded.value = false
                            takePhoto()
                        }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = fullNameText.value,
                onValueChange = { value ->
                    fullNameText.value = value
                    isFullNameDirty.value = true
                },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = positionText.value,
                onValueChange = { value ->
                    positionText.value = value
                    isPositionDirty.value = true
                },
                label = { Text("Должность") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = resumeUrlText.value,
                onValueChange = { value ->
                    resumeUrlText.value = value
                    isResumeUrlDirty.value = true
                },
                label = { Text("URL резюме (pdf)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            FavoritePairTimeField(
                valueState = favoritePairTimeText,
                errorState = favoritePairTimeError,
                isDirtyState = isFavoritePairTimeDirty,
                timeRegex = timeRegex
            )
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File.createTempFile("JPEG_${'$'}timeStamp_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", imageFile)
}

@Composable
private fun FavoritePairTimeField(
    valueState: MutableState<String>,
    errorState: MutableState<Int?>,
    isDirtyState: MutableState<Boolean>,
    timeRegex: Regex
) {
    val context = LocalContext.current
    val label = stringResource(R.string.favorite_pair_time_label)

    fun openTimePicker() {
        val initialHour: Int
        val initialMinute: Int
        val parts = valueState.value.split(":")
        if (parts.size == 2) {
            initialHour = parts[0].toIntOrNull() ?: 8
            initialMinute = parts[1].toIntOrNull() ?: 0
        } else {
            initialHour = 8
            initialMinute = 0
        }
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val formatted = "%02d:%02d".format(hour, minute)
                valueState.value = formatted
                isDirtyState.value = true
                errorState.value = validateFavoritePairTime(formatted, timeRegex)
            },
            initialHour,
            initialMinute,
            true
        ).show()
    }

    OutlinedTextField(
        value = valueState.value,
        onValueChange = { value ->
            valueState.value = value
            isDirtyState.value = true
            errorState.value = validateFavoritePairTime(value, timeRegex)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = {
            IconButton(onClick = { openTimePicker() }) {
                Icon(imageVector = Icons.Filled.AccessTime, contentDescription = label)
            }
        },
        isError = errorState.value != null
    )
    if (errorState.value != null) {
        Text(
            text = stringResource(errorState.value!!),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

private fun validateFavoritePairTime(value: String, timeRegex: Regex): Int? {
    if (value.isBlank()) return R.string.favorite_pair_time_error_empty
    val trimmed = value.trim()
    return when {
        trimmed.isEmpty() -> R.string.favorite_pair_time_error_empty
        !timeRegex.matches(trimmed) -> R.string.favorite_pair_time_error_format
        else -> null
    }
}


