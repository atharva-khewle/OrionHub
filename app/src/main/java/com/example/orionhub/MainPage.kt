package com.example.orionhub
import SharedPreferencesMAnager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.orionhub.databinding.FragmentMainPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.util.Log

//import com.example.orionhub.apple1


class MainPage : Fragment()  {

    private lateinit var binding : FragmentMainPageBinding
    private lateinit var bottomnav : BottomNavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMainPageBinding.inflate(inflater,container,false)
        fragmentReplace(apple1())
        bottomnav=binding.bottomNavigationView2;
        bottomnav.labelVisibilityMode =BottomNavigationView.LABEL_VISIBILITY_LABELED

        val a = Auth_Info(requireContext())
//        a.userID="mahoraga"
//        a.userNickname="mahoraga"
//        a.profileURL="https://fictionhorizon.com/wp-content/uploads/2023/11/Mahoraga.jpg"


        //shared prefs
        val prefs: SharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        if(prefs.getString("USERNAME_KEY", null)===null){
            //if name doesnt exist
            prefs.edit().putString("USERNAME_KEY", "dog").apply()
            Log.d("SharedPreferences", "Saved username:dog")
        }
        val appInstance = requireActivity().getApplication() as BaseApplication
        appInstance.initializethebird()



        binding.bottomNavigationView2.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navhome -> fragmentReplace(Home())
                R.id.navcommunities -> fragmentReplace(Communities())
                R.id.navchats->fragmentReplace(Chat())
                R.id.navinbox->fragmentReplace(inbox())
                R.id.navcreate->fragmentReplace(Create())
                else->{

                }
            }

            true


        }


        return binding.root
//        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    private fun fragmentReplace(frag: Fragment){
        var fragmanager = parentFragmentManager
        var transaction = fragmanager.beginTransaction()
        transaction.replace(R.id.mainfraglayout,frag)
        transaction.addToBackStack(null);
        transaction.commit()
    }

}