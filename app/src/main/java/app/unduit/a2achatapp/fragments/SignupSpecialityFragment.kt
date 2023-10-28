package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentSignupSpecialityBinding
import app.unduit.a2achatapp.models.User

class SignupSpecialityFragment : Fragment() {

    private lateinit var binding: FragmentSignupSpecialityBinding
    private val args: SignupSpecialityFragmentArgs by navArgs()

    private var specialityList = ArrayList<String>()

    private var userData: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupSpecialityBinding.inflate(inflater)
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
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            userData?.speciality = specialityList

            findNavController().navigate(SignupSpecialityFragmentDirections.actionSignupSpecialityFragmentToSignupAreasFragment(userData!!))
        }

        binding.cbOffplan.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                specialityList.add("Off-Plan")
            } else {
                specialityList.remove("Off-Plan")
            }
            checkButtonEnabled()
        }

        binding.cbSecondary.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                specialityList.add("Secondary")
            } else {
                specialityList.remove("Secondary")
            }
            checkButtonEnabled()
        }

        binding.cbRentals.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                specialityList.add("Rentals")
            } else {
                specialityList.remove("Rentals")
            }
            checkButtonEnabled()
        }

        binding.cbLands.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                specialityList.add("Lands")
            } else {
                specialityList.remove("Lands")
            }
            checkButtonEnabled()
        }

        binding.cbBuildings.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                specialityList.add("Buildings")
            } else {
                specialityList.remove("Buildings")
            }
            checkButtonEnabled()
        }
    }

    private fun checkButtonEnabled(){
        binding.btnNext.isEnabled = specialityList.size > 0
    }

}