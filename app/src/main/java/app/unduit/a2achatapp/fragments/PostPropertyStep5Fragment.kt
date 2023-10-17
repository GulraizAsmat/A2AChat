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
import app.unduit.a2achatapp.models.User
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.registerImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.Calendar


class PostPropertyStep5Fragment : Fragment() {
    private lateinit var binding: FragmentPostPropertyStep5Binding

    private val args: PostPropertyStep5FragmentArgs by navArgs()

    private lateinit var propertyData: PropertyData
    private var isEdit = false

    private var negotiationStr = "Negotiable"
    var furnishingStr = "Unfurnished"
    var commissionStr = "covered"

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
    private fun defaultData(propertyData: PropertyData){

        if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Residential"){
            selectRentAndResidents()
        }
        else if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Commercial"){
            selectRentAndCommercial()
        }

    }

    fun init() {

        isEdit = args.isEdit
        propertyData = args.propertyData

        spinnerManager()
        binding.postListBtn.setOnClickListener {
            uploadData()
        }
        binding.postListBtnEdit.setOnClickListener {
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



        if(args.isLand){
            selectedLand()
        }

        if(isEdit) {

            setData()
            binding.saveDraft.gone()
            binding.postListBtn.visibility=View.INVISIBLE
            binding.postListBtnEdit.visibility=View.VISIBLE
        }
        defaultData(propertyData)
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
        finishingSpinner()
        commissionSpinner()
    }


    fun selectedLand(){

        binding.finishingTitle.visibility=View.GONE
        binding.spinnerFinishing.visibility=View.GONE
        binding.finishingIcArrow.visibility=View.GONE
    }
    private fun finishingSpinner() {


        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.finishingList())

        binding.spinnerFinishing.adapter = adapter
        binding.spinnerFinishing.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.finishingList()[position]
                    furnishingStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun commissionSpinner() {


        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.commissionList())

        binding.spinnerCommission.adapter = adapter
        binding.spinnerCommission.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.commissionList()[position]
                    commissionStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
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
        binding.property.setText(propertyData.property_title)
        binding.description.setText(propertyData.property_description)

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

        SpinnersHelper.finishingList().forEachIndexed { index, item ->
            if(item.equals(propertyData.furnishing, true)) {
                binding.spinnerFinishing.setSelection(index)
            }
        }

        SpinnersHelper.commissionList().forEachIndexed { index, s ->
            if(  propertyData.commission==s){
                binding.spinnerCommission.setSelection(index)
            }
        }
    }

    private fun uploadData() {
        val title = binding.property.text.toString()

        if(title.isEmpty()) {
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
        propertyData.negotiation = negotiationStr
        propertyData.property_title = binding.property.text.toString()
        propertyData.property_description = binding.description.text.toString()
        propertyData.furnishing = furnishingStr
        propertyData.created_date = System.currentTimeMillis().toString()
        propertyData.uid = id
        propertyData.post_type = "property"
        propertyData.commission = commissionStr

        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val ref = db.collection("users").document(cUser.uid)

            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.toObject(User::class.java)

                    propertyData.user_name = data?.name
                    propertyData.user_picture = data?.profile_image
                    propertyData.user_company = data?.company
                    propertyData.user_experience = data?.experience
                    propertyData.user_specialty = data?.speciality
                    propertyData.brn = data?.brn.toString()
                    propertyData.verified = data?.verified == true
                    propertyData.user_phone = data?.phone.toString()
                    propertyData.user_whatsapp = data?.whatsapp.toString()

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

                } ?: run {
                    //Need to logout
                    progressDialog.progressBarVisibility(false)
                    requireContext().showToast("An error occurred. Try again later!")
                }
            }.addOnFailureListener { e ->
                progressDialog.progressBarVisibility(false)
                requireContext().showToast(e.localizedMessage.toString())
            }
        } ?: run {
            //Need to logout
            progressDialog.progressBarVisibility(false)
            requireContext().showToast("An error occurred. Try again later!")
        }
    }

    private fun selectRentAndResidents(){

        binding.clProcessBarForRent.visibility=View.VISIBLE
        binding.clProcessBar.visibility=View.INVISIBLE
        binding.descriptionTitle.visibility=View.GONE
        binding.description.visibility=View.GONE
    }

    private fun selectRentAndCommercial(){
        binding.descriptionTitle.visibility=View.GONE
        binding.description.visibility=View.GONE
        binding.clProcessBarForRent.visibility=View.VISIBLE
        binding.clProcessBar.visibility=View.INVISIBLE
    }
}