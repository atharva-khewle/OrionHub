package com.example.orionhub
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
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.database.DatabaseReference
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//import com.example.orionhub.apple1


class MainPage : Fragment() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : FragmentMainPageBinding
    private lateinit var bottomnav : BottomNavigationView
    private lateinit var database : DatabaseReference
    private  val sharedViewModel: SharedViewModel by activityViewModels()

    private var subredditsList: List<String>? = null
    private lateinit var left_nav_view : NavigationView;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMainPageBinding.inflate(inflater,container,false)
        fragmentReplace(Home())
        bottomnav=binding.bottomNavigationView2;
        bottomnav.labelVisibilityMode =BottomNavigationView.LABEL_VISIBILITY_LABELED



        val fireclass = Firebasefun();
        fireclass.initialiseFirebase()


        //topnavbar
        val topnavbar = view?.findViewById<Toolbar>(R.id.topnavbar)
        val topnavmenuicon= topnavbar?.navigationIcon


        //left right nav
        val drawerlayout = view?.findViewById<DrawerLayout>(R.id.drawerlayout1)
        val leftnav = view?.findViewById<NavigationView>(R.id.left_nav_view)
        val rightnav= view?.findViewById<NavigationView>(R.id.right_nav_view)




        //shared prefs
        val prefs: SharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        prefs.edit().putString("USERNAME_KEY", username).apply()


        fun setupUI(){


            if(prefs.getString("USERNAME_KEY", null)===null){
                //if name does not exist
                lifecycleScope.launch {
                    val combinedData = MediatorLiveData<Triple<String?, String?, Int?>>().apply {
                        var email: String? = null
                        var pass: String? = null
                        var isRegister: Int? = null

                        fun update() {
                            value = Triple(email, pass, isRegister)
                        }

                        addSource(sharedViewModel.email) {
                            email = it
                            update()
                        }
                        addSource(sharedViewModel.password) {
                            pass = it
                            update()
                        }
                        addSource(sharedViewModel.isregister) {
                            isRegister = it
                            update()
                        }
                    }

                    combinedData.observe(viewLifecycleOwner) { (email, pass, isRegister) ->
                        if (email != null && pass != null && isRegister != null) {
                            // All data is available
                            if (isRegister == 1) {
                                // Handle registration
                                lifecycleScope.launch {
                                    try {
                                        // Call the suspending function within a coroutine //created inised spacial courintine
                                        val msg = withContext(Dispatchers.IO) {
                                            fireclass.registerUser( sharedViewModel.username.value!!, pass,email)
                                        }

                                        if (msg == 1) {
                                            prefs.edit().putString("USERNAME_KEY", sharedViewModel.username.value).apply()
                                            prefs.edit().putString("EMAIL_KEY", sharedViewModel.email.value).apply()
                                            prefs.edit().putString("PASSWORD_KEY", sharedViewModel.password.value).apply()

                                            delay(500)

                                            //initializing sendbird
                                            val appInstance = requireActivity().getApplication() as BaseApplication
                                            appInstance.initializethebird()

                                            Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(requireContext(), "Registration failed\nClose app and Please try again", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                //login
                                lifecycleScope.launch {
                                    try {
                                        //already logged in so no need firebase
                                        prefs.edit().putString("USERNAME_KEY", sharedViewModel.username.value).apply()
                                        prefs.edit().putString("EMAIL_KEY", sharedViewModel.email.value).apply()
                                        prefs.edit().putString("PASSWORD_KEY", sharedViewModel.password.value).apply()
                                        delay(500)

                                        //initializing sendbird
                                        val appInstance = requireActivity().getApplication() as BaseApplication
                                        appInstance.initializethebird()
                                    } catch (e: Exception) {
                                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{

                binding.rightNavView.getHeaderView(0).findViewById<TextView>(R.id.setthisusername).text = prefs.getString("USERNAME_KEY", null)
                //initializing sendbird
                val appInstance = requireActivity().getApplication() as BaseApplication
                appInstance.initializethebird()
                //to open subredddit page by clickng on  item
                lifecycleScope.launch {
                    subredditsList = fireclass.mysubredditlists(requireContext())
                    //left nav open subreddit
                    left_nav_view = binding.leftNavView
                    updateNavigationMenu()
                    left_nav_view.setNavigationItemSelectedListener { menuItem->
                        val subredditName = menuItem.title.toString()
                        openSubredditFragment(subredditName)
                        binding.drawerlayout1.closeDrawer(GravityCompat.START)
                        true
                    }
                }
            }



            binding.topnavbar.setNavigationOnClickListener {
                binding.drawerlayout1.openDrawer(GravityCompat.START)
            }
            binding.topnavbar.setOnMenuItemClickListener {
                binding.drawerlayout1.openDrawer(GravityCompat.END)
                true
            }
            binding.rightNavView.setNavigationItemSelectedListener {
                if(it.title.toString()=="Create Community"){
                    Toast.makeText(requireContext(), "create", Toast.LENGTH_SHORT).show()
                    openCreateSubredditFragment()
                }
                binding.drawerlayout1.closeDrawer(GravityCompat.END)
                true
            }









            if (leftnav != null) {
                leftnav.setNavigationItemSelectedListener(this)
            }
            if (rightnav != null) {
                rightnav.setNavigationItemSelectedListener(this)
            }

            val toggle = ActionBarDrawerToggle(requireActivity() , drawerlayout, R.string.open_nav,R.string.close_nav)


            if (drawerlayout != null) {
                drawerlayout.addDrawerListener(toggle)
                toggle.syncState()
            }



            //onclick for bottom navgation
            binding.bottomNavigationView2.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.navhome -> {
                        fragmentReplace(Home())
                        binding.topnavbar.setTitle("Home")
                    }
                    R.id.navcommunities -> {
                        fragmentReplace(Communities())
                        binding.topnavbar.setTitle("Communities")

                    }
                    R.id.navchats->{
                        fragmentReplace(Chat())
                        binding.topnavbar.setTitle("Chats")

                    }
                    R.id.navinbox->{
                        fragmentReplace(inbox())
                        binding.topnavbar.setTitle("Inbox")
                    }
                    R.id.navcreate->{
                        fragmentReplace(Create())
                        binding.topnavbar.setTitle("Create a post")

                    }

                    else->{

                    }
                }

                true
            }


        }
        lifecycleScope.launch {
            setupUI()
            delay(4000)
            setupUI()
        }

        return binding.root
//        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }


    private fun updateNavigationMenu() {
        val menu = left_nav_view.menu
        menu.clear() // Clear existing items if necessary
        subredditsList?.forEachIndexed { index, name ->
            menu.add(Menu.NONE, index, Menu.NONE,"r/"+ name)
        }
    }

    private fun openCreateSubredditFragment() {
        val fragment = CreateSubreddit.newInstance()
        displayFragment(fragment)
    }



    private fun openSubredditFragment(subredditName: String) {
        val fragment = SubredditFragment.newInstance(subredditName)
        displayFragment(fragment)
    }



    private fun displayFragment(fragment: Fragment) {
      requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.inMainFrag_layout, fragment) // Replace with your FrameLayout ID
            .commit()
    }



    private fun fragmentReplace(frag: Fragment){
        var fragmanager = parentFragmentManager
        var transaction = fragmanager.beginTransaction()
//        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmanager.beginTransaction()
        fragmentTransaction.replace(R.id.inMainFrag_layout, frag)
        fragmentTransaction.commit()
    }




    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
        return true
    }





}