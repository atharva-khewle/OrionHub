package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.orionhub.databinding.FragmentLoginBinding
import com.example.orionhub.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class Login : Fragment() {
    private  val sharedviewmodel: SharedViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentLoginBinding.inflate(inflater, container, false)
        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()
        // Inflate the layout for this fragment
//        val view =inflater.inflate(R.layout.fragment_login, container, false)
//        val registertxt = view.findViewById<TextView>(R.id.toregister)


        binding.toregister.setOnClickListener{
            binding.toregister.findNavController().navigate(R.id.action_login_to_register)
        }

        binding.button2.setOnClickListener {
            val e = binding.loginEmail.text.toString()
            val p = binding.loginPass.text.toString()
            lifecycleScope.launch{
                if(e!="" && p!=""
                    && fireclass.isEmailAvailable(e)==true
                ){
                    if(fireclass.isPasswordRightbyEmail(e,p)){
                        sharedviewmodel.saveusername(fireclass.getUsernameFromEmail(e)?:"Internet_error or idk gg")
                        sharedviewmodel.saveEmail(e)
                        sharedviewmodel.savePassword(p)
                        sharedviewmodel.saveisregister(0)
                        Toast.makeText(requireContext(),"set to login", Toast.LENGTH_SHORT).show();
                        binding.button2.findNavController().navigate(R.id.action_login_to_mainPage)
                    }else{
                        Toast.makeText(requireContext(),"Incorrect password", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(requireContext(),"Email is not registered", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return binding.root;
    }

}