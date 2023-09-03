package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep1Binding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep2Binding


class PostPropertyStep2Fragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentPostPropertyStep2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep2Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }


    fun init(){
        listeners()
    }
    fun listeners(){
        binding.nextBtn.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.next_btn ->{
                findNavController().navigate(R.id.action_postFragment_to_postPropertyStep3Fragment)
            }
        }
    }


}