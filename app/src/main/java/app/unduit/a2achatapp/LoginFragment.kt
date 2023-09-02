package app.unduit.a2achatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater)
        binding.lifecycleOwner = this

        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.btnLogin.setOnClickListener {
            val email = binding.tvEmail.text.toString()
            val password = binding.tvPassword.text.toString()
            validateData(email, password)
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
        }
    }

    private fun validateData(email: String, password: String) {
        if(email.isEmpty()) {
            requireContext().showToast("Please enter email")
        } else if(password.isEmpty()) {
            requireContext().showToast("Please enter password")
        } else {
            progressDialog.progressBarVisibility(true)
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    requireContext().showToast("Login successful")
                    //Go to home
                } else {
                    requireContext().showToast(task.exception?.message.toString())
                }
                progressDialog.progressBarVisibility(false)
            }
    }


}