package nl.amro.android.app.ui.movies.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
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
import nl.amro.android.app.model.Genre
import nl.amro.android.app.ui.theme.AmroMoviesTheme

/**
 * Dropdown menu for filtering movies by genre.
 * Shows checkmarks for selected genres.
 */
@Composable
fun FilterDropdown(
    genres: List<Genre>,
    selectedGenreIds: Set<Int>,
    onGenreToggle: (Int) -> Unit,
    onFilterOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = {
                onFilterOpen()
                expanded = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = stringResource(R.string.filter),
                tint = if (selectedGenreIds.isNotEmpty()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (genres.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.loading_genres)) },
                    onClick = { }
                )
            } else {
                genres.forEach { genre ->
                    val isSelected = genre.id in selectedGenreIds
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
                                    text = genre.name,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        },
                        onClick = { onGenreToggle(genre.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterDropdownPreview() {
    AmroMoviesTheme {
        FilterDropdown(
            genres = listOf(
                Genre(id = 28, name = "Action"),
                Genre(id = 12, name = "Adventure"),
                Genre(id = 35, name = "Comedy")
            ),
            selectedGenreIds = setOf(28),
            onGenreToggle = {},
            onFilterOpen = {}
        )
    }
}
