package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep2Binding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep3Binding




class PostPropertyStep3Fragment : Fragment() {


    private lateinit var binding: FragmentPostPropertyStep3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep3Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    init()

        binding.nextBtn.setOnClickListener {

            findNavController().navigate(R.id.action_postPropertyStep3Fragment_to_postPropertyStep4Fragment)
        }
    }


    fun init(){

    }

    fun listeners(){

    }



}