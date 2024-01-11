package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.orionhub.databinding.FragmentCreateSubredditBinding
import kotlinx.coroutines.launch

class CreateSubreddit : Fragment() {

    private lateinit var binding : FragmentCreateSubredditBinding

    companion object {
        fun newInstance() : CreateSubreddit{
            val frag = CreateSubreddit()
            return frag
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()

        binding = FragmentCreateSubredditBinding.inflate(inflater, container, false)

        binding.createSubredditBtn.setOnClickListener {
            lifecycleScope.launch {
                var subname = binding.createSubTitle.text.toString()
                val desc = binding.createSubDesc.text.toString()
                val allsbs = fireclass.getAllsubredditlist()
                val res :Boolean= allsbs.contains(subname)
                if (subname != "" && res==false) {
                    val a = fireclass.createsubreddit(subname, desc, requireActivity())
                    val b= fireclass.addUserToSub(requireContext(),subname)
                    if(a==1){
                        binding.createSubDesc.setText("")
                        binding.createSubTitle.setText("")
                        Toast.makeText(requireContext(), "Subreddit Created", Toast.LENGTH_SHORT)
                            .show()
                    }else{
                        Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT)
                            .show()

                    }

                } else {
                    Toast.makeText(requireContext(), "Subreddit already exists", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return binding.root
    }

}