package com.example.todolist.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todolist.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("todo_prefs")

class TodoPreferences(private val context: Context){
    companion object {
        private val TODO_LIST_KEY = stringPreferencesKey("todo_list")
    }

    suspend fun saveTodos(todos: List<Todo>) {
        context.dataStore.edit { preference ->
            val jsonString = todos.joinToString("|")  { "${it.id},${it.title},${it.isDone}" }
            preference[TODO_LIST_KEY] = jsonString
        }
    }

    val todos: Flow<List<Todo>> = context.dataStore.data.map { preference ->
        val jsonString = preference[TODO_LIST_KEY] ?: ""
        jsonString.split("|").filter { it.isNotEmpty() }.map {
            val parts = it.split(",")
            Todo(parts[0].toInt(), parts[1], parts[2].toBoolean())
        }
    }
}