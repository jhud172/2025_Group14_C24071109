package uk.ac.cardiff.trainerhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import uk.ac.cardiff.trainerhub.mobile.MobileWebsiteApp
import uk.ac.cardiff.trainerhub.ui.theme.TrainerHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TrainerHubTheme {
                MobileWebsiteApp()
            }
        }
    }
}
