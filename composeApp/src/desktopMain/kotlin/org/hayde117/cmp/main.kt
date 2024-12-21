 package org.hayde117.cmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.plcoding.bookpedia.di.initKoin

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