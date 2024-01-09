package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orionhub.databinding.FragmentSubredditBinding
import kotlinx.coroutines.launch

class SubredditFragment : Fragment() {

    // Binding object instance corresponding to the fragment_subreddit.xml layout
    private var _binding: FragmentSubredditBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var subname : String;
    private lateinit var desc : String;
    private lateinit var subimage : String;
    private lateinit var subinfo : SubredditModel
    private lateinit var postIDs : List<String>
    private var members : Int = 0


    companion object {
        fun newInstance(subredditName: String): SubredditFragment {
            val fragment = SubredditFragment()
            val args = Bundle()
            args.putString("SUBREDDIT_NAME", subredditName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentSubredditBinding.inflate(inflater, container, false)

        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()

        val subredditName = arguments?.getString("SUBREDDIT_NAME")
        if (subredditName != null) {
            if(subredditName.contains("r/")){
                subname=subredditName.substring(2)
                Toast.makeText(requireContext(), subname, Toast.LENGTH_SHORT).show()
                binding.subredditName.text = subredditName
            }else{
                binding.subredditName.text = "r/"+subredditName
                subname=subredditName

            }
        }

        lifecycleScope.launch {
            val dataclass = fireclass.getSubredditdataFromName(subname)
            members=dataclass.members?:69
            subimage=dataclass.subimage?:""
            desc=dataclass.description?:""
            postIDs=dataclass.postIDList



            binding.subMembers.text = members.toString()
            binding.subredditDesc.setText(desc)



            val recyclerview  = binding.subredditRecyclerView
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.setHasFixedSize(true)



            //here u give list of posts to adapter. make it dynamic
            val list = fireclass.getPostsInModelpostFormat(subname)
            recyclerview.adapter=AdapterforPostsOnSubreddit(list)

        }













        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed
    }
}
