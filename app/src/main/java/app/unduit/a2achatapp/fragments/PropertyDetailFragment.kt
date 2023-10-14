package app.unduit.a2achatapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.PropertyFeaturesAdapter
import app.unduit.a2achatapp.databinding.FragmentPropertyDetailBinding
import app.unduit.a2achatapp.helpers.Const

import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.addComma
import app.unduit.a2achatapp.helpers.gone
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PropertyDetailFragment : Fragment(), View.OnClickListener {

    var TAG = "PropertyDetailFragment"

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentPropertyDetailBinding
    private val args: PropertyDetailFragmentArgs by navArgs()

    private var propertyData: PropertyData? = null

    private var isFrom = ""
    private var favouriteStatus = false

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    var propertyFeaturesList = ArrayList<String>()

    private val adapter: PropertyFeaturesAdapter by lazy {
        PropertyFeaturesAdapter(
            requireContext(),
            propertyFeaturesList
        )
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPropertyDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }


    private fun init() {
        propertyData = args.propertyData
        isFrom = args.isFrom

        binding.btnBack.setOnClickListener {
            if (isFrom == "home") {
                requireActivity().onBackPressed()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }

        getData()
        listeners()
    }

    private fun listeners() {
        binding.btnFav.setOnClickListener(this)
        binding.btnPhone.setOnClickListener(this)
        binding.btnChat.setOnClickListener(this)
        binding.btnWhatsapp.setOnClickListener(this)
        binding.agentInfoBtn.setOnClickListener(this)
        binding.ivPropertySlider.setOnClickListener(this)
    }

    private fun getData() {
        progressDialog.progressBarVisibility(true)

        propertyData?.let { pData ->
            val db = Firebase.firestore
            val ref = db.collection("properties").document(pData.uid)
                Log.e("Tag123","Const.PropertyType :: "+Const.PropertyType)
            if (Const.PropertyType == "match") {
                setData(pData)
            } else {
                ref.get().addOnSuccessListener { snapshot ->

                    snapshot?.let { snap ->
                        val data = snap.toObject(PropertyData::class.java)

                        data?.let {
                            setData(it)
                        } ?: run {
                            setData(pData)
                        }
                    }
                    checkFavouritePostStatus(pData)
                }.addOnFailureListener {
                    setData(pData)
                }
            }


        }
    }









    private fun buyAndResidence(data: PropertyData) {
        binding.propertyPrice.visibility = View.GONE


        binding.yearHandoverDot.visibility = View.GONE
        binding.yearHandoverStatus.visibility = View.GONE

        binding.layoutDescription.visibility = View.VISIBLE
        binding.divider4.visibility = View.VISIBLE


        binding.negotiationDot.visibility = View.GONE
        binding.negotiationStatus.visibility = View.GONE

        binding.commissionDot.visibility = View.GONE
        binding.commissionStatus.visibility = View.GONE

        binding.propertyStatus.visibility = View.GONE
        binding.propertyStatusDot.visibility = View.GONE

        binding.checksDot.visibility = View.GONE
        binding.checksStatus.visibility = View.GONE
        if (data.maidroom) {
            binding.maidRoomStatus.text = "Maid Room : Yes"
        } else {
            binding.maidRoomStatus.text = "Maid Room : No"
        }

        if (data.balcony) {
            binding.balconyStatus.text = "Balcony : Yes"
        } else {
            binding.balconyStatus.text = "Balcony: No"
        }



        binding.occopancyStatus.text = "Occupancy : " + data.occupancy
        binding.propertyDescription.text = data.property_description


        if (data.purpose == "Sale" && data.purpose_type == "Residential") {
            binding.serviceChargeStatus.text = "Purchase goal : " + data.purchase_goal
            binding.floorStatus.text = "Payment method : " + data.payment_method
            binding.occopancyStatus.text = "Occupancy : " + data.occupancy

        } else if (data.purpose == "Rent" && data.purpose_type == "Residential") {

            binding.serviceChargeStatus.text = "Number of checks : " + data.number_of_checks
            binding.floorStatus.text = "Moving Time : " + data.property_moving_time
            binding.occopancyStatus.text = "Furniture : " + data.property_furniture

        } else if (data.purpose == "Sale" && data.purpose_type == "Commercial") {


            binding.icBed.visibility = View.GONE
            binding.propertyRooms.visibility = View.GONE

            binding.icWashroom.visibility = View.GONE
            binding.propertyWashrooms.visibility = View.GONE

            binding.maidRoomStatus.visibility = View.GONE
            binding.maidRoomDot.visibility = View.GONE

            binding.checksStatus.visibility = View.VISIBLE
            binding.checksDot.visibility = View.VISIBLE

            binding.balconyStatus.visibility = View.GONE
            binding.balconyRoomDot.visibility = View.GONE

            binding.occopancyStatus.text = "Occupancy : " + data.occupancy
            binding.checksStatus.text = "Fitting : " + data.fitting
            binding.serviceChargeStatus.text = "Purchase goal : " + data.purchase_goal
            binding.floorStatus.text = "Payment method : " + data.payment_method

        }


        binding.propertyArea.text =
            data.property_size_min + " - " + data.property_size_max + " sqft"

        binding.propertyLounge.text = data.budget_min + " - " + data.budget_max + " budget"


        binding.propertyName.text = data.property_title
        if(data.project.isEmpty()) {
            binding.propertyProject.gone()
        }
        binding.propertyProject.text = data.project
        binding.propertyLocation.text = data.area_community

        val bedrooms =
            if (data.bedrooms.equals("Studio", true)) data.bedrooms else data.bedrooms + " Bedrooms"
        binding.propertyRooms.text = bedrooms
        binding.propertyWashrooms.text = data.bathrooms + " Bathrooms"


        val description = data.property_description.ifEmpty { "N/A" }
        binding.propertyDescription.text = description

        binding.propertyCategory.text = data.property_type

//        binding.agentSpecialty.text = specialty

        binding.tvListed.text = DateHelper.getDateTimeFromTimestamp(data.created_date)
        binding.tvReference.text = data.uid

    }

    private fun setData(data: PropertyData) {

        if(data.verified){
            binding.verifiedIcon.visibility=View.VISIBLE
        }
        binding.agentExperience.text = data.brn
        Glide.with(this).load(data.user_picture).fallback(R.drawable.ic_profile_pic_placeholder)
            .placeholder(R.drawable.ic_profile_pic_placeholder).into(binding.agentImage)




        Log.e("Tag1234", "data.property_type" + data.post_type)
        if (data.post_type == "request") {
            binding.ivPropertySlider.visibility = View.GONE
            binding.clBackBar.visibility = View.VISIBLE
            binding.btnFav.visibility = View.GONE

            binding.agentName.text = data.user_name
            binding.agentCompany.text = data.user_company










        } else {
            binding.ivPropertySlider.visibility = View.VISIBLE
            binding.clBackBar.visibility = View.GONE

            saleAndResidents(data)
            binding.agentName.text = data.user_name
            binding.agentCompany.text = data.user_company


            Glide.with(this).load(data.user_picture).fallback(R.drawable.ic_profile_pic_placeholder)
                .placeholder(R.drawable.ic_profile_pic_placeholder).into(binding.agentImage)


            if (isFrom == "matches") {

                if (Const.userId == data.user_id) {
                    Log.e("Tag3","Sender Name "+ data.sender_name)
                    binding.headingAgent.text = "Requester Info"
                    binding.agentName.text = data.sender_name
                    binding.agentCompany.text = data.sender_company
                    binding.agentExperience.text = data.sender_experience + " years"
                    binding.agentSpecialty.text = data.sender_speciality

                    Glide.with(this).load(data.sender_image)
                        .fallback(R.drawable.ic_profile_pic_placeholder)
                        .placeholder(R.drawable.ic_profile_pic_placeholder).into(binding.agentImage)


                } else {
                    binding.headingAgent.text = "Agent Info"
                }



                binding.btnFav.visibility = View.GONE
                binding.groupBottomBar.visible()
            }



            binding.propertyCategory.text = data.property_type

            val price =
                if (data.sp.isNotEmpty()) addComma(data.sp) + " AED" else addComma(data.rented_for) + " AED/month"
            binding.propertyPrice.text = price
            binding.propertyName.text = data.property_title
            if(data.project.isEmpty()) {
                binding.propertyProject.gone()
            }
            binding.propertyProject.text = data.project
            binding.propertyLocation.text = data.area_community

            val bedrooms = if (data.bedrooms.equals(
                    "Studio",
                    true
                )
            ) data.bedrooms else data.bedrooms + " Bedrooms"
            binding.propertyRooms.text = bedrooms
            binding.propertyWashrooms.text = data.bathrooms + " Bathrooms"
            binding.propertyArea.text = data.area_size + "sqft"
            binding.propertyLounge.text = data.furnishing

            val description = data.property_description.ifEmpty { "N/A" }
            binding.propertyDescription.text = description


            var specialty = ""
            data.user_specialty?.forEachIndexed { index, spec ->
                specialty = if (index == data.user_specialty!!.size - 1) spec else "$spec, "
            }
            binding.agentSpecialty.text = specialty

            binding.tvListed.text = DateHelper.getDateTimeFromTimestamp(data.created_date)
            binding.tvReference.text = data.uid


            val imageSliderList = ArrayList<SlideModel>()
            data.property_images?.forEach { image ->
                imageSliderList.add(SlideModel(image, ScaleTypes.CENTER_INSIDE))
            }
            binding.ivPropertySlider.setImageList(imageSliderList)


            binding.ivPropertySlider.setItemClickListener(object : ItemClickListener {
                override fun onItemSelected(position: Int) {
                    // You can listen here.
                    Log.e("Tag23223","CLick ivPropertySlider")
                    findNavController().navigate(
                        PropertyDetailFragmentDirections.actionPropertyDetailFragmentToFullViewImageFragment(
                            propertyData
                        )
                    )
                }
                override fun doubleClick(position: Int) {
                    // Do not use onItemSelected if you are using a double click listener at the same time.
                    // Its just added for specific cases.
                    // Listen for clicks under 250 milliseconds.
                } })

//            setFeaturesList(data)







        }

        if(data.post_type=="request"){
            binding.groupBottomBar.visible()
            buyAndResidence(data)
            if (data.purpose == "Sale" && data.purpose_type == "Residential") {
                saleAndResidenceRequest(data)
            }
            else if(data.purpose == "Sale" && data.purpose_type == "Commercial"){
                saleAndCommercialRequest(data)
            }
            else if (data.purpose == "Rent" && data.purpose_type == "Residential") {
                rentAndResidentialRequest(data)
            }
            else if(data.purpose == "Rent" && data.purpose_type == "Commercial"){
                rentAndCommercialRequest(data)
            }

        }else {
            if (data.purpose == "Sale" && data.purpose_type == "Residential") {
                if(data.property_type=="Land"){
                    forLand(data)
                }else{
                    saleAndResidence(data)
                }

            }
            else if (data.purpose == "Rent" && data.purpose_type == "Residential") {
                if(data.property_type=="Land"){
                    forLand(data)
                }else{
                    rentAndResidence(data)
                }

            }
            else if(data.purpose == "Sale" && data.purpose_type == "Commercial"){
                saleAndCommercial(data)
            }

            else if(data.purpose == "Rent" && data.purpose_type == "Commercial"){
                rentAndCommercial(data)
            }
        }



        progressDialog.progressBarVisibility(false)
    }

    private fun setFeaturesList(data: PropertyData) {

        propertyFeaturesList.add(data.purpose)
        propertyFeaturesList.add(data.purpose_type)
        propertyFeaturesList.add(data.occupancy)
        propertyFeaturesList.add(data.development_status)
        if (data.balcony) {
            propertyFeaturesList.add("Balcony")
        }
        if (data.maidroom) {
            propertyFeaturesList.add("Maid Room")
        }
        propertyFeaturesList.add("Floor: " + data.floor)
        propertyFeaturesList.add(data.negotiation)

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()

    }


    private fun checkFavouritePostStatus(data: PropertyData) {

        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("favourites").document(cUser.uid).collection("posts")
                .document(data.uid)

            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.data


                    if (snapshot != null && snapshot.exists()) {
                        favouriteStatus = true
                        binding.btnFav.setImageResource(R.drawable.aa_ic_selected_favourite_icon)
                        binding.btnFav.setPadding(10, 30, 10, 10)
                    } else {
                        favouriteStatus = false
                        binding.btnFav.setImageResource(R.drawable.ic_heart)
                        binding.btnFav.setPadding(30, 30, 30, 30)
                    }

                }
            }
        }

    }

    private fun addFavourites(data: PropertyData) {
        progressDialog.progressBarVisibility(true)
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {

            val collectionReference =
                db.collection("favourites").document(currentUser.uid).collection("posts")
                    .document(data.uid)
            data.created_date = System.currentTimeMillis().toString()
            collectionReference.set(data)
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e(TAG, "Uplaod")
                    progressDialog.progressBarVisibility(false)
                    favouriteStatus = true
                    binding.btnFav.setImageResource(R.drawable.aa_ic_selected_favourite_icon)
                    binding.btnFav.setPadding(10, 30, 10, 10)
                    requireContext().showToast("Property successfully added to favorites.")

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e(TAG, "Fail$e")
                }
        }


    }

    private fun unFavourites(data: PropertyData) {

        progressDialog.progressBarVisibility(true)
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {

            val collectionReference =
                db.collection("favourites").document(currentUser.uid).collection("posts")
                    .document(data.uid)
            data.created_date = System.currentTimeMillis().toString()
            collectionReference.delete()
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e(TAG, "Delete")
                    progressDialog.progressBarVisibility(false)
                    favouriteStatus = false
                    binding.btnFav.setImageResource(R.drawable.ic_heart)
                    binding.btnFav.setPadding(20, 20, 20, 20)

                    requireContext().showToast("Property successfully removed from favorites.")

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e(TAG, "Fail$e")
                }
        }

    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    private fun whatsapp(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/$phoneNumber")
        startActivity(intent)
        // Verify if WhatsApp is installed on the device

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_fav -> {
                if (favouriteStatus) {
                    unFavourites(propertyData!!)
                } else {
                    addFavourites(propertyData!!)
                }
            }

            R.id.btn_phone -> {
                if (Const.userId == propertyData!!.user_id) {
                    makePhoneCall(propertyData!!.sender_phone)
                } else {
                    makePhoneCall(propertyData!!.user_phone)
                }
            }

            R.id.btn_whatsapp -> {
                if (Const.userId == propertyData!!.user_id) {
                    whatsapp(propertyData!!.sender_whatsapp)
                } else {
                    whatsapp(propertyData!!.sender_whatsapp)
                }
            }

            R.id.btn_chat -> {
                if (Const.userId == propertyData!!.user_id) {
                    findNavController().navigate(
                        PropertyDetailFragmentDirections.actionPropertyDetailFragmentToChatFragment2(
                            propertyData!!,false,
                            propertyData!!.sender_id.toString(), propertyData!!.sender_name!!, propertyData!!.sender_image!!
                        )
                    )
                }else {
                    findNavController().navigate(
                        PropertyDetailFragmentDirections.actionPropertyDetailFragmentToChatFragment2(
                            propertyData!!,false,
                            propertyData!!.user_id.toString(), propertyData!!.user_name!!, propertyData!!.user_picture!!
                        )
                    )
                }



            }

            R.id.agent_info_btn -> {

                Log.e("Tag1234", "Clikc ")
                findNavController().navigate(
                    PropertyDetailFragmentDirections.actionPropertyDetailFragmentToAgentProfileFragment(
                        propertyData
                    )
                )

            }
            R.id.iv_property_slider->{
                Log.e("Tag213","iv_property_slider ")


            }


        }
    }


    fun saleAndResidentsHideData() {

    }

    private fun rentAndResidentsHideData() {
        binding.floorStatus.visibility = View.GONE
        binding.floorDot.visibility = View.GONE

        binding.yearHandoverStatus.visibility = View.GONE
        binding.yearHandoverDot.visibility = View.GONE

        binding.yearHandoverStatus.visibility = View.GONE
        binding.yearHandoverDot.visibility = View.GONE

        binding.occopancyStatus.visibility = View.GONE
        binding.occopancyDot.visibility = View.GONE

        binding.propertyStatusDot.visibility = View.GONE
        binding.propertyStatus.visibility = View.GONE

        binding.serviceChargeStatus.visibility = View.GONE
        binding.serviceChargeDot.visibility = View.GONE


        binding.checksDot.visibility = View.VISIBLE
        binding.checksStatus.visibility = View.VISIBLE


    }

    private fun rentAndCommercialHideData() {
        binding.floorStatus.visibility = View.GONE
        binding.floorDot.visibility = View.GONE

        binding.yearHandoverStatus.visibility = View.GONE
        binding.yearHandoverDot.visibility = View.GONE


        binding.icBed.visibility = View.GONE
        binding.propertyRooms.visibility = View.GONE

        binding.icWashroom.visibility = View.GONE
        binding.propertyWashrooms.visibility = View.GONE



        binding.maidRoomStatus.visibility = View.GONE
        binding.maidRoomDot.visibility = View.GONE


        binding.balconyStatus.visibility = View.GONE
        binding.balconyRoomDot.visibility = View.GONE



        binding.occopancyStatus.visibility = View.GONE
        binding.occopancyDot.visibility = View.GONE

        binding.propertyStatusDot.visibility = View.GONE
        binding.propertyStatus.visibility = View.GONE


        binding.serviceChargeStatus.visibility = View.VISIBLE
        binding.serviceChargeDot.visibility = View.VISIBLE


        binding.checksDot.visibility = View.VISIBLE
        binding.checksStatus.visibility = View.VISIBLE


    }

    fun saleAndResidents(data: PropertyData) {


        binding.propertyStatus.text = "Property Status : " + data.development_status
        if (data.maidroom) {
            binding.maidRoomStatus.text = "Maid Room : Yes"
        } else {
            binding.maidRoomStatus.text = "Maid Room : No"
        }

        if (data.balcony) {
            binding.balconyStatus.text = "Balcony : Yes"
        } else {
            binding.balconyStatus.text = "Balcony: No"
        }
        if (data.occupancy == "Rented") {
            binding.occopancyStatus.text = "Occupancy : " + data.occupancy
            binding.checksStatus.visibility = View.VISIBLE
            binding.checksDot.visibility = View.VISIBLE
            binding.checksStatus.text = "Number of checks : " + data.number_of_checks
        } else {
            binding.checksStatus.visibility = View.GONE
            binding.checksDot.visibility = View.GONE
            binding.occopancyStatus.text = "Occupancy : " + data.occupancy

        }

        binding.serviceChargeStatus.text = "Service Charges : " + data.service_charge + "(Aed/Sqft)"
        binding.floorStatus.text = "Floor : " + data.floor
        binding.yearHandoverStatus.text = "Year of handover : " + data.handover_year
        binding.negotiationStatus.text = "Negotiation : " + data.negotiation
        binding.commissionStatus.text = "Commission : " + data.commission



        if (data.purpose == "Sale" && data.purpose_type == "Residential") {
//            saleAndResidentsHideData()
        } else if (data.purpose == "Rent" && data.purpose_type == "Residential") {
//            rentAndResidentsHideData()
            binding.checksStatus.text = "Number of checks : " + data.number_of_checks
        } else if (data.purpose == "Rent" && data.purpose_type == "Commercial") {
//            rentAndCommercialHideData()
            Log.e("Tag123", "number_of_checks" + data.number_of_checks)
            Log.e("Tag123", "Fitting" + data.fitting)
            binding.checksStatus.text = "Number of checks : " + data.number_of_checks
            binding.serviceChargeStatus.text = "Fitting : " + data.fitting
        }
    }


    private fun saleAndResidence(data: PropertyData){
        propertyFeaturesList.clear()

        propertyFeaturesList.add("Property Status : ${data.development_status}")
        if(data!!.maidroom){
            propertyFeaturesList.add("Maid Room")
        }
        if(data!!.balcony){
            propertyFeaturesList.add("Balcony Room")
        }

        if (data!!.occupancy == "Rented") {
            propertyFeaturesList.add("Occupancy : " + data.occupancy)
            propertyFeaturesList.add( "Number of checks : " + data.number_of_checks)
        }else{
            propertyFeaturesList.add("Occupancy : " + data.occupancy)
        }





        propertyFeaturesList.add("Service Charges : " + data.service_charge + "(Aed/Sqft)")
        propertyFeaturesList.add("Floor : " + data.floor)
        propertyFeaturesList.add("Year of handover : " + data.handover_year)
        propertyFeaturesList.add("Negotiation : " + data.negotiation)
        propertyFeaturesList.add("Commission : " + data.commission)



        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }





    private fun saleAndResidenceRequest(data: PropertyData){
        propertyFeaturesList.clear()

        if(data.maidroom){
            propertyFeaturesList.add("Maid Room")
        }
        if(data.balcony){
            propertyFeaturesList.add("Balcony Room")
        }
        propertyFeaturesList.add("Occupancy : " + data.occupancy)
        propertyFeaturesList.add("Purchase Goal : " + data.purchase_goal )
        propertyFeaturesList.add("Payment Method : " + data.payment_method)

        propertyFeaturesList.add( "Development : " + data.reqDevelopmentStatus)

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }


    private fun saleAndCommercialRequest(data: PropertyData){
        propertyFeaturesList.clear()

        propertyFeaturesList.add("Occupancy : " + data.occupancy)
        propertyFeaturesList.add("Fitting : "+data.fitting)
        propertyFeaturesList.add("Purchase Goal : " + data.purchase_goal )
        propertyFeaturesList.add("Payment Method : " + data.payment_method)



        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }

    private fun rentAndResidence(data: PropertyData){
        propertyFeaturesList.clear()


        if(data.maidroom){
            propertyFeaturesList.add("Maid Room")
        }
        if(data.balcony){
            propertyFeaturesList.add("Balcony Room")
        }

        propertyFeaturesList.add( "Number of checks : " + data.number_of_checks)

        propertyFeaturesList.add("Negotiation : " + data.negotiation)
        propertyFeaturesList.add("Commission : " + data.commission)



        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }

    private fun saleAndCommercial(data: PropertyData){
        propertyFeaturesList.clear()

        propertyFeaturesList.add("Property Status : ${data.development_status}")
        if(data!!.maidroom){
            propertyFeaturesList.add("Maid Room")
        }
        if(data!!.balcony){
            propertyFeaturesList.add("Balcony Room")
        }

        if (propertyData!!.occupancy == "Rented") {
            propertyFeaturesList.add("Occupancy : " + data.occupancy)
            propertyFeaturesList.add( "Number of checks : " + data.number_of_checks)
        }else{
            propertyFeaturesList.add("Occupancy : " + data.occupancy)
        }





        propertyFeaturesList.add("Service Charges : " + data.service_charge + "(Aed/Sqft)")
        propertyFeaturesList.add("Floor : " + data.floor)
        propertyFeaturesList.add("Year of handover : " + data.handover_year)
        propertyFeaturesList.add("Negotiation : " + data.negotiation)
        propertyFeaturesList.add("Commission : " + data.commission)



        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }
    private fun rentAndCommercial(data: PropertyData){
        propertyFeaturesList.clear()



        propertyFeaturesList.add( "Number of checks : " + data.number_of_checks)
        propertyFeaturesList.add("Fitting : "+data.fitting)
        propertyFeaturesList.add("Negotiation : " + data.negotiation)
        propertyFeaturesList.add("Commission : " + data.commission)



        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }
    private fun rentAndCommercialRequest(data: PropertyData){
        propertyFeaturesList.clear()

        if(data!!.maidroom){
            propertyFeaturesList.add("Maid Room")
        }
        if(data!!.balcony){
            propertyFeaturesList.add("Balcony Room")
        }



        propertyFeaturesList.add("Occupancy : " + data.occupancy)
        propertyFeaturesList.add("Service Charges : " + data.service_charge + "(Aed/Sqft)")
        propertyFeaturesList.add("Floor : " + data.floor)



        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun rentAndResidentialRequest(data: PropertyData){
        propertyFeaturesList.clear()


        if(data.maidroom){
            propertyFeaturesList.add("Maid Room")
        }
        if(data.balcony){
            propertyFeaturesList.add("Balcony Room")
        }

        propertyFeaturesList.add("Furniture : "+data.furnishing)
        propertyFeaturesList.add( "Number of checks : " + data.number_of_checks)
        propertyFeaturesList.add( "Moving Time : " + data.property_moving_time)






        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()


    }

    private fun forLand(data: PropertyData){


        binding.icBed.visibility=View.GONE
        binding.propertyRooms.visibility=View.GONE
        binding.icWashroom.visibility=View.GONE
        binding.propertyWashrooms.visibility=View.GONE
        binding.icLounge.visibility=View.GONE
        binding.propertyLounge.visibility=View.GONE





        propertyFeaturesList.add("Ownership : "+data.ownership)
        propertyFeaturesList.add("GFA : "+data.gfa)
        propertyFeaturesList.add("Far : "+data.far)
        propertyFeaturesList.add("Use : "+data.use)
        if(data.height=="G+"){
            propertyFeaturesList.add("G+ : "+data.g_value)
        }
        propertyFeaturesList.add("Height : "+data.height)




        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        binding.rvFeatures.layoutManager = layoutManager
        binding.rvFeatures.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}