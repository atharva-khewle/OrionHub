package com.example.orionhub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orionhub.databinding.FragmentSubredditBinding

class SubredditFragment : Fragment() {

    // Binding object instance corresponding to the fragment_subreddit.xml layout
    private var _binding: FragmentSubredditBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

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

        val subredditName = arguments?.getString("SUBREDDIT_NAME")
        binding.subredditName.text = subredditName

        val recyclerview  = binding.subredditRecyclerView
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.setHasFixedSize(true)

        //here u give list of posts to adapter. make it dynamic
        val list = generateDummyPosts(9)
        recyclerview.adapter=AdapterforPostsOnSubreddit(list)



        return binding.root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed
    }
}
