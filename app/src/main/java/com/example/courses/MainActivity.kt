package com.example.courses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.courses.feature.presentation.ui.MainFragment
import com.example.courses.feature.presentation.ui.FavoritesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val mainFragment by lazy { MainFragment() }
    private val favoritesFragment by lazy { FavoritesFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main -> {
                    changeFragment(mainFragment)
                    true
                }
                R.id.nav_favorites -> {
                    // ИСПРАВЛЕНО: Открываем полноценный экран избранного
                    changeFragment(favoritesFragment)
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit()
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
