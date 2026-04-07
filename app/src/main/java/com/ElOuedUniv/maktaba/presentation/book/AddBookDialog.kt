package com.ElOuedUniv.maktaba.presentation.book

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialog for adding a new book
 * Contains input fields for title, ISBN, and number of pages.
 *
 * @param onDismiss Called when the user dismisses the dialog
 * @param onConfirm Called when the user confirms adding the book
 */
@Composable
fun AddBookDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, isbn: String, nbPages: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var pages by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add New Book")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pages,
                    onValueChange = { pages = it },
                    label = { Text("Number of Pages") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val nbPages = pages.toIntOrNull() ?: 0
                    if (title.isNotBlank()) {
                        onConfirm(title, isbn, nbPages)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
