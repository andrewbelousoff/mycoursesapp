package com.example.courses

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.courses.feature.presentation.ui.MainFragment
import com.example.courses.feature.presentation.ui.FavoritesFragment
import com.example.courses.feature.presentation.ui.AccountFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val mainFragment by lazy { MainFragment() }
    private val favoritesFragment by lazy { FavoritesFragment() }
    private val accountFragment by lazy { AccountFragment() }
    
    // Переменная для хранения текущего видимого экрана
    private var activeFragment: Fragment = mainFragment
    
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
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

        bottomNavigation.visibility = View.VISIBLE
        
        // ИСПРАВЛЕНО: Добавляем все три экрана в память один раз при старте.
        // Главный экран показываем, а Избранное и Аккаунт — прячем в бэкграунд.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, accountFragment, "profile").hide(accountFragment)
                .add(R.id.fragment_container, favoritesFragment, "favorites").hide(favoritesFragment)
                .add(R.id.fragment_container, mainFragment, "main")
                .commit()
            activeFragment = mainFragment
        }
    }

    // ИСПРАВЛЕНО: Вместо уничтожения экранов через replace(), мы просто прячем 
    // текущий экран и показываем нужный. Адаптеры и состояния внутри фрагментов сохраняются намертво!
    private fun switchFragment(targetFragment: Fragment) {
        if (activeFragment == targetFragment) return
        
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(targetFragment)
            .commit()
            
        activeFragment = targetFragment
    }
}

