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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orionhub.databinding.FragmentProfileBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [ProfileFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class ProfileFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment ProfileFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ProfileFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}

class ProfileFragment : Fragment() {

    // Binding object instance corresponding to the fragment_profile.xml layout
    private var _binding: FragmentProfileBinding? = null
    private lateinit var recyclerView: RecyclerView
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var username: String
    var fileUri : Uri? =null
    var imgurl :String = ""
    var imgselected : Boolean =false

    companion object {
        fun newInstance(username: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString("USERNAME", username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve and use the username argument
        arguments?.let {
            username = it.getString("USERNAME") ?: ""
            // Use 'username' to load profile data
            val fireclass = Firebasefun();
            fireclass.initialiseFirebase()

            binding.profileUsername.setText("u/"+username)

            lifecycleScope.launch {
                val karma = fireclass.calcUsersKarma(username)
                binding.profileKarma.setText(karma.toString())

                val userinfo = fireclass.getUserDataFromName(username)

                val myusername = fireclass.getMyusername(requireContext())

                val userprofileimgUrl = fireclass.getUserProfileUrlfromName(username)

                if(userprofileimgUrl!=""){

                Glide.with(requireActivity()).load(userprofileimgUrl).into(binding.profileImage)
                }

                if(myusername==username){
                    binding.profileImage.setOnClickListener {
                        lifecycleScope.launch {


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
                            val imguploadres = fireclass.updateUserProfileUrl(imgurl,requireContext())
                            if(imguploadres==1){
                                Toast.makeText(requireContext(), "Profile updated!!!", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(requireContext(), "Could'nt update. Please try again", Toast.LENGTH_SHORT).show()

                            }
                    }

                    }

                }

                // Extract date components from the list
                var date =
                    userinfo?.child("createdAt")?.child("date")?.getValue(Int::class.java)
                var month = userinfo?.child("createdAt")?.child("month")?.getValue(Int::class.java)
                val year = userinfo?.child("createdAt")?.child("year")?.getValue(Int::class.java)?.rem(100)


                val monthName = when (month?.plus(1)) {
                    1 -> "Jan"
                    2 -> "Feb"
                    3 -> "Mar"
                    4 -> "Apr"
                    5 -> "May"
                    6 -> "Jun"
                    7 -> "Jul"
                    8 -> "Aug"
                    9 -> "Sep"
                    10 -> "Oct"
                    11 -> "Nov"
                    12 -> "Dec"
                    else -> null  // Handle invalid month
                }

                val birthdaydate :String = "$date $monthName 20$year"

                binding.profileBirthday.setText(birthdaydate)

                val postsno = fireclass.getUsersPostNo(username)-1
                binding.profilePostsNo.setText(postsno.toString())


                recyclerView= binding.mypostsListRecyclerview
            recyclerView.layoutManager=LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)
                val mypostlist = fireclass.getUsersPostModelList(username)
                val adapter = AdapterforPostsOnHome(mypostlist){
                    openPostPage(it)
                }
                recyclerView.adapter = adapter
            }

        }
        // Rest of your setup code

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
                binding.profileImage.setImageBitmap(bitmap)

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
        _binding = null
    }
}
