
package com.example.composepractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Purple = Color(0xFF6650A4)
private val LightPurple = Color(0xFFF0EBFA)
private val PageBackground = Color(0xFFF8F7FC)

data class Task(
    val id: Int,
    val title: String,
    val category: String,
    val done: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                TaskFlowApp()
            }
        }
    }
}

@Composable
fun TaskFlowApp() {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(1, "Learn Kotlin basics", "Learning", true),
                Task(2, "Practice Compose UI", "Development"),
                Task(3, "Build my first app", "Project"),
                Task(4, "Review today's work", "Learning")
            )
        )
    }

    var taskTitle by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var nextId by remember { mutableIntStateOf(5) }

    val completed = tasks.count { it.done }
    val pending = tasks.size - completed

    val filteredTasks = tasks.filter { task ->
        when (selectedFilter) {
            "Pending" -> !task.done
            "Completed" -> task.done
            else -> true
        }
    }

    Scaffold(
        containerColor = PageBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = "TASKFLOW",
                color = Purple,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "My Tasks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Plan your day. Get things done.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Purple
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "YOUR DAILY PROGRESS",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "$completed of ${tasks.size} tasks completed",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = {
                            if (tasks.isEmpty()) 0f
                            else completed.toFloat() / tasks.size
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        if (tasks.isEmpty()) "Add a task to get started"
                        else "$pending tasks remaining",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    title = "Total",
                    value = tasks.size,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Pending",
                    value = pending,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Done",
                    value = completed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Add a new task",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("What do you need to do?") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val title = taskTitle.trim()
                    if (title.isNotEmpty()) {
                        tasks = tasks + Task(
                            id = nextId,
                            title = title,
                            category = "Personal"
                        )
                        nextId++
                        taskTitle = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Task")
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "My task list",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Pending", "Completed").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tasks here yet!",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggle = {
                                tasks = tasks.map {
                                    if (it.id == task.id) {
                                        it.copy(done = !it.done)
                                    } else it
                                }
                            },
                            onDelete = {
                                tasks = tasks.filterNot {
                                    it.id == task.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 14.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value.toString(),
                color = Purple,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.done,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Purple
                )
            )

            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.done) Color.Gray
                    else MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    task.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Purple
                )
            }

            if (task.done) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Purple
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = Color.Gray
                )
            }
        }
    }
}
