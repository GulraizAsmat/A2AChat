package app.unduit.a2achatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep4Binding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep5Binding


class PostPropertyStep5Fragment : Fragment() {
    private lateinit var binding: FragmentPostPropertyStep5Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep5Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }
    override


    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {

            findNavController().navigate(R.id.action_postPropertyStep5Fragment_to_postPropertyStep6Fragment)
        }
    }

}