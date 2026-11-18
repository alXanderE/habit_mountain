package com.example.habitmountain.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.habitmountain.data.Habit
import com.example.habitmountain.data.HabitCadence
import com.example.habitmountain.data.HabitRepository
import com.example.habitmountain.ui.theme.Gold
import com.example.habitmountain.ui.theme.Ink
import com.example.habitmountain.ui.theme.InkSoft
import com.example.habitmountain.ui.theme.Mist
import com.example.habitmountain.ui.theme.Snow
import com.example.habitmountain.ui.theme.Teal

@Composable
fun HabitMountainApp() {
    val repository = remember { HabitRepository() }
    var habits by remember { mutableStateOf(repository.habits) }
    var coins by remember { mutableStateOf(repository.coins) }
    var showAddDialog by remember { mutableStateOf(false) }

    fun refresh() {
        habits = repository.habits
        coins = repository.coins
    }

    Scaffold(
        containerColor = Ink,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Teal,
                contentColor = Ink
            ) { Icon(Icons.Default.Add, contentDescription = "Add habit") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Header(coins) }
            item { MountainCard(habits.count { it.completed }, habits.size) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Today's climb", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Snow)
                    Text("${habits.count { it.completed }}/${habits.size} complete", color = Teal, fontSize = 13.sp)
                }
            }
            items(habits, key = { it.id }) { habit ->
                HabitRow(habit) {
                    repository.toggle(habit.id)
                    refresh()
                }
            }
            item { Spacer(Modifier.height(90.dp)) }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, cadence, intervalDays ->
                repository.add(name, cadence, intervalDays)
                refresh()
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun Header(coins: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("HABIT MOUNTAIN", color = Mist, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Keep climbing.", color = Snow, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        Surface(color = InkSoft, shape = RoundedCornerShape(50)) {
            Row(modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("◆", color = Gold, fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(coins.toString(), color = Snow, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MountainCard(completed: Int, total: Int) {
    val progress = if (total == 0) 0f else completed.toFloat() / total
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(InkSoft)
    ) {
        Box(Modifier.fillMaxWidth().height(220.dp)) {
            MountainIllustration(progress)
            Column(Modifier.align(Alignment.TopStart).padding(18.dp)) {
                Text("BASE CAMP", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.6.sp)
                Text("Summit trail", color = Snow, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Level ${1 + completed}", color = Mist, fontSize = 13.sp)
            }
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd).padding(18.dp),
                color = Color(0xCC0E1824), shape = RoundedCornerShape(12.dp)
            ) { Text("${(progress * 100).toInt()}% to the next ledge", modifier = Modifier.padding(10.dp), color = Snow, fontSize = 12.sp) }
        }
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Trail progress", color = Mist, fontSize = 12.sp)
                Text("${completed * 10} m climbed", color = Teal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape), color = Teal, trackColor = Color(0xFF2A3A4A))
        }
    }
}

@Composable
private fun MountainIllustration(progress: Float) {
    Canvas(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF233A4B), Color(0xFF172536))))) {
        drawCircle(Color(0xFFF1B45B).copy(alpha = .18f), radius = 35f, center = Offset(size.width * .78f, size.height * .25f))
        drawMountain(Color(0xFF263F4E), .02f, .84f, 1.1f)
        drawMountain(Color(0xFF1C3241), .12f, .72f, .8f)
        drawMountain(Color(0xFF122733), .22f, .62f, .5f)
        val x = size.width * (.15f + progress.coerceIn(0f, 1f) * .58f)
        val y = size.height * (.78f - progress.coerceIn(0f, 1f) * .43f)
        drawCircle(Teal, 10f, Offset(x, y))
        drawCircle(Snow, 4f, Offset(x, y - 1f))
        drawLine(Teal, Offset(x, y + 10f), Offset(x, y + 24f), 3f)
    }
}

private fun DrawScope.drawMountain(color: Color, left: Float, peak: Float, heightScale: Float) {
    val path = Path().apply {
        moveTo(0f, size.height)
        lineTo(size.width * left, size.height * .83f)
        lineTo(size.width * .45f, size.height * peak)
        lineTo(size.width * .62f, size.height * (.68f * heightScale))
        lineTo(size.width * .86f, size.height * .88f)
        lineTo(size.width, size.height * .68f)
        lineTo(size.width, size.height)
        close()
    }
    drawPath(path, color)
}

@Composable
private fun HabitRow(habit: Habit, onToggle: () -> Unit) {
    Surface(color = InkSoft, shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(42.dp), color = habit.accent.copy(alpha = .18f), shape = RoundedCornerShape(14.dp)) {
                Icon(
                    imageVector = when (habit.cadence) { HabitCadence.WEEKLY -> Icons.Default.Terrain; HabitCadence.CUSTOM -> Icons.Default.Timelapse; else -> Icons.Default.LocalFlorist },
                    contentDescription = null, tint = habit.accent, modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(habit.name, color = Snow, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (habit.cadence == HabitCadence.CUSTOM) "Every ${habit.intervalDays ?: 1} days" else habit.cadence.label,
                        color = Mist,
                        fontSize = 12.sp
                    )
                    Text("  ·  ${habit.streak} day streak", color = habit.accent, fontSize = 12.sp)
                }
            }
            Surface(color = if (habit.completed) Teal else Color(0xFF2A3A4A), shape = CircleShape, modifier = Modifier.size(38.dp)) {
                IconButton(onClick = onToggle) {
                    Icon(if (habit.completed) Icons.Default.Check else Icons.Default.ChevronRight, contentDescription = if (habit.completed) "Completed" else "Complete habit", tint = if (habit.completed) Ink else Snow)
                }
            }
        }
    }
}

@Composable
private fun AddHabitDialog(onDismiss: () -> Unit, onAdd: (String, HabitCadence, Int?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var cadence by remember { mutableStateOf(HabitCadence.DAILY) }
    var intervalDays by remember { mutableStateOf("2") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = InkSoft,
        title = { Text("New habit", color = Snow, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Add a step to your trail.", color = Mist, fontSize = 13.sp)
                OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text("Habit name") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitCadence.entries.forEach { option ->
                        val selected = option == cadence
                        Surface(onClick = { cadence = option }, color = if (selected) Teal else Color(0xFF2A3A4A), shape = RoundedCornerShape(50)) {
                            Text(option.label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = if (selected) Ink else Snow, fontSize = 12.sp)
                        }
                    }
                }
                if (cadence == HabitCadence.CUSTOM) {
                    OutlinedTextField(
                        value = intervalDays,
                        onValueChange = { value -> intervalDays = value.filter(Char::isDigit).take(2) },
                        singleLine = true,
                        label = { Text("Repeat every (days)") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onAdd(name, cadence, intervalDays.toIntOrNull()) },
                enabled = name.isNotBlank() && (cadence != HabitCadence.CUSTOM || intervalDays.toIntOrNull()?.let { it > 0 } == true)
            ) { Text("Add to trail") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Mist) } }
    )
}
