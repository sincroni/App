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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class Vehicle(
    val id: String,
    val name: String,
    val plate: String,
    val icon: ImageVector,
    val status: String
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
    val vehicles = listOf(
        Vehicle("1", "Volvo FH 540", "ABC1D23", Icons.Filled.DirectionsCar, "Proteção Ativa"),
        Vehicle("2", "Honda CB 500F", "XYZ9W87", Icons.Filled.TwoWheeler, "Proteção Ativa"),
        Vehicle("3", "Chevrolet Onix", "BCA4E56", Icons.Filled.DirectionsCar, "Em Manutenção")
    )
    val pagerState = rememberPagerState(pageCount = { vehicles.size })
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                    // Simulando sincronização com o backend
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (vehicles[pagerState.currentPage].status == "Proteção Ativa") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            vehicles[pagerState.currentPage].status, 
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (vehicles[pagerState.currentPage].status == "Proteção Ativa") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error, 
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Alternar Tema"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Vehicle Pager
            HorizontalPager(
                state = pagerState,
                pageSpacing = 16.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) { page ->
                val vehicle = vehicles[page]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToStatus() },
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
                repeat(vehicles.size) { iteration ->
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
