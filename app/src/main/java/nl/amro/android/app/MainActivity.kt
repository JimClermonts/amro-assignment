package nl.amro.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import nl.amro.android.app.ui.navigation.AmroNavHost
import nl.amro.android.app.ui.theme.AmroMoviesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmroMoviesTheme {
                val navController = rememberNavController()
                AmroNavHost(navController = navController)
            }
        }
    }
}
