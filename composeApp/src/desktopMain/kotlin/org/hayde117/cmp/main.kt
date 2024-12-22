 package org.hayde117.cmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.hayde117.cmp.di.initKoin
import org.hayde117.cmp.app.App

 fun main() {

    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Bookpedia_CMP_PL",
        ) {
            App()
        }
    }

}