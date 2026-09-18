package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.utils.NetworkMonitor
import androidx.compose.animation.AnimatedVisibility

data class Vehicle(
    val id: String,
    val name: String,
    val plate: String,
    val icon: ImageVector,
    val status: String,
    val registrationYear: String,
    val chassis: String,
    val insurancePolicy: String,
    val insuranceExpiry: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToTowing: () -> Unit,
    onNavigateToGps: () -> Unit,
    onNavigateToStatus: () -> Unit
) {
    val allVehicles = listOf(
        Vehicle("1", "Volvo FH 540", "ABC1D23", Icons.Filled.DirectionsCar, "Proteção Ativa", "2023/2024", "9BW ZZZ 370 4T 123456", "APL-59281-23", "12/10/2026"),
        Vehicle("2", "Honda CB 500F", "XYZ9W87", Icons.Filled.TwoWheeler, "Proteção Ativa", "2022/2022", "9C2 JC64 10 4R 987654", "APL-39281-22", "05/03/2026"),
        Vehicle("3", "Chevrolet Onix", "BCA4E56", Icons.Filled.DirectionsCar, "Em Manutenção", "2020/2021", "9BG KS19 40 4B 456789", "APL-12903-21", "22/11/2025")
    )
    
    var searchQuery by remember { mutableStateOf("") }
    val filteredVehicles = remember(searchQuery) {
        allVehicles.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.plate.contains(searchQuery, ignoreCase = true)
        }
    }

    val pagerState = rememberPagerState(pageCount = { filteredVehicles.size })
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    
    val currentVehicle = filteredVehicles.getOrNull(pagerState.currentPage)
    
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle(initialValue = true)

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToEmergency,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Warning, contentDescription = "Emergência") },
                text = { Text("REPORTAR ROUBO OU FURTO", fontWeight = FontWeight.Bold) },
                modifier = Modifier.padding(16.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    delay(1500)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Olá, Carlos", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                        if (currentVehicle != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (currentVehicle.status == "Proteção Ativa") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    currentVehicle.status, 
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (currentVehicle.status == "Proteção Ativa") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error, 
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedVisibility(visible = !isOnline) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudOff,
                                        contentDescription = "Offline",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Offline",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Alternar Tema"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    placeholder = { Text("Buscar por nome ou placa") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Vehicle Pager
                if (filteredVehicles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum veículo encontrado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 16.dp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) { page ->
                        val vehicle = filteredVehicles[page]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedVehicle = vehicle
                                    showBottomSheet = true 
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (vehicle.status == "Proteção Ativa") MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            vehicle.status,
                                            color = if (vehicle.status == "Proteção Ativa") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                    Text(vehicle.plate, style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Icon(
                                    imageVector = vehicle.icon,
                                    contentDescription = vehicle.name,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.CenterHorizontally),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    vehicle.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pager Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(filteredVehicles.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(if (pagerState.currentPage == iteration) 8.dp else 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Atalhos Rápidos", 
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Quick Actions Grid
                val actions = listOf(
                    Pair("Solicitar Guincho", Icons.Filled.Build) to onNavigateToTowing,
                    Pair("Rastrear no Mapa", Icons.Filled.Map) to onNavigateToGps,
                    Pair("Clube de Benefícios", Icons.Filled.CardGiftcard) to {},
                    Pair("Cotação", Icons.Filled.AttachMoney) to {}
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(actions) { action ->
                        QuickActionCard(
                            title = action.first.first,
                            icon = action.first.second,
                            onClick = action.second
                        )
                    }
                }
            }
        }

        if (showBottomSheet && selectedVehicle != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                VehicleDetailsSheet(vehicle = selectedVehicle!!)
            }
        }
    }
}

@Composable
fun VehicleDetailsSheet(vehicle: Vehicle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = vehicle.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(vehicle.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text("Placa: ${vehicle.plate}", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Documentação", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        DetailRow("Ano/Modelo", vehicle.registrationYear)
        Spacer(modifier = Modifier.height(12.dp))
        DetailRow("Chassi", vehicle.chassis)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Seguro / Proteção", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        DetailRow("Apólice", vehicle.insurancePolicy)
        Spacer(modifier = Modifier.height(12.dp))
        DetailRow("Validade", vehicle.insuranceExpiry)
        Spacer(modifier = Modifier.height(12.dp))
        DetailRow(
            "Status", 
            vehicle.status, 
            valueColor = if (vehicle.status == "Proteção Ativa") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    val finalColor = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = finalColor))
    }
}

@Composable
fun QuickActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title, 
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )
        }
    }
}
