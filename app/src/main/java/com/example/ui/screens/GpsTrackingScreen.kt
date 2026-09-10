package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsTrackingScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telemetria") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Simulated Map Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE5E5E5)) // Map placeholder color
            ) {
                // Map grid lines simulation
                for (i in 1..10) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).offset(y = (i * 100).dp).background(Color.White.copy(alpha = 0.5f)))
                    Box(modifier = Modifier.fillMaxHeight().width(1.dp).offset(x = (i * 50).dp).background(Color.White.copy(alpha = 0.5f)))
                }
                
                // Vehicle Marker
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-50).dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Navigation, contentDescription = "Veículo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                }
            }

            // Map Controls
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .offset(y = (-50).dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallFloatingActionButton(onClick = {}, containerColor = MaterialTheme.colorScheme.surface) {
                    Icon(Icons.Filled.MyLocation, "Centralizar")
                }
                SmallFloatingActionButton(onClick = {}, containerColor = MaterialTheme.colorScheme.surface) {
                    Icon(Icons.Filled.History, "Histórico")
                }
                SmallFloatingActionButton(onClick = {}, containerColor = MaterialTheme.colorScheme.surface) {
                    Icon(Icons.Filled.Fence, "Cerca Virtual")
                }
            }

            // Telemetry Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp), // Extra padding for system nav bar
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Última transmissão: 10 seg atrás", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Online", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Av. Paulista, 1578 - São Paulo, SP", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryItem("Velocidade", "65 km/h", Icons.Filled.Speed)
                        TelemetryItem("Ignição", "Ligada", Icons.Filled.Power)
                        TelemetryItem("Bateria", "12.4V", Icons.Filled.BatteryFull)
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}
