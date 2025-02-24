package com.example.todolist

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.data.TodoPreferences
import kotlinx.coroutines.launch

data class Todo(
    val id: Int,
    val title: String,
    val isDone: Boolean
)

@Composable
fun TodoApp(context: Context) {
    val todoPreferences = remember { TodoPreferences(context) }
    var todos = remember { mutableStateListOf<Todo>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        todoPreferences.todos.collect { savedTodos ->
            todos.clear()
            todos.addAll(savedTodos)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
            items(todos) { todo ->
                TodoItem(todo = todo, onDelete = { id ->
                    todos.removeIf { it.id == id }
                    coroutineScope.launch { todoPreferences.saveTodos(todos) }
                }, onToggle = { id ->
                    val index = todos.indexOfFirst { it.id == id }
                    if (index != -1) {
                        todos[index] = todos[index].copy(isDone = !todos[index].isDone)
                        coroutineScope.launch { todoPreferences.saveTodos(todos) }
                    }
                })
            }
        }

        Button(onClick = {
            val newTodo = Todo(id = todos.size, title = "Todos ${todos.size + 1}", isDone = false)
            todos.add(newTodo)
            coroutineScope.launch { todoPreferences.saveTodos(todos) }
        }, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)) {
            Text("Tambah Tugas")
        }
    }

}

@Composable
fun TodoItem(todo: Todo, onDelete: (Int) -> Unit, onToggle: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(todo.title, style = MaterialTheme.typography.bodyLarge)
            Text(if (todo.isDone) "Selesai" else "Belum", style = MaterialTheme.typography.bodySmall)
        }

        Row {
            Button(onClick = { onToggle(todo.id)}) {
                Text(if (todo.isDone) "Batal" else "Selesai")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {onDelete(todo.id)}) {
                Text("Hapus")
            }
        }
    }
}