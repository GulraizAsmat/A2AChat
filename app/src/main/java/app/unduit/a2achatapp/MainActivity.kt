package app.unduit.a2achatapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import app.unduit.a2achatapp.helpers.Const

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

    }

    fun init(){
        navController = this.findNavController(R.id.fragmentContainerView)

        statusBarColorChange()
    }

    private fun statusBarColorChange() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = 0
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(Const.screenName=="favourite_icon " ||Const.screenName== "notification_icon"
            ||Const.screenName== "profile_image" ||Const.screenName== "property_list"
            ||Const.screenName== "add_property" ||Const.screenName== "chat"){


            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController

            val navGraph = navController.navInflater.inflate(R.navigation.navigation)
            navGraph.setStartDestination(R.id.homeFragment)
            navController.graph = navGraph
            this.navController = navController



        }


    }

}