/*
    Author: LilWons
    Date: 2023-12-09

    Description: Simple application using Action Intents to  phone calls, add calendar events, and take photos.
    Application uses fragments and bottom navigational menu to change between tasks.
    Application prompts user for permissions depending on the task.
 */
package com.example.casestudy3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.casestudy3.databinding.ActivityMainBinding

import com.google.android.material.bottomnavigation.BottomNavigationView

//Main application Activity.
class MainActivity : AppCompatActivity() {
    //Variable  declarations.
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavMenu()
    }

    //Sets up navigation menu with Jetpack's navigation component and bottom navigation view.
    private fun setupNavMenu() {

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment //Finds the container fragment for navigation destinations at R.id.nav_host_fragment.
            val navController = navHostFragment.navController //Gets the navController from navHostFragment.navController.
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation) //Finds bottom navigation menu at R.id.bottom_navigation.
            bottomNavigationView.setupWithNavController(navController) //Sets up bottom navigation menu to work with Navigational Controller.
    }
}


