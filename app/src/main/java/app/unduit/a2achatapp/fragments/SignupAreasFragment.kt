package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentSignupAreasBinding
import app.unduit.a2achatapp.models.User

class SignupAreasFragment : Fragment() {

    private lateinit var binding: FragmentSignupAreasBinding
    private val args: SignupAreasFragmentArgs by navArgs()

    private var userData: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupAreasBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        userData = args.userData

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            findNavController().navigate(SignupAreasFragmentDirections.actionSignupAreasFragmentToSignupExperienceFragment(userData!!))
        }

    }
}