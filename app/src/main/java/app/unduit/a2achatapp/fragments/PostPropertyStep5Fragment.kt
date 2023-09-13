package app.unduit.a2achatapp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.adapters.PostPropertyImagesAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep5Binding
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.helpers.gone
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.models.PropertyData
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.registerImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class PostPropertyStep5Fragment : Fragment() {
    private lateinit var binding: FragmentPostPropertyStep5Binding

    private val args: PostPropertyStep5FragmentArgs by navArgs()

    private lateinit var propertyData: PropertyData
    private var isEdit = false

    private var negotiationStr = "Negotiable"

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    val adapter : PostPropertyImagesAdapter  by lazy {
        PostPropertyImagesAdapter()
    }
    private var propertyImages = ArrayList<Any>()
    private var propertyImagesStr: ArrayList<String>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep5Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }
    override


    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {

        isEdit = args.isEdit
        propertyData = args.propertyData

        spinnerManager()
        binding.postListBtn.setOnClickListener {
            uploadData()
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnUpload.setOnClickListener {

            imagePickerLauncher.launch(ImagePickerConfig() {
                isShowCamera = false
                doneButtonText = "DONE"
                imageTitle = "Tap to select"
            })
        }

        binding.rvImages.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvImages.adapter = adapter

        if(isEdit) {
            setData()
            binding.saveDraft.gone()
            binding.postListBtn.gone()
            binding.postListBtnEdit.visible()
        }
    }

    private val imagePickerLauncher = registerImagePicker { images ->

        images.forEach {
            propertyImages.add(it.uri)
        }
        adapter.submitList(null)
        adapter.submitList(propertyImages)
    }

    private fun spinnerManager() {
        negotiationSpinner()
    }

    private fun negotiationSpinner(){
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.negotiationList())

        binding.spinnerNegotiation.adapter = adapter
        binding.spinnerNegotiation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.negotiationList()[position]
                    negotiationStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun setData(){
        binding.op.setText(propertyData.op)
        binding.sp.setText(propertyData.sp)
        binding.roi.setText(propertyData.roi)
        binding.property.setText(propertyData.property_title)

        SpinnersHelper.negotiationList().forEachIndexed { index, item ->
            if(item.equals(propertyData.negotiation, true)) {
                binding.spinnerNegotiation.setSelection(index)
            }
        }

        propertyData.property_images?.forEach {
            propertyImages.add(it)
            propertyImagesStr?.add(it)
        }
        adapter.submitList(null)
        adapter.submitList(propertyImages)

    }

    private fun uploadData() {
        val spStr = binding.sp.text.toString()
        val title = binding.property.text.toString()

        if(spStr.isEmpty()) {
            requireContext().showToast("Please enter a value for SP")
        } else if(title.isEmpty()) {
            requireContext().showToast("Please enter a title")
        } else {

            val db = Firebase.firestore
            val id: String = if(isEdit) propertyData.uid else db.collection("properties").document().id

            val totalImages = propertyImages.size

            if(propertyImages.size > 0) {

                progressDialog.progressBarVisibility(true)
                val storage = Firebase.storage
                val storageRef = storage.reference

                var count = 0
                propertyImages.forEach { uri ->
                    val time = System.currentTimeMillis()
                    val imagesRef: StorageReference = storageRef.child("property_images/${id}/image_${time}")

                    if(uri is Uri) {
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

                                propertyImagesStr!!.add(downloadUri.toString())
                                count++

                                if(count == propertyImages.size) {
                                    propertyData.property_images = propertyImagesStr
                                    uploadToDatabase(db, id)
                                }
                            } else {
                                // Handle failures
                                requireContext().showToast("Upload failed. Please try again")
                                progressDialog.progressBarVisibility(false)
                            }
                        }
                    } else {
                        count++
                        if(count == propertyImages.size) {
                            propertyData.property_images = propertyImagesStr
                            uploadToDatabase(db, id)
                        }
                    }
                }

            } else {
                progressDialog.progressBarVisibility(true)
                uploadToDatabase(db, id)
            }
        }
    }

    private fun uploadToDatabase(db: FirebaseFirestore, id: String) {

        propertyData.op = binding.op.text.toString()
        propertyData.sp = binding.sp.text.toString()
        propertyData.roi = binding.roi.text.toString()
        propertyData.negotiation = negotiationStr
        propertyData.property_title = binding.property.text.toString()
//        propertyData.created = DateHelper.getCurrentDateTime()

        propertyData.uid = id

        db.collection("properties")
            .document(id)
            .set(propertyData)
            .addOnSuccessListener {
                progressDialog.progressBarVisibility(false)
                requireContext().showToast("Property Posted!")
                findNavController().navigate(R.id.action_postPropertyStep5Fragment_to_homeFragment)
            }.addOnFailureListener {e ->
                progressDialog.progressBarVisibility(false)
                requireContext().showToast(e.localizedMessage.toString())
            }
    }

    private fun updateDatabase(db: FirebaseFirestore, id: String){

    }

}