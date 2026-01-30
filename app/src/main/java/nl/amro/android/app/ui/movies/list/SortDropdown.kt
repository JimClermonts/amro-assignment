package nl.amro.android.app.ui.movies.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.amro.android.app.R
import nl.amro.android.app.domain.SortOption
import nl.amro.android.app.ui.theme.AmroMoviesTheme

/**
 * Combined dropdown with 6 sort options.
 */
@Composable
fun SortDropdown(
    currentSortOption: SortOption,
    onSortOptionSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = stringResource(R.string.sort),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach { option ->
                val isSelected = option == currentSortOption
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = option.getDisplayName(),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    },
                    onClick = {
                        onSortOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Get display name for sort option from string resources.
 */
@Composable
private fun SortOption.getDisplayName(): String {
    return when (this) {
        SortOption.POPULARITY_DESC -> stringResource(R.string.sort_popularity_desc)
        SortOption.POPULARITY_ASC -> stringResource(R.string.sort_popularity_asc)
        SortOption.TITLE_ASC -> stringResource(R.string.sort_title_asc)
        SortOption.TITLE_DESC -> stringResource(R.string.sort_title_desc)
        SortOption.RELEASE_DATE_DESC -> stringResource(R.string.sort_release_date_desc)
        SortOption.RELEASE_DATE_ASC -> stringResource(R.string.sort_release_date_asc)
    }
}

@Preview(showBackground = true)
@Composable
private fun SortDropdownPreview() {
    AmroMoviesTheme {
        SortDropdown(
            currentSortOption = SortOption.POPULARITY_DESC,
            onSortOptionSelected = {}
        )
    }
}
