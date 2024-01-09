package com.example.orionhub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.orionhub.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

//        setContentView(R.layout.activity_main)
        setContentView(binding.root)

        
    }
}