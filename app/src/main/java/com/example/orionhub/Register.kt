package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.orionhub.databinding.FragmentIntresteditemsBinding
import com.example.orionhub.databinding.FragmentRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class Register : Fragment() {
    private  val sharedviewmodel: SharedViewModel by activityViewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)


        //on click things
        binding.tologin.setOnClickListener {
            binding.tologin.findNavController().navigate(R.id.action_register_to_login)
        }
        binding.button.setOnClickListener {
            //howto set data to shared view? use fuction inside it
            val email = binding.registerEmailTextbox.text.toString()
            val pass = binding.registerPassTextbox.text.toString()
            val fireclass = Firebasefun();
            fireclass.initialiseFirebase()

            lifecycleScope.launch{
            if(email!="" && pass!=""
                && fireclass.isEmailAvailable(email)==false
            ){
                sharedviewmodel.saveEmail(email)
                sharedviewmodel.savePassword(pass)

                sharedviewmodel.saveisregister(1)
//                Toast.makeText(requireContext(),"registered set true", Toast.LENGTH_SHORT).show();

                binding.button.findNavController().navigate(R.id.action_register_to_setUsername)
            }else{
                Toast.makeText(requireContext(),"Email is already registered", Toast.LENGTH_SHORT).show();
            }

            }

        }
        return binding.root;
    }



}