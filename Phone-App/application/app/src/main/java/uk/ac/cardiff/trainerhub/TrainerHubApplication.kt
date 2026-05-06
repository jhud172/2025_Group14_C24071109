package uk.ac.cardiff.trainerhub

import android.app.Application

class TrainerHubApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }
}
