package app.unduit.a2achatapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setScreenTitle()
        listeners()
    }

    fun listeners() {
        binding.backIcon.setOnClickListener(this)
        binding.clProfile.setOnClickListener(this)
        binding.clVerify.setOnClickListener(this)
    }

    private fun setScreenTitle() {
        binding.screenTitle.text = "Profile"
    }

    private fun verificationRequestDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setMessage("Your verification request has been sent to the administrator")
        builder.setPositiveButton("Okay") { dialog, which ->

        }

        builder.show()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.back_icon -> {
                findNavController().popBackStack()
            }

            R.id.cl_profile -> {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
            }

            R.id.cl_verify -> {
                verificationRequestDialog()
            }
        }
    }


}