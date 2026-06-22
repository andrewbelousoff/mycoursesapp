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

        // Регулярное выражение валидации почты по ТЗ (без кириллического текста)
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()

        fun validateInput() {
            val emailText = etEmail.text.toString().trim()
            val passwordText = etPassword.text.toString()

            // Кнопка активна только при полном совпадении с маской и непустом пароле
            val isValid = emailText.matches(emailPattern) && passwordText.isNotEmpty()
            btnLogin.isEnabled = isValid
        }

        etEmail.doAfterTextChanged { validateInput() }
        etPassword.doAfterTextChanged { validateInput() }

        // ПРЕДЗАПОЛНЕНИЕ ДЛЯ ТЕСТОВ: Вставляем готовые данные по твоему требованию
        etEmail.setText("test@gmail.com")
        etPassword.setText("12345")
        validateInput() // Принудительно разблокируем зелёную кнопку «Вход»

        btnLogin.setOnClickListener {
            // Вызываем безопасный метод-мост навигации в MainActivity
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

