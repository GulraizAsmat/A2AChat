package app.unduit.a2achatapp.fragments

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
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.PropertyFeaturesAdapter
import app.unduit.a2achatapp.databinding.FragmentPropertyDetailBinding
import app.unduit.a2achatapp.helpers.Const

import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PropertyDetailFragment : Fragment(),View.OnClickListener {

    var TAG="PropertyDetailFragment"

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentPropertyDetailBinding
    private val args: PropertyDetailFragmentArgs by navArgs()

    private var propertyData: PropertyData? = null

    private var isFrom = ""
    private var favouriteStatus=false

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
            if(isFrom == "home"){
                requireActivity().onBackPressed()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        }

        getData()
        listeners()
    }
    private fun listeners(){
        binding.btnFav.setOnClickListener(this)
        binding.btnPhone.setOnClickListener(this)
        binding.btnChat.setOnClickListener(this)
        binding.btnWhatsapp.setOnClickListener(this)
    }

    private fun getData() {
        progressDialog.progressBarVisibility(true)

        propertyData?.let { pData ->
            val db = Firebase.firestore
            val ref = db.collection("properties").document(pData.uid)

            if(Const.PropertyType=="match"){
                setData(pData)
            }else {
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


    private fun setData(data: PropertyData) {


        binding.agentName.text = data.user_name
        binding.agentCompany.text = data.user_company
        binding.agentExperience.text = data.user_experience + " years"

        Glide.with(this).load(data.user_picture).fallback(R.drawable.ic_profile_pic_placeholder)
            .placeholder(R.drawable.ic_profile_pic_placeholder).into(binding.agentImage)


        if(isFrom == "matches") {

            if(Const.userId==data.user_id){

                binding.headingAgent.text="Requester Info"
                binding.agentName.text = data.sender_name
                binding.agentCompany.text =data.sender_company
                binding.agentExperience.text = data.sender_experience + " years"
                binding.agentSpecialty.text = data.sender_speciality

                Glide.with(this).load(data.sender_image).fallback(R.drawable.ic_profile_pic_placeholder)
                    .placeholder(R.drawable.ic_profile_pic_placeholder).into(binding.agentImage)




            }
            else {
                binding.headingAgent.text="Agent Info"
            }



            binding.btnFav.visibility=View.GONE
            binding.groupBottomBar.visible()
        }



        binding.propertyCategory.text = data.property_type

        val price = if(data.sp.isNotEmpty()) data.sp + " AED" else data.rented_for + " AED/month"
        binding.propertyPrice.text = price
        binding.propertyName.text = data.property_title + ", " + data.project
        binding.propertyLocation.text = data.area_community

        val bedrooms = if(data.bedrooms.equals("Studio", true)) data.bedrooms else data.bedrooms + " Bedrooms"
        binding.propertyRooms.text = bedrooms
        binding.propertyWashrooms.text = data.bathrooms + " Bathrooms"
        binding.propertyArea.text = data.area_size + "sqft"
        binding.propertyLounge.text = data.furnishing

        val description = data.property_description.ifEmpty { "N/A" }
        binding.propertyDescription.text = description



        var specialty = ""
        data.user_specialty?.forEachIndexed { index, spec ->
            specialty = if(index == data.user_specialty!!.size - 1) spec else "$spec, "
        }
        binding.agentSpecialty.text = specialty

        binding.tvListed.text = DateHelper.getDateTimeFromTimestamp(data.created_date)
        binding.tvReference.text = data.uid


        val imageSliderList = ArrayList<SlideModel>()
        data.property_images?.forEach { image ->
            imageSliderList.add(SlideModel(image, ScaleTypes.CENTER_INSIDE))
        }
        binding.ivPropertySlider.setImageList(imageSliderList)

        setFeaturesList(data)

        progressDialog.progressBarVisibility(false)
    }

    private fun setFeaturesList(data: PropertyData) {

        propertyFeaturesList.add(data.purpose)
        propertyFeaturesList.add(data.purpose_type)
        propertyFeaturesList.add(data.occupancy)
        propertyFeaturesList.add(data.development_status)
        if(data.balcony) {
            propertyFeaturesList.add("Balcony")
        }
        if(data.maidroom) {
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



    private fun checkFavouritePostStatus(data: PropertyData){

        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("favourites").document(cUser.uid).collection("posts").document(data.uid)

            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.data


                    if (snapshot != null && snapshot.exists()) {
                        favouriteStatus=true
                        binding.btnFav.setImageResource(R.drawable.aa_ic_selected_favourite_icon)
                        binding.btnFav.setPadding(10,30,10,10)
                    } else {
                        favouriteStatus=false
                        binding.btnFav.setImageResource(R.drawable.ic_heart)
                        binding.btnFav.setPadding(30,30,30,30)
                    }

                }
            }
        }

    }

    private fun addFavourites(data: PropertyData){
        progressDialog.progressBarVisibility(true)
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {

            val collectionReference = db.collection("favourites").document(currentUser.uid).collection("posts").document(data.uid)
            data.created_date=System.currentTimeMillis().toString()
            collectionReference.set(data)
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e(TAG,"Uplaod")
                    progressDialog.progressBarVisibility(false)
                    favouriteStatus=true
                    binding.btnFav.setImageResource(R.drawable.aa_ic_selected_favourite_icon)
                    binding.btnFav.setPadding(10,30,10,10)
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

    private fun unFavourites(data: PropertyData){

        progressDialog.progressBarVisibility(true)
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {

            val collectionReference = db.collection("favourites").document(currentUser.uid).collection("posts").document(data.uid)
            data.created_date=System.currentTimeMillis().toString()
            collectionReference.delete()
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e(TAG,"Delete")
                    progressDialog.progressBarVisibility(false)
                    favouriteStatus=false
                    binding.btnFav.setImageResource(R.drawable.ic_heart)
                    binding.btnFav.setPadding(20,20,20,20)

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

    private fun whatsapp(phoneNumber: String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/$phoneNumber")
        startActivity(intent)
        // Verify if WhatsApp is installed on the device

    }

    override fun onClick(v: View?) {
      when(v!!.id){
          R.id.btn_fav->{
                if(favouriteStatus){
                    unFavourites(propertyData!!)
                }else {
                    addFavourites(propertyData!!)
                }
          }
          R.id.btn_phone->{
              if(Const.userId== propertyData!!.user_id){
                  makePhoneCall(propertyData!!.sender_phone)
              }
              else {
                  makePhoneCall(propertyData!!.user_phone)
              }
          }
          R.id.btn_whatsapp->{
              if(Const.userId== propertyData!!.user_id){
                  whatsapp(propertyData!!.sender_whatsapp)
              }
              else {
                  whatsapp(propertyData!!.sender_whatsapp)
              }
          }
          R.id.btn_chat->{
              Toast.makeText(requireContext(),"Under Development",Toast.LENGTH_LONG).show()
          }



      }
    }
}