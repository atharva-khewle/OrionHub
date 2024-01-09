package com.example.orionhub

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton


class StartPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_start_page, container, false);
        val loginbtn = view.findViewById<TextView>(R.id.loginredirect)
        val emailbtn = view.findViewById<MaterialButton>(R.id.registerredirect)

        emailbtn.setOnClickListener {
            emailbtn.findNavController().navigate(R.id.action_startPage_to_register)
        }
        loginbtn.setOnClickListener {
            loginbtn.findNavController().navigate(R.id.action_startPage_to_login)
        }
        return view;
    }



        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val prefs: SharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

            if(prefs.getString("USERNAME_KEY", null) != null){
                findNavController().navigate(R.id.action_startPage_to_mainPage)
            }



    }







}