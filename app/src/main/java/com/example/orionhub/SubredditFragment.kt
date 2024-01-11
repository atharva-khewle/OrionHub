package com.example.orionhub

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.orionhub.databinding.FragmentSubredditBinding
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    var fileUri : Uri? =null
    var imgurl :String = ""
    var imgselected : Boolean =false


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
//                Toast.makeText(requireContext(), subname, Toast.LENGTH_SHORT).show()
                binding.subredditName.text = subredditName
            }else{
                binding.subredditName.text = "r/"+subredditName
                subname=subredditName

            }
        }

        lifecycleScope.launch {
            val myusername = fireclass.getMyusername(requireContext())
            val amImod: Boolean = fireclass.getSubredditModsListAndCheckIfUSerIsInIt(subname,myusername)
            val subinfo = fireclass.getSubDataFromName(subname)
            val subimgUrl = subinfo?.child("subimage")?.getValue<String>()

            if(subimgUrl!=""){
                Glide.with(requireActivity()).load(subimgUrl).into(binding.subProfileUrl)
            }
            if(amImod){
                binding.subProfileUrl.setOnClickListener {
                    lifecycleScope.launch{
                        val intent = Intent()
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.type = "image/*"
                        startActivityForResult(
                            Intent.createChooser(intent, "Choose image to upload"), 0

                        )

                        while (imgselected == false) {
                            delay(500)
                        }
                        imgurl = fireclass.uploadImg(fileUri)
                        val imguploadres = fireclass.updateSubImg(subname,imgurl)
                        if(imguploadres==1){
                            Toast.makeText(requireContext(), "Image updated!!!", Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(requireContext(), "Could'nt update. Please try again", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }

            val dataclass = fireclass.getSubredditdataFromName(subname)
            members=dataclass.members?:69
            subimage=dataclass.subimage?:""
            desc=dataclass.description?:""
            postIDs=dataclass.postIDList

            val ami:Int=fireclass.AmISubbed(subname,requireContext())

            if(ami ==1){
                binding.joinSubBtn.text ="Joined"
            }else{
                binding.joinSubBtn.text ="Join"
            }

            binding.joinSubBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {


                if (ami == 1) {
                    binding.joinSubBtn.text = "Join"
                    fireclass.RemoveSubFromUser(requireContext(), subname)
                    fireclass.RemoveFromSub(requireContext(),subname)

                } else {
                    binding.joinSubBtn.text = "Joined"
                    fireclass.addSubToUser(requireContext(),subname)
                    fireclass.addUserToSub(requireContext(),subname)
                }
            }
            }


            binding.subMembers.text = members.toString()
            binding.subredditDesc.setText(desc)



            val recyclerview  = binding.subredditRecyclerView
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.setHasFixedSize(true)



            //here u give list of posts to adapter. make it dynamic
            val list = fireclass.getPostsInModelpostFormatForSubreddit(subname)
            val adapter =  AdapterforPostsOnSubreddit(list){
                openPostPage(it)
            }
            recyclerview.adapter=adapter



        }













        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null && data.data!=null){
            fileUri=data.data
            try {


                val fireclass = Firebasefun();
                fireclass.initialiseFirebase()

                val bitmap : Bitmap =
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver,fileUri)
                binding.subProfileUrl.setImageBitmap(bitmap)

                imgselected=true
//                Toast.makeText(requireContext(), "B", Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                Log.e("Exception","Error: "+e)
            }
        }
    }
    private fun openPostPage(post : PostShownModel) {
        val fragment = postpage.newInstance(post.postId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.inMainFrag_layout, fragment)
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed
    }
}
