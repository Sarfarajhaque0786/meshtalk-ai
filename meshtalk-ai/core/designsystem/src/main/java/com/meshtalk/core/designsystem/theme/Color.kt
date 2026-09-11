package com.meshtalk.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Base palette. Kept minimal in the skeleton; expand into a full tonal palette
// (M3 dynamic color / custom scheme) once brand direction is finalized.
val MeshPrimary = Color(0xFF3E6259)
val MeshOnPrimary = Color(0xFFFFFFFF)
val MeshSecondary = Color(0xFF5C6B73)
val MeshBackground = Color(0xFFF7F7F5)
val MeshSurface = Color(0xFFFFFFFF)
val MeshError = Color(0xFFB3261E)

val MeshPrimaryDark = Color(0xFFA6D0C4)
val MeshOnPrimaryDark = Color(0xFF12352C)
val MeshSecondaryDark = Color(0xFFB4C4CD)
val MeshBackgroundDark = Color(0xFF121412)
val MeshSurfaceDark = Color(0xFF1B1D1B)
val MeshErrorDark = Color(0xFFF2B8B5)

// Reserved for emergency / SOS UI states - deliberately high-contrast and distinct
// from the standard palette so it reads as urgent even in dark mode.
val MeshEmergency = Color(0xFFD32F2F)
