package org.hayde117.cmp

import androidx.compose.ui.window.ComposeUIViewController
import org.hayde117.cmp.di.initKoin
import org.hayde117.cmp.app.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }