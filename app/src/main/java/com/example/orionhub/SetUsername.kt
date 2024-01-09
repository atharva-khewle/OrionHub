package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.orionhub.databinding.FragmentIntresteditemsBinding
import com.example.orionhub.databinding.FragmentSetUsernameBinding
import kotlinx.coroutines.launch


class SetUsername : Fragment() {

    private  val sharedviewmodel: SharedViewModel by activityViewModels()
    private var _binding: FragmentSetUsernameBinding? = null
    private val binding get() = _binding!!
    var email="uwu"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetUsernameBinding.inflate(inflater, container, false)
        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()

        // Inflate the layout for this fragment]
//        val view = inflater.inflate(R.layout.fragment_set_username, container, false)
//        val setusernamebtn = view.findViewById<Button>(R.id.setusernamebtn)


        //how to get data from sharedviewmodel
        sharedviewmodel.email.observe(viewLifecycleOwner,{emaill->
            email = emaill
        })







        binding.setusernamebtn.setOnClickListener {
            val username = binding.usernametxt.text.toString();
            lifecycleScope.launch{
                if(username!="" && fireclass.doesUsernameExist(username)==false){
                    sharedviewmodel.saveusername(username);
                    binding.setusernamebtn.findNavController().navigate(R.id.action_setUsername_to_intresteditems)
                }else{
                    Toast.makeText(requireContext(),"This username is already taken", Toast.LENGTH_SHORT).show();
                }
            }
//            binding.setusernamebtn.findNavController().navigate(R.id.action_setUsername_to_intresteditems)

        }
        return binding.root
    }



}