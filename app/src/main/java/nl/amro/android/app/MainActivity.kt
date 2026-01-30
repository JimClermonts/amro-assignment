package nl.amro.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import nl.amro.android.app.ui.movies.list.MoviesScreen
import nl.amro.android.app.ui.theme.AmroMoviesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmroMoviesTheme {
                MoviesScreen(
                    onMovieClick = { movie ->
                        // TODO: Navigate to detail screen (Step 9)
                    }
                )
            }
        }
    }
}
