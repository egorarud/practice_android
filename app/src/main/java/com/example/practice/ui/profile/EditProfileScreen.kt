package com.example.practice.ui.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onDone: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    val fullNameText = rememberSaveable { mutableStateOf(profile.fullName) }
    val positionText = rememberSaveable { mutableStateOf(profile.position) }
    val resumeUrlText = rememberSaveable { mutableStateOf(profile.resumeUrl) }

    // Флаги, чтобы не перетирать пользовательский ввод при обновлении profile
    val isFullNameDirty = remember { mutableStateOf(false) }
    val isPositionDirty = remember { mutableStateOf(false) }
    val isResumeUrlDirty = remember { mutableStateOf(false) }

    LaunchedEffect(profile.fullName) {
        if (!isFullNameDirty.value) fullNameText.value = profile.fullName
    }
    LaunchedEffect(profile.position) {
        if (!isPositionDirty.value) positionText.value = profile.position
    }
    LaunchedEffect(profile.resumeUrl) {
        if (!isResumeUrlDirty.value) resumeUrlText.value = profile.resumeUrl
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

    fun chooseAvatarFromGallery() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
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
                    TextButton(onClick = {
                        viewModel.updateFullName(fullNameText.value)
                        viewModel.updatePosition(positionText.value)
                        viewModel.updateResumeUrl(resumeUrlText.value)
                        viewModel.save()
                        onDone()
                    }) { Text("Готово") }
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
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File.createTempFile("JPEG_${'$'}timeStamp_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", imageFile)
}

 


