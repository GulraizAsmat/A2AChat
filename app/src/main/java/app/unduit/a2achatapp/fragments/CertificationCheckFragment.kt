package app.unduit.a2achatapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentCertificationCheckBinding
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.gone
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.models.User
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class CertificationCheckFragment : Fragment() {

    private lateinit var binding: FragmentCertificationCheckBinding
    private val args: CertificationCheckFragmentArgs by navArgs()

    private var userData: User? = null

    private var imageUri: Uri? = null

    private var isCertified: Boolean = false

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCertificationCheckBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        userData = args.userData

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(binding.groupBottomsheet.visibility == View.VISIBLE) {
                binding.groupBottomsheet.gone()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (binding.root.findViewById<AppCompatRadioButton>(checkedId)) {
                binding.radioYes -> {
                    binding.groupBottomsheet.visible()
                    isCertified = true
                }
                binding.radioNo -> {
                    binding.btnNext.isEnabled = true
                    isCertified = false
                }
            }
        }

        binding.btnUpload.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .start()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            userData?.certified = isCertified
            findNavController().navigate(CertificationCheckFragmentDirections.actionCertificationCheckFragmentToSignupSpecialityFragment(userData!!))
        }

    }

    private fun uploadToStorage(){
        imageUri?.let { uri ->
            progressDialog.progressBarVisibility(true)

            val storage = Firebase.storage
            val storageRef = storage.reference

            val imagesRef: StorageReference? = storageRef.child("RERA/${userData?.uid}")
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

                        userData?.certified = isCertified
                        userData?.certified_image = downloadUri.toString()
                        requireContext().showToast("Image uploaded successfully")
                        findNavController().navigate(CertificationCheckFragmentDirections.actionCertificationCheckFragmentToSignupSpecialityFragment(
                            userData!!
                        ))
                    } else {
                        // Handle failures
                        requireContext().showToast("Upload failed. Please try again")
                    }
                    progressDialog.progressBarVisibility(false)
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
//            binding.ivProfile.setImageURI(fileUri)
            imageUri = fileUri
            uploadToStorage()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            requireContext().showToast(ImagePicker.getError(data))
        }
    }
}