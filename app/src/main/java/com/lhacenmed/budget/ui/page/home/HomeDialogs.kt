package com.lhacenmed.budget.ui.page.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lhacenmed.budget.ui.component.BudgetBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpendingSheet(
    shopperName: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, quantity: String, price: Float, description: String?) -> Unit
) {
    var name        by remember { mutableStateOf("") }
    var quantity    by remember { mutableStateOf("") }
    var price       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isValid = name.isNotBlank() && quantity.isNotBlank() && price.toFloatOrNull() != null

    BudgetBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Add Spending",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = shopperName, onValueChange = {},
                label = { Text("Shopper") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true
            )
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Item name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = quantity, onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("1kg, 2 bottles…", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Price (dh)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = {
                    onConfirm(name, quantity, price.toFloat(), description.ifBlank { null })
                    onDismiss()
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsSheet(
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val isValid = amount.toFloatOrNull()?.let { it > 0 } == true

    BudgetBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Add Funds",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = amount, onValueChange = { amount = it },
                    label = { Text("Amount (dh)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Button(
                    onClick = {
                        onConfirm(amount.toFloat())
                        onDismiss()
                    },
                    enabled = isValid,
                    modifier = Modifier.height(56.dp)
                ) { Text("Add") }
            }
        }
    }
}
