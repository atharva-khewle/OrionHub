package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sendbird.uikit.activities.ChannelListActivity
import com.sendbird.uikit.fragments.ChannelListFragment

class Chat : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelListFragment = ChannelListFragment.Builder()
//            .setCustomChannelListQuery(null) // Customize your query if needed
            .setUseHeader(true)
            .setHeaderTitle("Add a friend")
            .build()

        // Display the fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.chatContainer, channelListFragment)
            .commit()        // Create an instance of the GroupChannelListFragment

    }
}