package app.unduit.a2achatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep6Binding

class PostPropertyStep6Fragment : Fragment() {

    private lateinit var binding: FragmentPostPropertyStep6Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragmen6
        binding = FragmentPostPropertyStep6Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {

            findNavController().navigate(R.id.action_postPropertyStep6Fragment_to_postPropertyStep7Fragment)
        }
    }


}