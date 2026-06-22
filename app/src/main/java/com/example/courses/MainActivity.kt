package com.example.courses

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.courses.feature.presentation.ui.MainFragment
import com.example.courses.feature.presentation.ui.FavoritesFragment
import com.example.courses.feature.presentation.ui.AccountFragment
import com.example.courses.feature.presentation.ui.LoginFragment
import com.example.courses.feature.presentation.ui.LoginNavigation

class MainActivity : AppCompatActivity(), LoginNavigation {

    private val mainFragment by lazy { MainFragment() }
    private val favoritesFragment by lazy { FavoritesFragment() }
    private val accountFragment by lazy { AccountFragment() }
    
    private var activeFragment: Fragment = mainFragment
    private var bottomNavigation: View? = null // Делаем вьюшку nullable для безопасности

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Безопасный поиск нижнего меню
        bottomNavigation = findViewById(R.id.bottom_navigation)

        val bottomNavView = bottomNavigation as? com.google.android.material.bottomnavigation.BottomNavigationView
        bottomNavView?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    switchFragment(mainFragment)
                    true
                }
                R.id.nav_favorites -> {
                    switchFragment(favoritesFragment)
                    true
                }
                R.id.nav_profile -> {
                    switchFragment(accountFragment)
                    true
                }
                else -> false
            }
        }

        // ИСПРАВЛЕНО: Безопасное скрытие меню через оператор ?. 
        // Приложение больше никогда не упадет на старте с ошибкой NullPointerException!
        if (savedInstanceState == null) {
            bottomNavigation?.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, LoginFragment(), "login")
                .commit()
        }
    }

    override fun onLoginSuccess() {
        bottomNavigation?.visibility = View.VISIBLE // Безопасно показываем меню при успешном входе
        
        val bottomNavView = bottomNavigation as? com.google.android.material.bottomnavigation.BottomNavigationView
        
        supportFragmentManager.beginTransaction()
            .remove(supportFragmentManager.findFragmentByTag("login")!!)
            .add(R.id.fragment_container, accountFragment, "profile").hide(accountFragment)
            .add(R.id.fragment_container, favoritesFragment, "favorites").hide(favoritesFragment)
            .add(R.id.fragment_container, mainFragment, "main")
            .commit()
            
        activeFragment = mainFragment
        bottomNavView?.selectedItemId = R.id.nav_main
    }

    private fun switchFragment(targetFragment: Fragment) {
        if (activeFragment == targetFragment) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(targetFragment)
            .commit()
        activeFragment = targetFragment
    }
}

