package com.example.orionhub
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.sendbird.android.exception.SendbirdException
import com.sendbird.android.handler.InitResultHandler
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter
import com.sendbird.uikit.interfaces.UserInfo
import android.util.Log


//this is where chatting part is setup
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here



//        val profilelurl = a.profileURL



    }


    fun initializethebird(){
        val a = Auth_Info(this);
        val appID = a.APPLICATION_ID

        val prefs: SharedPreferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val nickname = prefs.getString("USERNAME_KEY", null)?:"default"
        val userID = prefs.getString("USERNAME_KEY", null)?:"default"
        Log.d("SharedPreferences", "Retrieved nickname: $nickname")


        SendbirdUIKit.init(object : SendbirdUIKitAdapter {


            override fun getAppId(): String {
                return appID // Specify your Sendbird application ID.
            }

            override fun getAccessToken(): String {
                return ""
            }

            override fun getUserInfo(): UserInfo {
                return object : UserInfo {
                    override fun getUserId(): String {
                        return userID // Specify your user ID.
                    }

                    override fun getNickname(): String {
                        return nickname // Specify your user nickname.
                    }

                    override fun getProfileUrl(): String {
                        return ""
                    }
                }
            }

            override fun getInitResultHandler(): InitResultHandler {
                return object : InitResultHandler {
                    override fun onMigrationStarted() {
                        // DB migration has started.
                    }

                    override fun onInitFailed(e: SendbirdException) {
                        // If DB migration fails, this method is called.
                    }

                    override fun onInitSucceed() {
                        // If DB migration is successful, this method is called and you can proceed to the next step.
                        // In the sample app, the `LiveData` class notifies you on the initialization progress
                        // And observes the `MutableLiveData<InitState> initState` value in `SplashActivity()`.
                        // If successful, the `LoginActivity` screen
                        // Or the `HomeActivity` screen will show.
                    }
                }
            }
        }, this)
        SendbirdUIKit.setDefaultThemeMode(SendbirdUIKit.ThemeMode.Dark)

    }

}
