package com.example.orionhub
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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






    suspend fun getUserProfileUrlfromName(username:String): String? {
        return try {
            val userinfo = userdb.child(username).get().await()
            val res = userinfo.child("profileUrl").getValue<String>()
            res
        }catch (e:Exception){
            ""
        }
    }



    suspend fun updateUserProfileUrl(url:String, context: Context):Int{
        return try {
            val myusername = getMyusername(context)
            val userinfo = userdb.child(myusername).get().await()
            val updateddata = mapOf<String,String>(
                "profileUrl" to url
            )
            userdb.child(myusername).updateChildren(updateddata).await()
            1
        }catch (e:Exception){
            0
        }
    }




    suspend fun getAllUsersList(): MutableList<String> {
        val db = userdb.get().await()
        val list = mutableListOf<String>()
        for(name in db.children){
            list.add(
                name.child("username").getValue<String>()!!
            )
        }
        return list
    }

    suspend fun getUsersPostNo(user:String): Int {
        val list = getUsersPostIdList(user)
        var p :Int =0;
        for(ee in list ){
            p+=1
        }
        return p
    }


    suspend fun getUsersPostModelList(user: String): MutableList<PostShownModel> {
        val postidlist = getUsersPostIdList(user)
        val finalList = convertPostIDstoPostModelFormat(postidlist)

        return finalList
    }

    suspend fun getUsersPostIdList(user:String): MutableList<String> {
        val userinfo = getUserDataFromName(user)
        val finallist = mutableListOf<String>()
        for (id in userinfo?.child("postList")?.children!!){
            finallist.add(
                id.getValue<String>()!!
            )
        }
        return finallist
    }

    suspend fun calcUsersKarma(username: String):Int{
        val userinfo = getUserDataFromName(username)
        var postidslist :MutableList<String> = mutableListOf()
        if(userinfo?.child("postList")?.getValue<MutableList<String>>()!=null){

        postidslist = userinfo?.child("postList")?.getValue<MutableList<String>>()!!
        }

        val postmodellist = convertPostIDstoPostModelFormat(postidslist)
        var k :Int =0
        for(post in postmodellist){
            k+=post.votes
        }
        return k
    }

    suspend fun addSubToUser(context: Context,subname: String):Int{
        val username = getMyusername(context)
        return try {
            val userinfo = getUserDataFromName(username)
            var list : MutableList<String> = mutableListOf<String>()
                if(      userinfo!=null &&          userinfo.child("subredditList").getValue<MutableList<String>>()!=null){
                    list = userinfo.child("subredditList").getValue<MutableList<String>>()?: mutableListOf<String>()
                }
            list.add(subname)
            val newusersidlist = mapOf<String,List<String>>(
                "subredditList" to list
            )
            userdb.child(username).updateChildren(newusersidlist).await()
            1
        }catch (e:Exception){
            0
        }
    }
    suspend fun RemoveSubFromUser(context: Context,subname: String):Int{
        val username = getMyusername(context)
        return try {
            val userinfo = getUserDataFromName(username)
            val list : MutableList<String>? = userinfo?.child("subredditList")?.getValue<MutableList<String>>()
            list?.remove(subname)
            val newusersidlist = mapOf<String,List<String>>(
                "subredditList" to list!!
            )
            userdb.child(username).updateChildren(newusersidlist).await()
            1
        }catch (e:Exception){
            0
        }
    }


    suspend fun addUserToSub(context: Context, subname:String):Int{
        val username = getMyusername(context)
        return try {
            val subinfo = getSubDataFromName(subname)

            val list : MutableList<String>? = subinfo?.child("usersList")?.getValue<MutableList<String>>()
            list?.add(username)
            val newusersidlist = mapOf<String,List<String>>(
                "usersList" to list!!
            )
            subredditdb.child(subname).updateChildren(newusersidlist).await()
            1
        }catch (e:Exception){
            0
        }
    }

    suspend fun RemoveFromSub(context: Context,subname: String):Int{
        val username = getMyusername(context)
        return try {
            val subinfo = getSubDataFromName(subname)
            val list : MutableList<String>? = subinfo?.child("usersList")?.getValue<MutableList<String>>()
            list?.remove(username)
            val newusersidlist = mapOf<String,List<String>>(
                "usersList" to list!!
            )
            subredditdb.child(subname).updateChildren(newusersidlist).await()
            1
        }catch (e:Exception){
            0
        }
    }

    suspend fun getSubredditNameListofMyUsername(context: Context): MutableList<String> {
        val username = getMyusername(context)
        val userInfo = userdb.child(username).get().await()
        val sublist = mutableListOf<String>()

        for (sub in userInfo.child("subredditList").children){
            sublist.add(
                sub.getValue<String>()!!
            )
        }

        return sublist

    }

    suspend fun AmISubbed(sub:String,context: Context):Int{
        return try {
            val list = getSubredditNameListofMyUsername(context)
            if(list.contains(sub)){
                1
            }else{
                2
            }
        }catch (e:Exception){
            0
        }
    }

    suspend fun getMyusername(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username= prefs.getString("USERNAME_KEY", null)?:"default"
        return username
    }


    suspend fun convertPostIDstoPostModelFormat(postids : MutableList<String>): MutableList<PostShownModel> {
        val postinfo = postdb.get().await()
        val finalList = mutableListOf<PostShownModel>()

        for(post in postinfo.children){
            if(postids.contains(post.child("postId").getValue<String>())){
                val temp = PostShownModel(
                    post.child("subreddit").getValue<String>()!!,
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


    suspend fun updateVotetoPostbyPostID(postid: String,i:Int):Int{
        return try {
            val postinfo = getPostDataFromPostID(postid)
            val votes:Int = postinfo?.child("votes")?.getValue<Int>()?:0
            val updatedvotes = mapOf<String,Int>(
                "votes" to votes+i
            )
            postdb.child(postid).updateChildren(updatedvotes).await()
            1
        }catch (e:Exception){
            0
        }
    }


    suspend fun getPostsInModelpostFormatForSubreddit(subname:String): MutableList<PostShownModel> {
        val postids = getSubredditPostIDList(subname)
        return convertPostIDstoPostModelFormat(postids)
    }

    suspend fun getPostsInModelpostFormatForHomePage(context: Context):MutableList<PostShownModel>{
        var finallist= mutableListOf<PostShownModel>()
        val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username= prefs.getString("USERNAME_KEY", null)?:"default"

        val sublist = userdb.child(username).child("subredditList").get().await().getValue<MutableList<String>>()

        if(sublist!=null){
            for(sub in sublist){
                val postlistforsub = getPostsInModelpostFormatForSubreddit(sub)
                finallist += postlistforsub
            }
        }



        return finallist
    }

    suspend fun getSubredditPostIDList(subname:String): MutableList<String> {
        val subinfo = subredditdb.child(subname).get().await()
        val postids = mutableListOf<String>()

        for(it in subinfo.child("postIDList").children){
            postids.add(it.getValue(String::class.java)!!)
        }
        return postids
    }

    suspend fun getSubredditModsListAndCheckIfUSerIsInIt(subname:String,username: String): Boolean {
        val subinfo = subredditdb.child(subname).get().await()
        val modslist = mutableListOf<String>()
        var ans : Boolean=false;

        for(it in subinfo.child("mods").children){
            if(it.getValue(String::class.java)==username){
                ans=true
            }
        }
        return ans
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

    suspend fun updateSubImg(subname: String,imgurl:String):Int{
        return try {
            val subinfo = subredditdb.child(subname).get().await()
            val updateddata = mapOf<String,String>(
                "subimage" to imgurl
            )
            subredditdb.child(subname).updateChildren(updateddata).await()
            1
        }catch (e:Exception){
            0
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


    suspend fun getPostDataFromPostID(postid: String): DataSnapshot? {
        val postinfo = postdb.child(postid).get().await()
        return postinfo
    }


    suspend fun getSubDataFromName(subname:String): DataSnapshot? {
        val subinfo = subredditdb.child(subname).get().await()
        return subinfo
    }

    suspend fun getUserDataFromName(user:String): DataSnapshot? {
        val userinfo = userdb.child(user).get().await()
        return userinfo
    }

    suspend fun addCommentIDtoUsername(commentid:String,context: Context):Int{
        return try {
            val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val username= prefs.getString("USERNAME_KEY", null)?:"default"
            val userinfo = getUserDataFromName(username)

            val list : MutableList<String>? = userinfo?.child("commentList")?.getValue<MutableList<String>>()
            list?.add(commentid)
            val newcomentidlist = mapOf<String,List<String>>(
                "commentList" to list!!
            )
            userdb.child(username).updateChildren(newcomentidlist).await()
            1
        }catch (e:Exception){
            0
        }
    }
    suspend fun addCommentIDtoPost(commentid:String,postid:String):Int{
        return try {
            val postinfo = postdb.child(postid).get().await()
            val list : MutableList<String>? = postinfo?.child("commentList")?.getValue<MutableList<String>>()
            list?.add(commentid)
            val newcommentidlist = mapOf<String,List<String>>(
                "commentList" to list!!
            )
            postdb.child(postid).updateChildren(newcommentidlist).await()
            1
        }catch (e:Exception){
            0
        }
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

    suspend fun getCommentIDsfromPostID(postid: String): MutableList<String>? {
        val postinfo = postdb.child(postid).get().await()
        return postinfo.child("commentList").getValue<MutableList<String>>()
    }
    suspend fun getCommentModelfromPostID(postid:String, context: Context):MutableList<CommentModel>{
        val commentids = getCommentIDsfromPostID(postid)!!
        val ModelcmtList = mutableListOf<CommentModel>()
        for (commentid in commentids){
            val cmtinfo = commentdb.child(commentid).get().await()
            var t= CommentModel(
                cmtinfo.child("commentId").getValue(String::class.java)?:"",
                cmtinfo.child("postId").getValue(String::class.java)?:"",
                cmtinfo.child("authorId").getValue(String::class.java)?:"",
                cmtinfo.child("content").getValue(String::class.java)?:"",
                cmtinfo.child("replyingTo").getValue(String::class.java)?:"",
            )
            if(t.commentId==""){

            }else{
//                Toast.makeText(context, t.content, Toast.LENGTH_SHORT).show()
                ModelcmtList.add(t)
            }



        }
        return ModelcmtList
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

    suspend fun getAllsubredditlist():List<String?>{
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

    suspend fun createComment(modelcmt:CommentModel):Int{
        return  try {
            commentdb.child(modelcmt.commentId).setValue(modelcmt).await()
            1
        }catch (e:Exception){
            0
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
                subredditdb.child(subinfo.title).setValue(subinfo).await()
                userdb.child(mod).updateChildren(user).await()
                subredditdb.child(title).setValue(subinfo).await()
                1 // Success
            }

        } catch (e: Exception) {
            0 // Failure
        }

    }



}