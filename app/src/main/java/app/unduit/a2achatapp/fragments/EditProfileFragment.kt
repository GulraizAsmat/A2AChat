package app.unduit.a2achatapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentEditProfileBinding
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.models.User
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var auth: FirebaseAuth

    private var userData: User? = null

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    private var imageUri: Uri? = null
    private var isProfileImageChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        getUserData()
    }

    private fun init() {
        binding.etEmail.isEnabled = false

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            if(isProfileImageChanged) {
                uploadImage()
            } else {
                saveUserData()
            }
        }

        binding.profileImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .start()
        }
    }

    private fun getUserData() {
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("users").document(cUser.uid)

            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.data

                    userData = User()
                    userData?.uid = cUser.uid
                    userData?.name = data?.get("name") as String?
                    userData?.email = data?.get("email") as String?
                    userData?.phone = data?.get("phone") as String?
                    userData?.whatsapp = data?.get("whatsapp") as String?
                    userData?.company = data?.get("company") as String?
                    userData?.profile_image = data?.get("profile_image") as String?
                    userData?.brn = data?.get("brn") as String?
                    userData?.verified = data?.get("verified") as Boolean?
                    userData?.status = data?.get("status") as String?
                    userData?.certified_image = data?.get("certified_image") as String?
                    userData?.experience = data?.get("experience") as String?
                    userData?.location = data?.get("location") as String?


                    setUserData()

                } ?: run {
                    //Need to logout
                    progressDialog.progressBarVisibility(false)
                }
            }
        } ?: run {
            //Need to logout
            progressDialog.progressBarVisibility(false)
        }

    }

    private fun setUserData(){

        userData?.let { user ->
            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phone)
            binding.etWhatsapp.setText(user.whatsapp)
            binding.etCompany.setText(user.company)
            binding.etBrn.setText(user.brn)

            if(user.verified == true){
                binding.verifiedIcon.visibility=View.VISIBLE
            }else {
                binding.verifiedIcon.visibility=View.GONE
            }

            Glide.with(this).load(user.profile_image).fallback(R.drawable.ic_profile_pic_placeholder)
                .placeholder(R.drawable.ic_profile_pic_placeholder).into(binding.profileImage)
        }

        progressDialog.progressBarVisibility(false)
    }

    private fun uploadImage(){
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
                    saveUserData()
                } else {
                    saveUserData()
                }
            }
        }
    }

    private fun saveUserData() {
        userData?.let { user ->
            progressDialog.progressBarVisibility(true)
            user.name = binding.etName.text.toString()
            user.email = binding.etEmail.text.toString()
            user.phone = binding.etPhone.text.toString()
            user.whatsapp = binding.etWhatsapp.text.toString()
            user.company = binding.etCompany.text.toString()

            if(binding.etBrn.text.isNotEmpty()){
                user.brn = binding.etBrn.text.toString()
                user.verified =true
            }else {

                user.verified =false
            }

            val db = Firebase.firestore
            db.collection("users")
                .document(user.uid!!)
                .set(user)
                .addOnSuccessListener {
                    progressDialog.progressBarVisibility(false)
                    requireContext().showToast("Profile information updated")
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }.addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    requireContext().showToast(e.localizedMessage.toString())
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
            binding.profileImage.setImageURI(fileUri)
            imageUri = fileUri
            isProfileImageChanged = true
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            requireContext().showToast(ImagePicker.getError(data))
        }
    }
}