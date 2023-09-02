package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.back_icon
import kotlinx.android.synthetic.main.fragment_profile.screen_title

class ProfileFragment : Fragment(),View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    init()
    }

    private fun init(){
        setScreenTitle()
        listeners()
    }

    fun listeners(){
        back_icon.setOnClickListener(this)
    }

    private fun setScreenTitle(){

        screen_title.text="Profile"
    }

    override fun onClick(v: View?) {
            when(v!!.id){
                R.id.back_icon->{
                    findNavController().popBackStack()
                }



            }
    }


}