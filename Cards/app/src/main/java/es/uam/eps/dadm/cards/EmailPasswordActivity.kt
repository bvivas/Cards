package es.uam.eps.dadm.cards

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.databinding.ActivityEmailPasswordBinding


class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailPasswordBinding

    var username = " "
    var password = " "

    private lateinit var userInfo: UserInfo
    private lateinit var userIntent: Intent
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_password)
        userIntent = Intent(this, TitleActivity::class.java)

        auth = Firebase.auth
        val currentUser = Firebase.auth.currentUser
        if(currentUser != null) {
            currentUser.reload()
        }

        val usernameTextWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                username = s.toString()
            }
        }

        val passwordTextWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                password = s.toString()
            }
        }

        binding.usernameEdit.addTextChangedListener(usernameTextWatcher)
        binding.passwordEdit.addTextChangedListener(passwordTextWatcher)

        // Log in
        binding.logInButton.setOnClickListener {
            userInfo = UserInfo(username, password)
            logInUser(userInfo.username, userInfo.password)
        }

        // Sign up
        binding.signUpButton.setOnClickListener {
            userInfo = UserInfo(username, password)
            signUpUser(userInfo.username, userInfo.password)
        }
    }

    private fun logInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    startActivity(userIntent)
                } else {
                    Snackbar.make(binding.root, R.string.error_log_in, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    startActivity(userIntent)
                } else {
                    Snackbar.make(binding.root, R.string.error_sign_up, Snackbar.LENGTH_LONG).show()
                }
            }
    }
}