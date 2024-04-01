package com.mgws.adownkyi.ui.compose.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mgws.adownkyi.R
import com.mgws.adownkyi.model.home.VideoItemUiState

@Composable
fun VideoListScreen(
    videoItemUiStates: List<VideoItemUiState>,
    onClick: (List<VideoItemUiState>) -> Unit,
) {

    var selectedAll by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            //选择所有视频
            Checkbox(
                checked = selectedAll,
                onCheckedChange = {
                    selectedAll = !selectedAll
                    videoItemUiStates
                        .parallelStream()
                        .filter { it.isSelected != selectedAll }
                        .forEach { it.isSelected = selectedAll }
                }
            )
            Text(stringResource(R.string.select_all))
        }
        TextButton(onClick = { onClick(videoItemUiStates.filter { it.isSelected }) }) {
            Text(stringResource(R.string.download))
        }
    }

    VideoList(videoItemUiStates) {
        selectedAll = videoItemUiStates.all { predicate -> predicate.isSelected }
    }

}