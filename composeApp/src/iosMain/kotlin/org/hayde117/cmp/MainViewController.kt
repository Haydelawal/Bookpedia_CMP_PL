package org.hayde117.cmp

import androidx.compose.ui.window.ComposeUIViewController
import com.plcoding.bookpedia.di.initKoin
import org.hayde117.cmp.app.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }