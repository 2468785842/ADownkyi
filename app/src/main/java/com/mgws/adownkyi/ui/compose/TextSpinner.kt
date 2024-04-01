package com.mgws.adownkyi.ui.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun TextSpinner(
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(if (options.isNotEmpty()) options[0] else "None")
    }

    TextSpinner(
        modifier,
        options,
        expanded,
        selectedOptionText = selectedOptionText,
        {
            selectedOptionText = it
            expanded = false
            onSelect(it)
        },
        { expanded = false },
        { expanded = !expanded }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSpinner(
    modifier: Modifier = Modifier,
    options: List<String>,
    expanded: Boolean,
    selectedOptionText: String,
    onSelect: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
) {

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {

        Row(
            modifier = Modifier.menuAnchor(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = selectedOptionText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(onClick = { onSelect(selectionOption) }, text = {
                    Text(text = selectionOption)
                })
            }
        }

    }
}
