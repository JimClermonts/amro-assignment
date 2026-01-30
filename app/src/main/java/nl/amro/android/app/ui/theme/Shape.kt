package nl.amro.android.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for AMRO Movies app.
 * Following Material Design 3 shape scale.
 */
val Shapes = Shapes(
    // Extra small - for small components like chips
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small - for cards, text fields
    small = RoundedCornerShape(8.dp),
    
    // Medium - for cards, dialogs
    medium = RoundedCornerShape(12.dp),
    
    // Large - for large cards, sheets
    large = RoundedCornerShape(16.dp),
    
    // Extra large - for full-screen sheets
    extraLarge = RoundedCornerShape(28.dp)
)
