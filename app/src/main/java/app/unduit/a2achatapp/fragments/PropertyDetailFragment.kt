package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.PropertyFeaturesAdapter
import app.unduit.a2achatapp.databinding.FragmentPropertyDetailBinding
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PropertyDetailFragment : Fragment() {

    private lateinit var binding: FragmentPropertyDetailBinding
    private val args: PropertyDetailFragmentArgs by navArgs()

    private var propertyData: PropertyData? = null

    private var isFrom = ""

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
    }

    private fun getData() {
        progressDialog.progressBarVisibility(true)

        propertyData?.let { pData ->
            val db = Firebase.firestore
            val ref = db.collection("properties").document(pData.uid)

            ref.get().addOnSuccessListener { snapshot ->

                snapshot?.let { snap ->
                    val data = snap.toObject(PropertyData::class.java)

                    data?.let {
                        setData(it)
                    } ?: run {
                        setData(pData)
                    }
                }

            }.addOnFailureListener {
                setData(pData)
            }
        }
    }

    private fun setData(data: PropertyData) {

        if(isFrom == "matches") {
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

        binding.agentName.text = data.user_name
        binding.agentCompany.text = data.user_company
        binding.agentExperience.text = data.user_experience + " years"

        var specialty = ""
        data.user_specialty?.forEachIndexed { index, spec ->
            specialty = if(index == data.user_specialty!!.size - 1) spec else "$spec, "
        }
        binding.agentSpecialty.text = specialty

        binding.tvListed.text = DateHelper.getDateTimeFromTimestamp(data.created_date)
        binding.tvReference.text = data.uid

        Glide.with(this).load(data.user_picture).fallback(R.drawable.ic_profile_pic_placeholder)
            .placeholder(R.drawable.ic_profile_pic_placeholder).into(binding.agentImage)

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
}