package com.example.orionhub

import AdapterforCommunityRecycler
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orionhub.databinding.FragmentCommunitiesBinding
import kotlinx.coroutines.launch
import java.util.Locale


class Communities : Fragment() {


    private lateinit var binding : FragmentCommunitiesBinding
    private lateinit var recyclerView :RecyclerView
    private lateinit var searchView :SearchView
    private var mylist :List<String> = listOf("")
    private lateinit var adapter: AdapterforCommunityRecycler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()
        binding = FragmentCommunitiesBinding.inflate(inflater, container, false)
        recyclerView=binding.communityRecyclerview
        searchView=binding.communitySearchview

        searchView.setOnClickListener{
            searchView.isIconified = false
        }


        lifecycleScope.launch {
            val a = fireclass.getAllsubredditlist()
            if(a!=null){
                mylist = a as List<String>
                setupui()
            }


        }


        return binding.root
    }

    fun setupui(){

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        adapter = AdapterforCommunityRecycler(mylist)
        adapter = AdapterforCommunityRecycler(mylist) { subredditName ->
            openSubredditFragment(subredditName)
        }
        recyclerView.adapter =adapter

        // Initially hide the RecyclerView
        // uncomment this when u have free time
//        recyclerView.visibility = View.GONE




        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Hide RecyclerView when query is empty
                    // uncomment this when u have free time
//                    recyclerView.visibility = View.GONE
                } else {
                    // Show RecyclerView when user starts typing
                    // uncomment this when u have free time
//                    recyclerView.visibility = View.VISIBLE


                }
                // Update your RecyclerView's data here
                filterlist(newText) // Assuming you have a method to filter the list
                return true
            }

        })
    }

    private fun openSubredditFragment(subredditName: String) {
        val fragment = SubredditFragment.newInstance(subredditName)
        parentFragmentManager.beginTransaction()
            .replace(R.id.inMainFrag_layout, fragment)
            .commit()
    }
    fun filterlist(query : String?){
        if(query!=null){
            val filteredlist = ArrayList<String>()

            for(i in mylist){
                if(i.lowercase(Locale.ROOT).contains(query)){
                    filteredlist.add(i)
                }
            }
            if(filteredlist.isEmpty()){
                Toast.makeText(requireContext(), "Nope, not here", Toast.LENGTH_SHORT).show()
            }else{
//                adapter.setfilteredlist(filteredlist)
                adapter.setFilteredList(filteredlist)
            }
        }
    }

}