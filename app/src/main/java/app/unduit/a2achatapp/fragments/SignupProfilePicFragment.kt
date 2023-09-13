package app.unduit.a2achatapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentSignupBinding
import app.unduit.a2achatapp.databinding.FragmentSignupProfilePicBinding
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.models.User
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class SignupProfilePicFragment : Fragment() {

    private lateinit var binding: FragmentSignupProfilePicBinding
    private val args: SignupProfilePicFragmentArgs by navArgs()

    private var userData: User? = null

    private var imageUri: Uri? = null

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupProfilePicBinding.inflate(inflater)
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
            //Create User and upload to firebase
            uploadToStorage()
        }

        binding.ivAdd.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .start()
        }
    }

    private fun uploadToStorage() {
            imageUri?.let { uri ->
                progressDialog.progressBarVisibility(true)

                val storage = Firebase.storage
                val storageRef = storage.reference

                val imagesRef: StorageReference? = storageRef.child("profile_pictures/${userData?.uid}")
                val uploadTask = imagesRef?.putFile(uri)

                val urlTask = uploadTask?.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imagesRef.downloadUrl
                }?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        userData?.profile_image = downloadUri.toString()
                        uploadToDatabase()
                    } else {
                        // Handle failures
                        requireContext().showToast("Upload failed. Please try again")
                        progressDialog.progressBarVisibility(false)
                    }
                }

            }
    }

    private fun uploadToDatabase() {
        val db = Firebase.firestore
        db.collection("users")
            .document(userData?.uid!!)
            .set(userData!!)
            .addOnSuccessListener {
                progressDialog.progressBarVisibility(false)
                findNavController().navigate(SignupProfilePicFragmentDirections.actionSignupProfilePicFragmentToHomeFragment())
            }.addOnFailureListener { e ->
                progressDialog.progressBarVisibility(false)
                requireContext().showToast(e.localizedMessage.toString())
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
            binding.ivAdd.setImageURI(fileUri)
            imageUri = fileUri
            binding.btnNext.text = getString(R.string.text_next)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            requireContext().showToast(ImagePicker.getError(data))
        }
    }
}