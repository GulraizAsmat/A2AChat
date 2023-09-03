package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.UserExperience
import app.unduit.a2achatapp.adapters.UserExperienceAdapter
import app.unduit.a2achatapp.databinding.FragmentSignupExperienceBinding
import app.unduit.a2achatapp.models.User


class SignupExperienceFragment : Fragment() {

    private lateinit var binding: FragmentSignupExperienceBinding
    private val args: SignupExperienceFragmentArgs by navArgs()

    private var userData: User? = null

    private var experienceStr: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupExperienceBinding.inflate(inflater)
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
            userData?.experience = experienceStr

            findNavController().navigate(SignupExperienceFragmentDirections.actionSignupExperienceFragmentToSignupProfilePicFragment(userData!!))
        }

        val adapter = UserExperienceAdapter(requireContext())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerExperience.adapter = adapter
        binding.spinnerExperience.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                experienceStr = adapter.getItem(position)?.category
                checkBtnEnabled()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.btnNext.isEnabled = false
            }

        }

    }

    private fun checkBtnEnabled(){
        binding.btnNext.isEnabled = experienceStr?.isNotEmpty() == true && experienceStr != getString(R.string.text_select_option)
    }
}