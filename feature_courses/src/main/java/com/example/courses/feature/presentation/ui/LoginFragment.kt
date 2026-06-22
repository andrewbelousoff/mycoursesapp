package com.example.courses.feature.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.courses.feature.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnVk = view.findViewById<Button>(R.id.btnVk)
        val btnOk = view.findViewById<Button>(R.id.btnOk)

        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

        fun validateInput() {
            val emailText = etEmail.text.toString().trim()
            val passwordText = etPassword.text.toString()

            val isValid = emailText.matches(emailPattern) && passwordText.isNotEmpty()
            btnLogin.isEnabled = isValid
        }

        etEmail.doAfterTextChanged { validateInput() }
        etPassword.doAfterTextChanged { validateInput() }

        btnLogin.setOnClickListener {
            // ИСПРАВЛЕНО: Кастуем activity к интерфейсу-мосту, скрывая MainActivity от модуля фичи
            (activity as? LoginNavigation)?.onLoginSuccess()
        }

        btnVk.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com")))
        }

        btnOk.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://ok.ru")))
        }
    }
}
