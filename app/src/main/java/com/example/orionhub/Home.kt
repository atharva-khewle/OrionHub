package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orionhub.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch


class Home : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater, container, false)



        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()
        // Inflate the layout for this fragment

        lifecycleScope.launch {

            val homepagepostslist = fireclass.getPostsInModelpostFormatForHomePage(requireContext())

            binding.homepageRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.homepageRecyclerview.setHasFixedSize(true)

            //here u give list of posts to adapter. make it dynamic
//            val list =generateDummyPosts(6)
            val adapter = AdapterforPostsOnHome(homepagepostslist){
                openPostPage(it)
            }
            binding.homepageRecyclerview.adapter =adapter

        }

        return binding.root
    }
    private fun openPostPage(post : PostShownModel) {
        val fragment = postpage.newInstance(post.postId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.inMainFrag_layout, fragment)
            .commit()
    }

    fun generateDummyPosts(count: Int): List<PostShownModel> {
        val contentTypes = listOf("text", "video", "image")
        val dummyPosts = mutableListOf<PostShownModel>()

        for (i in 1..count) {
            val randomContentType = contentTypes.random()
            val post = PostShownModel(
                subredditName = "Subreddit $i",
                title = "Post Title $i",
                contentType = randomContentType,
                contenttext = "Avatar: The Way of Water OTT release date: James Cameron's directorial blockbuster- Avatar: The Way of Water is set to premiere on OTT platforms. The sequel to the 2009 blockbuster movie 'Avatar' will be released today i.e. March 28. The film will reportedly release on ott platforms such as Amazon Video, Apple TV, Vudu, and movies anywhere.\n" +
                        "\n" +
                        "The official Avatar Instagram account on Thursday announced the Avatar 2 release date as March 28, 2023.\n" +
                        "\n" +
                        "Posting the same, the caption quoted, \" Verified Return to Pandora whenever you want at home, only on Digital on March 28. Get access to over three hours of never-before-seen extras when you add #AvatarTheWayOfWater to your movie collection.\"",
                medialink = when(randomContentType){
                    "video" -> "https://www.redgifs.com/ifr/cornsilksweetratfish"
                    "text" -> "Avatar: The Way of Water OTT release date: James Cameron's directorial blockbuster- Avatar: The Way of Water is set to premiere on OTT platforms. The sequel to the 2009 blockbuster movie 'Avatar' will be released today i.e. March 28. The film will reportedly release on ott platforms such as Amazon Video, Apple TV, Vudu, and movies anywhere.\n\nThe official Avatar Instagram account on Thursday announced the Avatar 2 release date as March 28, 2023.\n\nPosting the same, the caption quoted, \" Verified Return to Pandora whenever you want at home, only on Digital on March 28. Get access to over three hours of never-before-seen extras when you add #AvatarTheWayOfWater to your movie collection.\""
                    "image" -> "https://upload.wikimedia.org/wikipedia/commons/0/0f/Eiffel_Tower_Vertical.JPG"
                    else -> ""
                }
                ,
                votes = (10..1000).random(),
                comments = (0..100).random(),
                postId = "post_id_$i"
            )
            dummyPosts.add(post)
        }

        return dummyPosts
    }


}