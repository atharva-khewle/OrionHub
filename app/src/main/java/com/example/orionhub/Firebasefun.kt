package com.example.orionhub
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID

class Firebasefun {
    private lateinit var userdb : DatabaseReference
    private lateinit var postdb : DatabaseReference
    private lateinit var commentdb : DatabaseReference
    private lateinit var subredditdb : DatabaseReference
    private lateinit var storagedb : StorageReference

    fun initialiseFirebase() {
        userdb = FirebaseDatabase.getInstance().getReference("Users")
        postdb = FirebaseDatabase.getInstance().getReference("Posts")
        commentdb = FirebaseDatabase.getInstance().getReference("Comments")
        subredditdb = FirebaseDatabase.getInstance().getReference("Subreddits")
        storagedb = FirebaseStorage.getInstance().getReference()
    }








    suspend fun getPostsInModelpostFormat(subname:String): MutableList<PostShownModel> {
        val postids = getSubredditPostIDList(subname)
        val postinfo = postdb.get().await()
        val finalList = mutableListOf<PostShownModel>()

        for(post in postinfo.children){
            if(postids.contains(post.child("postId").getValue<String>())){
                val temp = PostShownModel(
                    subname,
                    post.child("title").getValue<String>()!!,
                    post.child("contentType").getValue<String>()!!,
                    post.child("content").getValue<String>()!!,
                    post.child("content").getValue<String>()!!,
                    post.child("votes").getValue<Int>()?:0,
                    post.child("commentList").getValue<MutableList<String>>()!!.size,
                    post.child("postId").getValue<String>()!!
                )
                finalList.add(temp)
            }
        }

        return finalList
    }

    suspend fun getSubredditPostIDList(subname:String): MutableList<String> {
        val subinfo = subredditdb.child(subname).get().await()
        val postids = mutableListOf<String>()

        for(it in subinfo.child("postIDList").children){
            postids.add(it.getValue(String::class.java)!!)
        }
        return postids
    }


    suspend fun uploadVideo(vidUri:Uri?): String {
        if(vidUri!=null){
            val vidref = storagedb.child("videos/"+UUID.randomUUID().toString())
            vidref.putFile(vidUri).await()
            val link = vidref.downloadUrl.await()
            return link.toString()
        }else{
            return "0"
        }
    }



    suspend fun uploadImg(imguri:Uri?): String {
        if(imguri!=null){
            val ref = storagedb.child("images/"+UUID.randomUUID().toString())
            ref.putFile(imguri).await()
            val link = ref.downloadUrl.await()
            return link.toString()
        }else{
            return "0"
        }
    }


    suspend fun doesthisSubExist(sub: String):Int{
        return try {
            val res = subredditdb.child(sub).get().await()
            if(res.exists()){
                1
            }else{
                0
            }
        }catch (e:Exception){
            0
        }

    }

    suspend fun mysubredditlists(context: Context) : List<String>? {
        val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username =prefs.getString("USERNAME_KEY", null)
        val datasnap = userdb.child(username!!).get().await()
        val list : MutableList<String>? = datasnap.child("subredditList").getValue<MutableList<String>>()

        return list

    }

    suspend fun doesUsernameExist(username : String) : Boolean{
        return try {
            val dataSnapshot = userdb.child(username).get().await()
            dataSnapshot.exists() // Return true if the user exists
        } catch (e: Exception) {
            false // Handle exceptions, perhaps by returning false indicating an error occurred
        }
    }

    suspend fun isEmailAvailable(email : String) : Boolean{
        val snapshot = userdb.get().await()

        for (userSnapshot in snapshot.children) {
            val userEmail = userSnapshot.child("email").getValue(String::class.java)
            if (userEmail == email) {
                return true
            }
        }
        return false
    }


    suspend fun getSubDataFromName(subname:String): DataSnapshot? {
        val subinfo = subredditdb.child(subname).get().await()
        return subinfo
    }

    suspend fun getUserDataFromName(user:String): DataSnapshot? {
        val userinfo = userdb.child(user).get().await()
        return userinfo
    }

    suspend fun addPostIDtoUsername(postid:String,context: Context):Int{
        return try {
            val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val username= prefs.getString("USERNAME_KEY", null)?:"default"
            val userinfo = getUserDataFromName(username)

            val list : MutableList<String>? = userinfo?.child("postList")?.getValue<MutableList<String>>()
            list?.add(postid)
            val newpostidlist = mapOf<String,List<String>>(
                "postList" to list!!
            )
            userdb.child(username).updateChildren(newpostidlist).await()
            1
        }catch (e:Exception){
            0
        }
    }

    suspend fun addPostIDtoSubreddit(postid:String,subname:String):Int{
        return try {
            val subinfo = getSubDataFromName(subname)

            val list : MutableList<String>? = subinfo?.child("postIDList")?.getValue<MutableList<String>>()
            list?.add(postid)
            val newpostidlist = mapOf<String,List<String>>(
                "postIDList" to list!!
            )
            subredditdb.child(subname).updateChildren(newpostidlist).await()
            1
        }catch (e:Exception){
            0
        }
    }

    suspend fun getSubredditdataFromName(subname: String): trivial_subdata_class{
        val subinfo = subredditdb.child(subname).get().await()

        var members :Int=0

        for(a in subinfo.child("usersList").children){
            members += 1;
        }


        val desc = subinfo.child("description").getValue(String::class.java)
        val subimage = subinfo.child("subimage").getValue(String::class.java)
        val list = mutableListOf<String>()


        for(postid in subinfo.child("postIDList").children){
            list.add(postid.getValue(String::class.java)!!)
        }


        return trivial_subdata_class(members,desc,subimage,list);


    }

    suspend fun isPasswordRightbyEmail(email : String, pass: String) : Boolean{
        val snapshot = userdb.get().await()

        for (userSnapshot in snapshot.children) {
            val userEmail = userSnapshot.child("email").getValue(String::class.java)
            val userpass = userSnapshot.child("password").getValue(String::class.java)
            if (userEmail == email) {
                if(userpass == pass){
                    return true
                }else{
                    return false
                }
            }
        }
        return false
    }

    suspend fun getUsernameFromEmail(targetEmail: String): String? {
        val allUsersSnapshot = userdb.get().await()
        for (userSnapshot in allUsersSnapshot.children) {
            val userEmail = userSnapshot.child("email").getValue(String::class.java)
            if (userEmail == targetEmail) {
                return userSnapshot.key // Return the username if the email matches
            }
        }
        return null // Return null if no matching email is found
    }



    suspend fun isPasswordRightbyUsername(username : String, pass : String) : Boolean{
        val userSnapshot = userdb.child(username).get().await()
        if (userSnapshot.exists()) {
            val storedPassword = userSnapshot.child("password").getValue(String::class.java)
            return storedPassword == pass
        }
        return false
    }

    suspend fun registerUser(username: String, pass: String, email:String) :Int {
        var Userinfo = UserModel(username,pass,email)
        var ans : Int;

        return try {
            userdb.child(username).setValue(Userinfo).await() // Use await() for Kotlin 1.6+
            1 // Success
        } catch (e: Exception) {
            0 // Failure
        }
    }

    suspend fun allsubredditlist():List<String?>{
        val snap = subredditdb.get().await()
        var list = mutableListOf<String>()
        for(a in snap.children){
            list.add(a.key.toString())
        }

        return list
    }

    suspend fun createPost(postinfo:PostFirebase):Int{
        return try {
            postdb.child(postinfo.postId).setValue(postinfo).await() // Use await() for Kotlin 1.6+
            1 // Success
        } catch (e: Exception) {
            0 // Failure
        }
    }

    suspend fun createsubreddit(title:String,desc:String,context: Context):Int{
        val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val mod= prefs.getString("USERNAME_KEY", null)?:"default"
        val modss: List<String> = listOf(mod)
        val subinfo = SubredditModel(title,desc,modss)

        return try {
            val subs:MutableList<String> = mysubredditlists(context) as MutableList<String>
            if(subs==null){
                0
            }else{
                subs.add(title)
                val user = mapOf<String,List<String>>(
                    "subredditList" to subs
                )
                userdb.child(mod).updateChildren(user).await()
                subredditdb.child(title).setValue(subinfo).await()
                1 // Success
            }

        } catch (e: Exception) {
            0 // Failure
        }

    }



}