package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.utils.AudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyFlowScreen(
    onBack: () -> Unit,
    onConfirmEmergency: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var isSafe by remember { mutableStateOf<Boolean?>(null) }
    var incidentType by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var hasRecordedAudio by remember { mutableStateOf(false) }
    
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isRecording = true
            audioRecorder.startRecording()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) attachedFileName = "foto_evidencia.jpg"
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) cameraLauncher.launch(null)
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) attachedFileName = "documento_anexo.pdf"
    }

    val onToggleRecording: () -> Unit = {
        if (isRecording) {
            audioRecorder.stopRecording()
            isRecording = false
            hasRecordedAudio = true
        } else {
            recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergência") },
                navigationIcon = {
                    IconButton(onClick = { if (step == 2) step = 1 else onBack() }) {
                        Icon(Icons.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (step == 1) {
                Step1(
                    isSafe = isSafe,
                    onSafeChange = { isSafe = it },
                    incidentType = incidentType,
                    onTypeChange = { incidentType = it },
                    isRecording = isRecording,
                    hasRecordedAudio = hasRecordedAudio,
                    onToggleRecording = onToggleRecording,
                    attachedFileName = attachedFileName,
                    onAttachClick = { showAttachmentSheet = true },
                    onNext = { step = 2 }
                )
            } else {
                Step2(onConfirmEmergency = onConfirmEmergency)
            }
        }
        
        if (showAttachmentSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAttachmentSheet = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        "Anexar Evidência",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp)
                    )
                    ListItem(
                        headlineContent = { Text("Tirar Foto") },
                        leadingContent = { Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.clickable {
                            showAttachmentSheet = false
                            if (hasCameraPermission) {
                                cameraLauncher.launch(null)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Escolher Arquivo") },
                        leadingContent = { Icon(Icons.Filled.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.clickable {
                            showAttachmentSheet = false
                            filePickerLauncher.launch("*/*")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Step1(
    isSafe: Boolean?,
    onSafeChange: (Boolean) -> Unit,
    incidentType: String?,
    onTypeChange: (String) -> Unit,
    isRecording: Boolean,
    hasRecordedAudio: Boolean,
    onToggleRecording: () -> Unit,
    attachedFileName: String?,
    onAttachClick: () -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Passo 1 de 2", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Você está em segurança?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { onSafeChange(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSafe == true) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                    )
                ) { Text("Sim") }
                OutlinedButton(
                    onClick = { onSafeChange(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSafe == false) MaterialTheme.colorScheme.errorContainer else Color.Transparent
                    )
                ) { Text("Não") }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = isSafe != null) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "O que aconteceu?",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTypeChange("Roubo") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (incidentType == "Roubo") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        PaddingValues(16.dp).let {
                            Text("Fui Assaltado / Roubo", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTypeChange("Furto") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (incidentType == "Furto") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        PaddingValues(16.dp).let {
                            Text("Veículo Estacionado Sumiu / Furto", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = incidentType != null) {
                Column(modifier = Modifier.padding(top = 32.dp)) {
                    Text("Evidências (Opcional)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        EvidenceButton(
                            text = if (isRecording) "Gravando..." else if (hasRecordedAudio) "Áudio" else "Gravar Voz",
                            icon = if (isRecording) Icons.Filled.Stop else if (hasRecordedAudio) Icons.Filled.Check else Icons.Filled.Mic,
                            isActive = isRecording,
                            isDone = hasRecordedAudio,
                            onClick = onToggleRecording,
                            modifier = Modifier.weight(1f)
                        )
                        EvidenceButton(
                            text = if (attachedFileName != null) "Anexado" else "Anexar Doc",
                            icon = if (attachedFileName != null) Icons.Filled.Check else Icons.Filled.AttachFile,
                            isActive = false,
                            isDone = attachedFileName != null,
                            onClick = onAttachClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 16.dp),
            enabled = isSafe != null && incidentType != null,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Avançar", fontSize = 16.sp)
        }
    }
}

@Composable
fun EvidenceButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    isDone: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.errorContainer 
                             else if (isDone) MaterialTheme.colorScheme.primaryContainer 
                             else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = text,
                tint = if (isActive) MaterialTheme.colorScheme.error 
                       else if (isDone) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text, 
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isDone || isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.error 
                            else if (isDone) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun Step2(onConfirmEmergency: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(if (isPressed) 2000 else 200),
        label = "progress"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            progress = 1f
            delay(2000) // Hold duration
            if (isPressed) {
                onConfirmEmergency()
            }
        } else {
            progress = 0f
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Passo 2 de 2", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Localização Capturada",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mini map placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Map, contentDescription = "Mapa", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Av. Brasil, Rio de Janeiro - RJ", modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            "Pressione e segure por 2 segundos para confirmar o alerta.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.error,
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            )
            
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Warning, 
                    contentDescription = "Confirmar", 
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
