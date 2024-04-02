package com.mgws.adownkyi.ui.compose.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mgws.adownkyi.R

@Composable
fun SearchUserInput(
    query: String,
    history: List<String>,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onDelHistory: (String) -> Unit,
) {
    var active by remember { mutableStateOf(false) }

    SearchUserInput(
        query, active, history,
        onSearch = {
            active = false
            onSearch(it)
        },
        onQueryChange = onQueryChange,
        onDelHistory = onDelHistory,
        onActiveChange = { active = it },
        onClear = onClear,
        onBack = { active = false },
        modifier
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchUserInput(
    query: String,
    active: Boolean,
    historyList: List<String>,
    onSearch: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onDelHistory: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,
        onActiveChange = onActiveChange,
        placeholder = {},
        leadingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.search_back),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable(onClick = onBack),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search),
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        },
        trailingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.search_clear),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable { onClear() },
                )
            } else {
                //显示用户头像:TODO
            }
        },
    ) {
        //History
        LazyColumn {
            items(items = historyList) { history ->
                ListItem(
                    headlineContent = {},
                    trailingContent = {
                        IconButton(onClick = { onDelHistory(history) }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    },
                    supportingContent = {
                        Text(
                            text = history,
                            modifier = Modifier.clickable { onSearch(history) },
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}
