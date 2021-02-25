package xyz.ramil.searchingit.ui.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.SignInButton
import xyz.ramil.searchingit.App
import xyz.ramil.searchingit.R
import xyz.ramil.searchingit.ui.main.MainActivity


class SignInActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var signInButton: SignInButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton = findViewById(R.id.signInButton)
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signInButton -> signIn()
        }
    }

    private fun signIn() {
        val signInIntent = App.instance?.signInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult()
        }
    }

    private fun handleSignInResult() {
            val intent = Intent(this, MainActivity()::class.java)
            startActivity(intent)
            finishAffinity()
    }

    companion object {
        private const val RC_SIGN_IN = 7
    }
}