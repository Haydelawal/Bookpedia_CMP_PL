package org.hayde117.cmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bookpedia_CMP_PL",
    ) {
        App()
    }
}