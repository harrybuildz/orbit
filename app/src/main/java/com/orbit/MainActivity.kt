package com.orbit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import com.orbit.ui.FeedRoute
import com.orbit.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrbitTheme {
                FeedRoute(onOpenArticle = ::openArticle)
            }
        }
    }

    private fun openArticle(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
}
