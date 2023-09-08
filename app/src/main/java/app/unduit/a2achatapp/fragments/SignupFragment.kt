package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.UserCategoryAdapter
import app.unduit.a2achatapp.databinding.FragmentSignupBinding
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding

    private lateinit var auth: FirebaseAuth

    private var userCategory: String? = ""

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater)
        binding.lifecycleOwner = this

//        auth = Firebase.auth/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        val adapter = UserCategoryAdapter(requireContext())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUserType.adapter = adapter
        binding.spinnerUserType.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                userCategory = adapter.getItem(position)?.category
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            validateData()
        }

    }

    private fun validateData(){
        if(binding.etName.text.toString().isEmpty()) {
            requireContext().showToast("Please enter name")
        } else if(binding.etEmail.text.toString().isEmpty()) {
            requireContext().showToast("Please enter email")
        } else if(binding.etPassword.text.toString().isEmpty()) {
            requireContext().showToast("Please enter password")
        } else if(binding.etPhone.text.toString().isEmpty()) {
            requireContext().showToast("Please enter phone number")
        } else if(binding.etWhatsapp.text.toString().isEmpty()) {
            requireContext().showToast("Please enter whatsapp number")
        } else if(userCategory?.isEmpty() == true || userCategory == getString(R.string.text_select_option)) {
            requireContext().showToast("Please select a role")
        }
        else if(binding.etPassword.text.toString().length<8){
            requireContext().showToast("Password should be 8 character")
        }
        else {
            createUser()
        }
    }

    private fun createUser(){
        progressDialog.progressBarVisibility(true)
         auth = FirebaseAuth.getInstance()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignupFrag", "createUserWithEmail:success")
                    val user = auth.currentUser
                    val userData = User()
                    userData.uid = user?.uid
                    userData.name = binding.etName.text.toString()
                    userData.email = binding.etEmail.text.toString()
                    userData.phone = binding.etPhone.text.toString()
                    userData.whatsapp = binding.etWhatsapp.text.toString()
                    userData.status = userCategory
                    userData.company = binding.etCompany.text.toString()

                    findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToCertificationCheckFragment(userData))
                } else {
                    // If sign in fails, display a message to the user.
                    requireContext().showToast(task.exception?.message.toString())
                }
                progressDialog.progressBarVisibility(false)
            }
    }

}