package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

class Register : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val logintxt = view.findViewById<TextView>(R.id.tologin)
        val continuebtn = view.findViewById<Button>(R.id.button)


        //on click things
        logintxt.setOnClickListener {
            logintxt.findNavController().navigate(R.id.action_register_to_login)
        }
        continuebtn.setOnClickListener {
            continuebtn.findNavController().navigate(R.id.action_register_to_intresteditems)
        }
        return view;
    }

}