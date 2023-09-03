package app.unduit.a2achatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep7Binding


class PostPropertyStep7Fragment : Fragment() {
    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentPostPropertyStep7Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep7Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postListBtn.setOnClickListener {

            findNavController().navigate(R.id.action_postPropertyStep6Fragment_to_postPropertyStep7Fragment)
        }
    }

}