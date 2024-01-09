package com.example.orionhub

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.browse.MediaBrowser.MediaItem
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.orionhub.databinding.FragmentCreateBinding // Import your generated binding class
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.launch
import java.util.UUID

class Create : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    var fileUri : Uri? =null
    var vidUri : Uri? =null
    private lateinit var videolauncher : ActivityResultLauncher<Intent>

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    // Request code for selecting an image
    private val imageRequestCode = 1001
    // Permission request code
    private val storagePermissionCode = 1002

    var textbool :Boolean=false
    var imgbool :Boolean=false
    var vidbool :Boolean=false
    var uuidtxt :String?=null
    var content:String=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()


        super.onViewCreated(view, savedInstanceState)

        videolauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){res->
            if(res.resultCode== RESULT_OK){
                vidUri =res.data?.data

                //        binding.postSelecedVideo.setVideoURI(vidUri)
                val player = SimpleExoPlayer.Builder(requireContext()).build()

                binding.postSelecedVideo.player = player

//        val mediaitem = MediaItem
                if(vidUri !=null){
                    player.setMediaItem(com.google.android.exoplayer2.MediaItem.fromUri(vidUri!!))
                    player.prepare()
                }else{

                }
            }
        }


        binding.selectedText.visibility = View.GONE
        binding.selectedImage.visibility = View.GONE
        binding.selectedVideo.visibility = View.GONE
        binding.selectImgBtn.visibility = View.GONE
        binding.selectVidBtn.visibility = View.GONE


        binding.contentText.setOnClickListener {
            updateVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE)
        }

        binding.contentImage.setOnClickListener {
            updateVisibility(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE)
        }

        binding.contentVideo.setOnClickListener {
            updateVisibility(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE)
        }


        binding.selectVidBtn.setOnClickListener {
            checkVidPerm()
        }


        binding.postButton.setOnClickListener {
            lifecycleScope.launch {
                val prefs: SharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val username = prefs.getString("USERNAME_KEY", null)
                val title = binding.createPostTitle.text.toString()
                content = binding.createPostTextContent.text.toString()
                val subname = binding.createPostSubreddit.text.toString()
                val list :List<String> = listOf("comment0")
                var post:PostFirebase?=null
                if(title!=null && subname!="" && content!=null && (textbool || imgbool || vidbool)){
                    val resSub = fireclass.doesthisSubExist(subname)
                    if(resSub==1){
                        Toast.makeText(requireContext(), "Entered", Toast.LENGTH_SHORT).show()
                        uuidtxt=UUID.randomUUID().toString()
                        val postid = uuidtxt
                        if(textbool){

                            post = PostFirebase(
                                uuidtxt!!,
                                username!!,
                                subname,
                                "text",
                                title,
                                content,
                                list,
                                0
                            )
                        }
                        if(imgbool){
                            content=fireclass.uploadImg(fileUri)
                            post = PostFirebase(
                                uuidtxt!!,
                                username!!,
                                subname,
                                "image",
                                title,
                                content,
                                list,
                                0
                            )
                        }
                        if(vidbool){
                            content = fireclass.uploadVideo(vidUri)
                            post = PostFirebase(
                                uuidtxt!!,
                                username!!,
                                subname,
                                "video",
                                title,
                                content,
                                list,
                                0
                            )
                        }

                        uploadPost(post!!)


                    }else{
                        Toast.makeText(requireContext(), "subreddit doesnot exist", Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(requireContext(), "Enter required details", Toast.LENGTH_SHORT).show()

                }
            }

        }

        binding.selectImgBtn.setOnClickListener {

            val intent = Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent,"Choose image to upload"),0

            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode==RESULT_OK && data!=null && data.data!=null){
            fileUri=data.data
            try {
                val bitmap :Bitmap=MediaStore.Images.Media.getBitmap(activity?.contentResolver,fileUri)
                binding.postSelectedImage.setImageBitmap(bitmap)
            }catch (e:Exception){
                Log.e("Exception","Error: "+e)
            }
        }
    }
    private fun updateVisibility(textVisibility: Int, imageVisibility: Int, videoVisibility: Int, imgBtnVisibility: Int, vidBtnVisibility: Int) {
        textbool = textVisibility == View.VISIBLE
        imgbool = imageVisibility == View.VISIBLE
        vidbool = videoVisibility == View.VISIBLE

        binding.selectedText.visibility = textVisibility
        binding.selectedImage.visibility = imageVisibility
        binding.selectedVideo.visibility = videoVisibility
        binding.selectImgBtn.visibility = imgBtnVisibility
        binding.selectVidBtn.visibility = vidBtnVisibility
    }
    suspend fun uploadPost(post:PostFirebase){
        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()

        val a= fireclass.createPost(post)
        val b= fireclass.addPostIDtoSubreddit(uuidtxt!!,post.subreddit)
        val c= fireclass.addPostIDtoUsername(uuidtxt!!,requireContext())
        if(a==1 && b==1 && c==1){
            Toast.makeText(requireContext(), "Posted!!!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireContext(), "Error!!!", Toast.LENGTH_SHORT).show()
        }

        // add postid to user and subreddit
    }

    fun checkVidPerm(){
        var readexternalVideo : String =""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readexternalVideo = android.Manifest.permission.READ_MEDIA_VIDEO
        }else{
            readexternalVideo = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if(ContextCompat.checkSelfPermission(requireContext(),readexternalVideo)==PackageManager.PERMISSION_GRANTED){
            openvideopicker()
        }else{
            ActivityCompat.requestPermissions(
                    requireActivity(),
                arrayOf(readexternalVideo),
                100
                    )
        }
    }

    fun openvideopicker(){
        var intent = Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        videolauncher.launch(intent)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
