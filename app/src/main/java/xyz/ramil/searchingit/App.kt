package xyz.ramil.searchingit

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class App : Application() {

    lateinit var signInClient: GoogleSignInClient

   override fun onCreate() {
        super.onCreate()
        instance = this
        signInClient = GoogleSignIn.getClient(applicationContext, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build())
    }

    companion object {
        var instance: App? = null
            private set

        var API = "https://api.github.com/"

    }

}