package com.example.orionhub
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

class Firebasefun {
    private lateinit var userdb : DatabaseReference
    private lateinit var postdb : DatabaseReference
    private lateinit var commentdb : DatabaseReference
    private lateinit var subredditdb : DatabaseReference

    fun initialiseFirebase() {
        userdb = FirebaseDatabase.getInstance().getReference("Users")
        postdb = FirebaseDatabase.getInstance().getReference("Posts")
        commentdb = FirebaseDatabase.getInstance().getReference("Comments")
        subredditdb = FirebaseDatabase.getInstance().getReference("Subreddits")
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