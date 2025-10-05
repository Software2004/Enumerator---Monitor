package com.example.enumerator_monitor.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.enumerator_monitor.MainActivity
import com.example.enumerator_monitor.R
import com.example.enumerator_monitor.databinding.ActivityLoginBinding
import com.example.enumerator_monitor.databinding.BottomSheetForgotPasswordBinding
import com.example.enumerator_monitor.viewmodel.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var forgotPasswordDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visible()
                binding.btnLogin.hideText()
            } else {
                binding.progressBar.gone()
                binding.btnLogin.showText()
            }
        })

        authViewModel.errorMessage.observe(this, Observer { message ->
            message?.let {
                showErrorOnFields(it)
                showSnackBar(it, isError = true)
                authViewModel.clearMessages()
            }
        })

        authViewModel.successMessage.observe(this, Observer { message ->
            message?.let {
                showSnackBar(it, isError = false)
                authViewModel.clearMessages()
            }
        })

        authViewModel.currentUser.observe(this, Observer { user ->
            user?.let {
                showSnackBar("Login successful! Welcome back.", isError = false)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            clearAllErrors()
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                authViewModel.login(email, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupTextWatchers() {
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilUsername.isErrorEnabled) {
                    binding.tilUsername.error = null
                }
            }
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.tilPassword.isErrorEnabled) {
                    binding.tilPassword.error = null
                }
            }
        })
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isBlank()) {
            binding.tilUsername.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilUsername.error = "Please enter a valid email"
            isValid = false
        }

        if (password.isBlank()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun showErrorOnFields(message: String) {
        binding.tilUsername.error = message
    }

    private fun clearAllErrors() {
        binding.tilUsername.error = null
        binding.tilPassword.error = null
    }

    private fun showForgotPasswordDialog() {
        forgotPasswordDialog = BottomSheetDialog(this)
        val dialogBinding = BottomSheetForgotPasswordBinding.inflate(layoutInflater)
        forgotPasswordDialog?.setContentView(dialogBinding.root)

        dialogBinding.btnCancel.setOnClickListener {
            forgotPasswordDialog?.dismiss()
        }

        dialogBinding.btnSend.setOnClickListener {
            val email = dialogBinding.etEmail.text.toString().trim()
            if (email.isNotBlank()) {
                authViewModel.resetPassword(email)
                forgotPasswordDialog?.dismiss()
            } else {
                dialogBinding.tilEmail.error = "Please enter your email address"
            }
        }

        dialogBinding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (dialogBinding.tilEmail.isErrorEnabled) {
                    dialogBinding.tilEmail.error = null
                }
            }
        })

        forgotPasswordDialog?.show()
    }

    private fun showSnackBar(message: String, isError: Boolean = false) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        if (isError) {
            snackbar.setBackgroundTint(getColor(R.color.colorError))
        } else {
            snackbar.setBackgroundTint(getColor(R.color.green))
        }
        snackbar.show()
    }

    private fun View.visible() {
        this.visibility = View.VISIBLE
    }

    private fun View.gone() {
        this.visibility = View.GONE
    }

    private fun View.inVisible() {
        this.visibility = View.INVISIBLE
    }

    private fun MaterialButton.showText() {
        this.text = "Login"
    }

    private fun MaterialButton.hideText() {
        this.text = ""
    }
}