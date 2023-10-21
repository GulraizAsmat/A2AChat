package app.unduit.a2achatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.Application
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.BathroomItemFilterAdapter
import app.unduit.a2achatapp.adapters.BedroomItemFilterAdapter
import app.unduit.a2achatapp.adapters.CustomPriceSpinnerAdapter
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.adapters.MinPriceFilterAdapter
import app.unduit.a2achatapp.adapters.PropertyFiltersAdapter
import app.unduit.a2achatapp.adapters.PropertyListMultipleViewAdapter
import app.unduit.a2achatapp.adapters.PropertyTypeFilterAdapter
import app.unduit.a2achatapp.databinding.FragmentPropertyListBinding

import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.helpers.addComma
import app.unduit.a2achatapp.helpers.removeComma
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType
import app.unduit.a2achatapp.models.PropertyData
import app.unduit.a2achatapp.models.PropertyType
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class PropertyListFragment : Fragment(), AdapterListener {

    private val TAG = "PropertyListFragment"

    private lateinit var binding: FragmentPropertyListBinding
    var previousPosition = 0
    var propertyType = ""
    var selectedPostType="All"
    var bathRoomPreviousPosition = 0
    private var bedRoomPreviousPosition = 0
    private var minPricePreviousPosition = 0
    private var maxPricePreviousPosition = 0
    var viewOnlyCommercialProperty = false
    var viewOnlyResidenacialProperty = false
    var bedroomStr = ""
    var bathroomStr = ""
    var selectedPaymentPurpose=0   //all=0, buy=1, rent=2
    private lateinit var propertyTypeDialog: BottomSheetDialog

    private var selectedCategory = "All"
    private var selectedPropertyType = ""
    private var selectedBath = ""
    private var selectedBed = ""
    private var selectedMinPrice = "No min."
    private var selectedMaxPrice = "No max."

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    private lateinit var auth: FirebaseAuth

    private var propertyList = ArrayList<PropertyData>()
    private var propertyListFiltered = ArrayList<PropertyData>()
    var propertyFilterList = ArrayList<PropertyType>()
    var propertyItemList = ArrayList<PropertyType>()
    private var bedRoomList = ArrayList<BathBedType>()
    var bathRoomList = ArrayList<BathBedType>()
    var minPriceList = ArrayList<String>()
    var maxPriceList = ArrayList<String>()

    private val adapter by lazy {
        PropertyListMultipleViewAdapter(
            requireContext(),
            this,
            propertyListFiltered
        )
    }


    private val propertyFiltersAdapter by lazy {
        PropertyFiltersAdapter(
            requireContext(),
            this,
            propertyFilterList
        )
    }

    private lateinit var propertyTypeAdapter: PropertyTypeFilterAdapter




    private lateinit var bedRoomAdapter: BedroomItemFilterAdapter
    private lateinit var bathRoomAdapter : BathroomItemFilterAdapter

    private val minPriceAdapter by lazy {
        MinPriceFilterAdapter(
            requireContext(),
            this,
            bathRoomList
        )
    }

    override fun onResume() {
        super.onResume()
        Const.screenName== "property_list"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPropertyListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {



        Const.tempValue = false
        setListeners()
        setRV()
        getExceptedData()
        loadUserProfileImage()
        propertyFilterRecyclerViewManger()
        filtersListData()


    }


    private fun filtersListData() {

        propertyFilterList.clear()
        propertyFilterList.add(PropertyType(name = "All", selected = false))
        propertyFilterList.add(PropertyType(name = "Category", selected = false))
        propertyFilterList.add(PropertyType(name = "Property Type", selected = false))
        propertyFilterList.add(PropertyType(name = "Price", selected = false))
        propertyFilterList.add(PropertyType(name = "Beds & Baths", selected = false))
//        propertyFilterList.add(PropertyType(name ="Amenitites"))

        propertyFiltersAdapter.notifyDataSetChanged()


    }

    private fun bedRoomData() {
        bedRoomPreviousPosition = 0
        bedRoomList.clear()
        bedRoomList.add(BathBedType("Studio", false))
        bedRoomList.add(BathBedType("1", false))
        bedRoomList.add(BathBedType("2", false))
        bedRoomList.add(BathBedType("3", false))
        bedRoomList.add(BathBedType("4", false))
        bedRoomList.add(BathBedType("5", false))
        bedRoomList.add(BathBedType("6", false))
        bedRoomList.add(BathBedType("7", false))
        bedRoomList.add(BathBedType("8+", false))

    }

    private fun priceDataForRent() {
        minPricePreviousPosition = 0
        maxPricePreviousPosition = 0
        minPriceList.clear()
        maxPriceList.clear()
        minPriceList.add("No min.")
        minPriceList.add("20,000")
        minPriceList.add("30,000")
        minPriceList.add("40,000")
        minPriceList.add("50,000")
        minPriceList.add("60,000")
        minPriceList.add("70,000")
        minPriceList.add("80,000")
        minPriceList.add("90,000")
        minPriceList.add("100,000")
        minPriceList.add("100,000")
        minPriceList.add("150,000")
        minPriceList.add("200,000")
        minPriceList.add("250,000")
        minPriceList.add("300,000")
        minPriceList.add("350,000")
        minPriceList.add("400,000")
        minPriceList.add("450,000")
        minPriceList.add("500,000")
        minPriceList.add("550,000")
        minPriceList.add("600,000")
        minPriceList.add("650,000")
        minPriceList.add("700,000")
        minPriceList.add("750,000")
        minPriceList.add("800,000")
        minPriceList.add("850,000")
        minPriceList.add("900,000")
        minPriceList.add("950,000")
        minPriceList.add("1000,000")
        minPriceList.add("1000,000+")


        maxPriceList.addAll(minPriceList)
        maxPriceList[0] = "No max."

    }
    private fun priceDataForBuy() {
        minPricePreviousPosition = 0
        maxPricePreviousPosition = 0
        minPriceList.clear()
        maxPriceList.clear()
        minPriceList.add("No min.")
        minPriceList.add("300,000")
        minPriceList.add("400,000")
        minPriceList.add("500,000")
        minPriceList.add("600,000")
        minPriceList.add("700,000")
        minPriceList.add("800,000")
        minPriceList.add("900,000")
        minPriceList.add("1,000,000")
        minPriceList.add("1,100,000")
        minPriceList.add("1,200,000")
        minPriceList.add("1,300,000")
        minPriceList.add("1,400,000")
        minPriceList.add("1,500,000")
        minPriceList.add("1,600,000")
        minPriceList.add("1,700,000")
        minPriceList.add("1,800,000")
        minPriceList.add("1,900,000")
        minPriceList.add("2,000,000")
        minPriceList.add("2,100,000")
        minPriceList.add("2,200,000")
        minPriceList.add("2,300,000")
        minPriceList.add("2,400,000")
        minPriceList.add("2,500,000")
        minPriceList.add("2,600,000")
        minPriceList.add("2,700,000")
        minPriceList.add("2,800,000")
        minPriceList.add("2,900,000")
        minPriceList.add("3,000,000")
        minPriceList.add("3,250,000")
        minPriceList.add("3,500,000")
        minPriceList.add("3,750,000")
        minPriceList.add("4,000,000")
        minPriceList.add("4,250,000")
        minPriceList.add("4,500,000")
        minPriceList.add("5,000,000")
        minPriceList.add("6,000,000")
        minPriceList.add("7,000,000")
        minPriceList.add("8,000,000")
        minPriceList.add("9,000,000")
        minPriceList.add("10,000,000")
        minPriceList.add("25,000,000")
        minPriceList.add("50,000,000")



        maxPriceList.addAll(minPriceList)
        maxPriceList[0] = "No max."

    }
    private fun priceDataForAll() {
        minPricePreviousPosition = 0
        maxPricePreviousPosition = 0
        minPriceList.clear()
        maxPriceList.clear()
        minPriceList.add("No min.")
        minPriceList.add("20,000")
        minPriceList.add("30,000")
        minPriceList.add("40,000")
        minPriceList.add("50,000")
        minPriceList.add("60,000")
        minPriceList.add("70,000")
        minPriceList.add("80,000")
        minPriceList.add("90,000")
        minPriceList.add("100,000")
        minPriceList.add("100,000")
        minPriceList.add("150,000")
        minPriceList.add("200,000")
        minPriceList.add("250,000")
        minPriceList.add("300,000")
        minPriceList.add("350,000")
        minPriceList.add("400,000")
        minPriceList.add("450,000")
        minPriceList.add("500,000")
        minPriceList.add("550,000")
        minPriceList.add("600,000")
        minPriceList.add("650,000")
        minPriceList.add("700,000")
        minPriceList.add("750,000")
        minPriceList.add("800,000")
        minPriceList.add("850,000")
        minPriceList.add("900,000")
        minPriceList.add("950,000")
        minPriceList.add("1000,000")
        minPriceList.add("1,100,000")
        minPriceList.add("1,200,000")
        minPriceList.add("1,300,000")
        minPriceList.add("1,400,000")
        minPriceList.add("1,500,000")
        minPriceList.add("1,600,000")
        minPriceList.add("1,700,000")
        minPriceList.add("1,800,000")
        minPriceList.add("1,900,000")
        minPriceList.add("2,000,000")
        minPriceList.add("2,100,000")
        minPriceList.add("2,200,000")
        minPriceList.add("2,300,000")
        minPriceList.add("2,400,000")
        minPriceList.add("2,500,000")
        minPriceList.add("2,600,000")
        minPriceList.add("2,700,000")
        minPriceList.add("2,800,000")
        minPriceList.add("2,900,000")
        minPriceList.add("3,000,000")
        minPriceList.add("3,250,000")
        minPriceList.add("3,500,000")
        minPriceList.add("3,750,000")
        minPriceList.add("4,000,000")
        minPriceList.add("4,250,000")
        minPriceList.add("4,500,000")
        minPriceList.add("5,000,000")
        minPriceList.add("6,000,000")
        minPriceList.add("7,000,000")
        minPriceList.add("8,000,000")
        minPriceList.add("9,000,000")
        minPriceList.add("10,000,000")
        minPriceList.add("25,000,000")
        minPriceList.add("50,000,000")

        maxPriceList.addAll(minPriceList)
        maxPriceList[0] = "No max."

    }
    private fun bathRoomData() {
        bathRoomList.clear()
        bathRoomPreviousPosition = 0
        bathRoomList.add(BathBedType("1", false))
        bathRoomList.add(BathBedType("2", false))
        bathRoomList.add(BathBedType("3", false))
        bathRoomList.add(BathBedType("4", false))
        bathRoomList.add(BathBedType("5", false))
        bathRoomList.add(BathBedType("6", false))
        bathRoomList.add(BathBedType("7+", false))

    }


    @SuppressLint("SuspiciousIndentation")
    private fun setListeners() {
        binding.favouriteIcon.setOnClickListener {
            Const.screenName = "favourite_icon"
            findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToFavouriteFragment())
        }

        binding.notificationIcon.setOnClickListener {
            Const.screenName = "notification_icon"
            findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToNotificationFragment())
        }

        binding.profileImage.setOnClickListener {
            Const.screenName = "profile_image"
            findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToProfileFragment())
        }

        binding.btnChat.setOnClickListener {
            Const.screenName = ""
            Const.tempValue = true
                findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToChatFragment())
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }


    }

    private fun setRV() {
        binding.rvProperty.layoutManager =
            LinearLayoutManager(requireContext())
        binding.rvProperty.itemAnimator = DefaultItemAnimator()
        binding.rvProperty.adapter = adapter
    }

    private fun propertyFilterRecyclerViewManger() {
        binding.rvPropertyFilter.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPropertyFilter.itemAnimator = DefaultItemAnimator()
        binding.rvPropertyFilter.adapter = propertyFiltersAdapter
    }

    private fun getExceptedData(){

        lifecycleScope.launch {
            val database = Application.database


            if( database.exceptDataDao().getRowCount()!=0){
                val data = database.exceptDataDao().getAllEntities()

                val stringList =ArrayList<String>()

                data.forEach {
                    stringList.add(it.post_id)
                    Log.e("Tag12345","Data 12334"+it.post_id)
                }


                if(stringList.isEmpty()){


                }
                else {
                    Log.e("Tag21345","DB is  not Empty "+stringList.size)

                }

//                   loadHomeDataWithExcepted(stringList)



                getData(stringList)

            }else {
                Log.e("Tag21345","DB is Empty")

                val stringList =ArrayList<String>()
                getData(stringList)

            }




        }
    }
    private fun getData(list: ArrayList<String>) {
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("properties")
                .whereNotEqualTo("user_id", cUser.uid)

            ref.get()
                .addOnSuccessListener { documents ->
                    propertyList.clear()
                    propertyListFiltered.clear()
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertyList.add(document.toObject(PropertyData::class.java))
                    }



                    list.forEach {  local->
                        propertyList.apply {  forEachIndexed { index, main ->

                            if(main.uid==local){
                                Log.e("Raf","main.uid :: "+ main.uid +" local "+local +"propertylistTenp "+propertyList[index].uid  )

                                propertyList.removeAt(index)
                                return@apply

                            }

                        }
                        }
                    }

                    for (propertyData in propertyList) {
                        if (propertyData.sp.isEmpty()) {
                            // Update 'sp' with the value of 'rented_for'
                            propertyData.sp = propertyData.rented_for
                        }
                        if(propertyData.post_type=="request"){
                                if(propertyData.budget_min.isEmpty()){
                                    propertyData.budget_min = "0"
                                    propertyData.budget_max = "0"
                                }
                        }
                    }
                    val list = propertyList.sortedByDescending { it.created_date }.distinctBy { it.uid }




                    propertyList.clear()
                    propertyListFiltered.clear()
                    propertyList.addAll(list)

                    propertyListFiltered.addAll(propertyList)



                    val list3 = propertyList.filter {
                        it.purpose_type.equals("Residential", true)
                    }
                    val list4 = propertyList.filter {
                        it.purpose_type.equals("Commercial", true)
                    }




                    Log.e("poasp12","Residential  :: "+list3.size)
                    Log.e("poasp12","Commercial  :: "+list4.size)
                    Log.e("poasp12","propertyListFiltered  :: "+propertyList.size)
                    val list2 = propertyList.filter {
                        it.post_type.equals("request", true)
                    }

                    Log.e("Tag21213","Post Type request  :: "+list2.size)

                    val list1 = propertyList.filter {
                        it.post_type.equals("property", true)
                    }

                    Log.e("Tag21213","Post Type  Porpery:: "+list1.size)

                    adapter.notifyDataSetChanged()
                    Log.d(TAG, "propertyList size => ${propertyList.size}")
                    progressDialog.progressBarVisibility(false)
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }
    }

    private fun loadUserProfileImage() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("users").document(cUser.uid)
            Const.userId = cUser.uid
            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.data
                    try {
                        val image = data?.get("profile_image") as String
                        Glide.with(this).load(image)
                            .fallback(R.drawable.ic_deafult_profile_icon)
                            .placeholder(R.drawable.ic_deafult_profile_icon)
                            .into(binding.profileImage)

                    } catch (ex: Exception) {

                    }

                }
            }
        }

    }

    private fun showAllPropertyType() {
        previousPosition = 0
        propertyItemList.clear()

        propertyItemList.add(
            PropertyType(
                name = "Apartment",
                image = R.drawable.ic_apartment,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Villa",
                image = R.drawable.ic_villa,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Townhouse",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Penthouse",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Duplex",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Land",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Hotel Apartment",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Office",
                image = R.drawable.ic_office,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Shop",
                image = R.drawable.ic_warehouse,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Commercial Villa",
                image = R.drawable.ic_warehouse,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Warehouse",
                image = R.drawable.ic_shop,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Labour Camp",
                image = R.drawable.ic_labour_camp,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Staff Accommodation",
                image = R.drawable.ic_labour_camp,
                selected = false
            )
        )
    }

    private fun showResidentProperty() {
        previousPosition = 0
        propertyItemList.clear()

        propertyItemList.add(
            PropertyType(
                name = "Apartment",
                image = R.drawable.ic_apartment,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Villa",
                image = R.drawable.ic_villa,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Townhouse",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Penthouse",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Duplex",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Land",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Hotel Apartment",
                image = R.drawable.ic_penthouse,
                selected = false
            )
        )
    }

    private fun showCommercialProperty() {
        previousPosition = 0
        propertyItemList.clear()

        propertyItemList.add(
            PropertyType(
                name = "Office",
                image = R.drawable.ic_office,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Shop",
                image = R.drawable.ic_warehouse,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Commercial Villa",
                image = R.drawable.ic_warehouse,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Warehouse",
                image = R.drawable.ic_shop,
                selected = false
            )
        )
        propertyItemList.add(
            PropertyType(
                name = "Labour Camp",
                image = R.drawable.ic_labour_camp,
                selected = false
            )
        )

        propertyItemList.add(
            PropertyType(
                name = "Staff Accommodation",
                image = R.drawable.ic_labour_camp,
                selected = false
            )
        )

    }

    private fun bottomSheetForRent() {

        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_for_rent, null)

        val forAll = view.findViewById<ConstraintLayout>(R.id.cl_selector_all)
        val forRent = view.findViewById<ConstraintLayout>(R.id.cl_selector_1)
        val forSale = view.findViewById<ConstraintLayout>(R.id.cl_selector_2)
        val forRequest = view.findViewById<ConstraintLayout>(R.id.cl_selector_3)
        val allTitle = view.findViewById<TextView>(R.id.all_title)
        val rentTitle = view.findViewById<TextView>(R.id.rent_title)
        val buyTitle = view.findViewById<TextView>(R.id.buy_title)
        val requestTitle = view.findViewById<TextView>(R.id.request_title)
        val onlyCommercial = view.findViewById<AppCompatCheckBox>(R.id.cb_commercial)
        val onlyResidencial = view.findViewById<AppCompatCheckBox>(R.id.cb_residence)
        val showBtn = view.findViewById<AppCompatButton>(R.id.show_btn)

        onlyCommercial.isChecked = viewOnlyCommercialProperty
        onlyResidencial.isChecked = viewOnlyResidenacialProperty

        when (selectedCategory) {
            "All" -> {
                forAll.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
                forRent.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forSale.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)

                allTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_white_shade_1
                    )
                )
                rentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                buyTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                requestTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
            }

            "Rent" -> {
                forAll.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forRent.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
                forSale.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                allTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                rentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_white_shade_1
                    )
                )
                buyTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                requestTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
            }

            "Sale" -> {
                forAll.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forRent.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forSale.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
                forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                allTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                rentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                buyTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_white_shade_1
                    )
                )
                requestTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
            }

            "Request" -> {
                forAll.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forRent.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forSale.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
                forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
                allTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                rentTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                buyTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_black_shade_1
                    )
                )
                requestTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_white_shade_1
                    )
                )
            }
        }

        forAll.setOnClickListener {
            forAll.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            forRent.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forSale.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)

            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )
            rentTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            buyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )

            selectedCategory = "All"
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            showBtn.text = "Show ${propertyListFiltered.size} properties"
        }

        forRent.setOnClickListener {
            forAll.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forRent.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            forSale.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            rentTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )
            buyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )

            selectedCategory = "Rent"
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            showBtn.text = "Show ${propertyListFiltered.size} properties"
        }

        forSale.setOnClickListener {
            forAll.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forRent.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forSale.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            rentTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            buyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )

            selectedCategory = "Sale"
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            showBtn.text = "Show ${propertyListFiltered.size} properties"
        }

        forRequest.setOnClickListener {
            forAll.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forRent.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forSale.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forRequest.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            rentTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            buyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )

            selectedCategory = "Request"
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            showBtn.text = "Show ${propertyListFiltered.size} properties"
        }

        onlyCommercial.setOnClickListener {

            if(viewOnlyCommercialProperty){
                viewOnlyCommercialProperty = false
            }else {
                viewOnlyCommercialProperty = true
                viewOnlyResidenacialProperty = false
                onlyResidencial.isChecked=false
            }



            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            showBtn.text = "Show ${propertyListFiltered.size} properties"
        }

        onlyResidencial.setOnClickListener {

            if(viewOnlyResidenacialProperty){
                viewOnlyResidenacialProperty = false
            }else {
                onlyCommercial.isChecked=false
                viewOnlyResidenacialProperty = true
                viewOnlyCommercialProperty = false
            }



            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            showBtn.text = "Show ${propertyListFiltered.size} properties"
        }

        filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
        showBtn.text = "Show ${propertyListFiltered.size} properties"

        var filterApplied = false
        val prevSelected = selectedCategory
        showBtn.setOnClickListener {
            filterApplied = true
            adapter.notifyDataSetChanged()
            propertyTypeDialog.dismiss()

            var setSelection = selectedCategory != "All"
            var titleShow="Rent"
            if(selectedCategory=="Sale"){

                titleShow="Buy"
            }


            if(viewOnlyCommercialProperty){
                if(!setSelection){
                    titleShow="All"
                    setSelection=true
                }
                propertyFilterList[1] = PropertyType(name = "Commercial | $titleShow", selected = setSelection)
            }
            else if(viewOnlyResidenacialProperty){
                if(!setSelection){
                    titleShow="All"
                    setSelection=true
                }
                propertyFilterList[1] = PropertyType(name = "Residential | $titleShow", selected = setSelection)
            }
            else {
                if(!setSelection) {
                    titleShow = "All"
                }
                propertyFilterList[1] = PropertyType(name = titleShow, selected = setSelection)

            }

            propertyFiltersAdapter.notifyItemChanged(1)
        }

        propertyTypeDialog.setOnDismissListener {
            if (!filterApplied) {
                selectedCategory = prevSelected
            }
        }

        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()
    }


    private fun bottomSheetForPost(){
        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_for_post_type, null)
        val all = view.findViewById<ConstraintLayout>(R.id.cl_selector_all)
        val property = view.findViewById<ConstraintLayout>(R.id.cl_selector_1)
        val allTitle = view.findViewById<TextView>(R.id.all_title)
        val propertyTitle = view.findViewById<TextView>(R.id.rent_title)
        val requestTitle = view.findViewById<TextView>(R.id.request_title)
        val request = view.findViewById<ConstraintLayout>(R.id.cl_selector_3)
        val showBtn = view.findViewById<AppCompatButton>(R.id.show_btn)


        if( selectedPostType=="request"){
            request.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            property.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            all.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            propertyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )

            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )
        }else if( selectedPostType=="property") {
            property.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            request.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            all.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            propertyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )
        }
        else {
            all.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            request.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            property.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)


            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )

            propertyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )
        }



        property.setOnClickListener {


            selectedPostType="property"
            property.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            request.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            all.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            propertyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

            Log.e("Gul21","property"  +propertyListFiltered.size)


            showBtn.text = "Show ${propertyListFiltered.size} properties"



        }


        request.setOnClickListener {
            Log.e("Gul21","request"  )
            selectedPostType="request"
            request.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            property.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            all.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            propertyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )

            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )

            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

            Log.e("Gul21","request"  +propertyListFiltered.size)

            showBtn.text = "Show ${propertyListFiltered.size} properties"
        }

        all.setOnClickListener {
            all.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            request.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            property.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)


            requestTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )

            propertyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_black_shade_1
                )
            )
            allTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_white_shade_1
                )
            )

            selectedPostType="All"
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

            Log.e("filterCategory","property"  +propertyListFiltered.size)


            showBtn.text = "Show ${propertyListFiltered.size} properties"

        }


        showBtn.setOnClickListener {
//            filterApplied = true
            adapter.notifyDataSetChanged()
            propertyTypeDialog.dismiss()

            val setSelection = selectedPostType != "All"
            propertyFilterList[0] = PropertyType(name = selectedPostType.capitalize(), selected = setSelection)
            propertyFiltersAdapter.notifyItemChanged(0)
        }

        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()


    }

    private fun bottomSheetForPropertyType() {
        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_propertytype, null)

        val rvPropertyType = view.findViewById<RecyclerView>(R.id.rv_property_type)
        val showBtn = view.findViewById<AppCompatButton>(R.id.show_btn)
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap =
            FlexWrap.WRAP  // Items will wrap to the next line if there's not enough space
        layoutManager.flexDirection = FlexDirection.ROW
        rvPropertyType.layoutManager = layoutManager






        propertyTypeAdapter = PropertyTypeFilterAdapter(
            requireContext(),
            propertyItemList
        ) { position ->
            propertyItemList[previousPosition].selected = false

            if (!propertyItemList[position].selected) {
                propertyItemList[position].selected = true
                previousPosition = position
            }

            propertyType = propertyItemList[previousPosition].name

            selectedPropertyType = propertyType
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            showBtn.text = "Show ${propertyListFiltered.size} properties"

            propertyTypeAdapter.notifyDataSetChanged()

        }


//        propertyItemList.forEachIndexed { index, type ->
//            if (type.name.equals(propertyType, true)) {
//                propertyItemList[previousPosition].selected = false
//
//                if (!propertyItemList[index].selected) {
//                    propertyItemList[index].selected = true
//                    previousPosition = index
//                }
//
//                propertyType = propertyItemList[previousPosition].name
//
//                selectedPropertyType = propertyType
//                filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
//                showBtn.text = "Show ${propertyListFiltered.size} properties"
//
//                propertyTypeAdapter.notifyDataSetChanged()
//            }
//        }



//        propertyTypeAdapter.notifyDataSetChanged()
        var filterApplied = false
        val prevSelected = selectedPropertyType
        showBtn.setOnClickListener {
            filterApplied = true
            adapter.notifyDataSetChanged()
            propertyTypeDialog.dismiss()
            propertyFilterList[2] = PropertyType(name = selectedPropertyType, selected = true)
            propertyFiltersAdapter.notifyItemChanged(2)
        }

        propertyTypeDialog.setOnDismissListener {
            if (!filterApplied) selectedPropertyType = prevSelected
        }

        rvPropertyType.adapter = propertyTypeAdapter
        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()

    }


    private fun bottomSheetForPrice() {


        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_price, null)

        val rvMinPrice = view.findViewById<Spinner>(R.id.rv_min_price)
        val rvMaxPrice = view.findViewById<Spinner>(R.id.rv_max_price)
        val clMinPrice = view.findViewById<ConstraintLayout>(R.id.cl_min_price)
        val showBtn = view.findViewById<AppCompatButton>(R.id.show_btn)

//        filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
//        showBtn.text = "Show ${propertyListFiltered.size} properties"

        val adapterMin = CustomPriceSpinnerAdapter(requireContext(), minPriceList)

        rvMinPrice.adapter = adapterMin
        rvMinPrice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = minPriceList[position]

                    selectedMinPrice = selectedItem
                    filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
                    showBtn.text = "Show ${propertyListFiltered.size} properties"
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }

        minPriceList.forEachIndexed { index, item ->
            if(item.equals(selectedMinPrice, true)) {
                rvMinPrice.setSelection(index)
            }
        }


        val adapterMax = CustomPriceSpinnerAdapter(requireContext(), maxPriceList)

        rvMaxPrice.adapter = adapterMax
        rvMaxPrice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = maxPriceList[position]

                    selectedMaxPrice = selectedItem
                    filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
                    showBtn.text = "Show ${propertyListFiltered.size} properties"
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }

        maxPriceList.forEachIndexed { index, item ->
            if(item.equals(selectedMaxPrice, true)) {
                rvMaxPrice.setSelection(index)
            }
        }

        var filterApplied = false
        val prevSelectedMinPrice = selectedMinPrice
        val prevSelectedMaxPrice = selectedMaxPrice

        showBtn.setOnClickListener {
            filterApplied = true
            adapter.notifyDataSetChanged()
            propertyTypeDialog.dismiss()

            val text = "$selectedMinPrice - $selectedMaxPrice"
            propertyFilterList[3] = PropertyType(name = text, selected = true)
            propertyFiltersAdapter.notifyItemChanged(3)
        }

        propertyTypeDialog.setOnDismissListener {
            if (!filterApplied) {
                selectedMinPrice = prevSelectedMinPrice
                selectedMaxPrice = prevSelectedMaxPrice
            }
        }

        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()

    }


    private fun bottomSheetForBedAndBath() {


        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_for_bed_bath, null)

        val rvBed = view.findViewById<RecyclerView>(R.id.rv_beds)
        val rvBath = view.findViewById<RecyclerView>(R.id.rv_bath)
        val showBtn = view.findViewById<AppCompatButton>(R.id.show_btn)

        bedRoomAdapter = BedroomItemFilterAdapter(
            requireContext(),
            bedRoomList
        ) { position ->
            bedRoomList[bedRoomPreviousPosition].selected = false

            if (!bedRoomList[position].selected) {

                bedRoomList[position].selected = true
                bedRoomPreviousPosition = position
            }

            bedroomStr = bedRoomList[bedRoomPreviousPosition].name

            selectedBed = bedroomStr
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

            showBtn.text = "Show ${propertyListFiltered.size} properties"

            bedRoomAdapter.notifyDataSetChanged()
        }

        bathRoomAdapter = BathroomItemFilterAdapter(
            requireContext(),
            bathRoomList
        ) { position ->
            bathRoomList[bathRoomPreviousPosition].selected = false

            if (!bathRoomList[position].selected) {

                bathRoomList[position].selected = true
                bathRoomPreviousPosition = position
            }

            bathroomStr = bathRoomList[bathRoomPreviousPosition].name

            selectedBath = bathroomStr
            filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

            showBtn.text = "Show ${propertyListFiltered.size} properties"

            bathRoomAdapter.notifyDataSetChanged()
        }

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap =
            FlexWrap.WRAP  // Items will wrap to the next line if there's not enough space
        layoutManager.flexDirection = FlexDirection.ROW
        rvBed.layoutManager = layoutManager
        rvBed.adapter = bedRoomAdapter


        val layoutManagerBath = FlexboxLayoutManager(requireContext())
        layoutManagerBath.flexWrap =
            FlexWrap.WRAP  // Items will wrap to the next line if there's not enough space
        layoutManagerBath.flexDirection = FlexDirection.ROW
        rvBath.layoutManager = layoutManagerBath
        rvBath.adapter = bathRoomAdapter

        showBtn.text = "Show ${propertyListFiltered.size} properties"

        bedRoomList.forEachIndexed { index, type ->
            if (type.name.equals(selectedBed, true)) {
                bedRoomList[bedRoomPreviousPosition].selected = false

                if (!bedRoomList[index].selected) {

                    bedRoomList[index].selected = true
                    bedRoomPreviousPosition = index
                }

                bedroomStr = bedRoomList[bedRoomPreviousPosition].name

                selectedBed = bedroomStr
                filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

                showBtn.text = "Show ${propertyListFiltered.size} properties"

                bedRoomAdapter.notifyDataSetChanged()
            }
        }

        bathRoomList.forEachIndexed { index, type ->
            if (type.name.equals(selectedBath, true)) {
                bathRoomList[bathRoomPreviousPosition].selected = false

                if (!bathRoomList[index].selected) {

                    bathRoomList[index].selected = true
                    bathRoomPreviousPosition = index
                }

                bathroomStr = bathRoomList[bathRoomPreviousPosition].name

                selectedBath = bathroomStr
                filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

                showBtn.text = "Show ${propertyListFiltered.size} properties"

                bathRoomAdapter.notifyDataSetChanged()
            }
        }

        var filterApplied = false
        val prevSelectedBed = selectedBed
        val prevSelectedBath = selectedBath
        showBtn.setOnClickListener {
            filterApplied = true
            adapter.notifyDataSetChanged()
            propertyTypeDialog.dismiss()

            var text = ""

            text = if(selectedBath == "") {
                if(selectedBed == "Studio") {
                    "Studio"
                } else {
                    "$selectedBed Beds"
                }
            } else if(selectedBed == "") {
                "$selectedBath Baths"
            } else {
                if(selectedBed == "Studio") {
                    "Studio & $selectedBath Baths"
                } else {
                    "$selectedBed Beds & $selectedBath Baths"
                }
            }

            propertyFilterList[4] = PropertyType(name = text, selected = true)
            propertyFiltersAdapter.notifyItemChanged(4)
        }

        propertyTypeDialog.setOnDismissListener {
            if (!filterApplied) {
                selectedBed = prevSelectedBed
                selectedBath = prevSelectedBath
            }
        }

        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()

    }
    private  fun filterPropertyCategory(list :ArrayList<PropertyData>){
        if(viewOnlyCommercialProperty) {

            val list = list.filter {
                it.purpose_type.equals("Commercial", true)
            }
            propertyListFiltered.clear()
            propertyListFiltered.addAll(list)
        } else if(viewOnlyResidenacialProperty){
            val list = list.filter {
                it.purpose_type.equals("Residential", true)
            }
            propertyListFiltered.clear()
            propertyListFiltered.addAll(list)
            Log.e("Gul21","filterPropertyCategory "+propertyListFiltered.size)
        }else {

            if(selectedPostType=="All") {
                val list = propertyList.filter {
                    it.purpose_type.equals(
                        "Residential",
                        true
                    ) || it.purpose_type.equals("Commercial", true)
                }
                propertyListFiltered.clear()
                propertyListFiltered.addAll(list)
                Log.e("Gul21", "propertyListFiltered " + list.size)
            }
        }
    }
    private fun filterCategory(category: String, type: String, beds: String, baths: String, minPrice: String, maxPrice: String) {


        Log.e("filterCategory", "category :: $category")
        Log.e("filterCategory", "type :: $type")
        Log.e("filterCategory", "beds :: $beds")
        Log.e("filterCategory", "baths :: $baths")
        Log.e("filterCategory", "minPrice :: $minPrice")
        Log.e("filterCategory", "maxPrice :: $maxPrice")

        when (selectedPostType) {
            "request" -> {
                propertyListFiltered.clear()
                val list = propertyList.filter {
                    it.post_type.equals("request", true)
                }
                propertyListFiltered.addAll(list)
                Log.e("Gul21","propertyListFiltered "+propertyListFiltered.size)
                filterPropertyCategory(propertyListFiltered)

            }
            "property" -> {
                propertyListFiltered.clear()
                val list = propertyList.filter {
                    it.post_type.equals("property", true)
                }
                propertyListFiltered.addAll(list)
                Log.e("Gul21","propertyListFiltered "+propertyListFiltered.size)
                filterPropertyCategory(propertyListFiltered)



            }
            else -> {
//                propertyListFiltered.clear()
                Log.e("Gul21","ALL "+propertyListFiltered.size)
                val list = propertyList.filter {
                    it.post_type.equals("property", true) ||  it.post_type.equals("request", true)
                }
                propertyListFiltered.clear()
                propertyListFiltered.addAll(list)
                Log.e("Gul21","propertyListFiltered "+list.size)
                filterPropertyCategory(propertyListFiltered)
//                propertyListFiltered.clear()
//                propertyListFiltered.addAll(propertyList)
//                filterPropertyCategory(propertyListFiltered)
            }
        }





        when (category) {
            "All" -> {
                if(viewOnlyCommercialProperty) {
                    Log.e("Gul21","viewOnlyCommercialProperty "+propertyListFiltered.size)
                    val list = propertyListFiltered.filter {
                        it.purpose_type.equals("Commercial", true)
                    }
                    propertyListFiltered.clear()
                    propertyListFiltered.addAll(list)
                } else  if(viewOnlyResidenacialProperty){

//                    propertyListFiltered.forEach {
//
//                        Log.e("Gul21","purpose_type "+ it.purpose_type)
//                    }


                    val list = propertyListFiltered.filter {
                        it.purpose_type.equals("Commercial", true)
                    }
//                    propertyListFiltered.clear()
//                    propertyListFiltered.addAll(list)
                    Log.e("Gul21","viewOnlyResidenacialProperty "+list.size)
//                    propertyListFiltered.clear()
//                    propertyListFiltered.addAll(propertyList)
                }
            }

            "Rent" -> {
                val list = propertyListFiltered.filter {
                    it.purpose.equals("Rent", true)
                }
                propertyListFiltered.clear()
                propertyListFiltered.addAll(list)
            }

            "Sale" -> {
                val list = propertyListFiltered.filter {
                    it.purpose.equals("Sale", true)
                }
                propertyListFiltered.clear()
                propertyListFiltered.addAll(list)
            }

        }

        if (type != "") {
            val list = propertyListFiltered.filter {
                it.property_type.equals(type, true)
            }
            propertyListFiltered.clear()
            propertyListFiltered.addAll(list)
        }

        if (beds != "") {
            val list = propertyListFiltered.filter {
                it.bedrooms.equals(beds, true)
            }
            propertyListFiltered.clear()
            propertyListFiltered.addAll(list)
        }

        if (baths != "") {
            val list = propertyListFiltered.filter {
                it.bathrooms.equals(baths, true)
            }
            propertyListFiltered.clear()
            propertyListFiltered.addAll(list)
        }

        if(minPrice != "No min." && maxPrice != "No max.") {
            Log.e("Tag21351q2", "-------------------")
            propertyListFiltered.forEach {
                Log.e("Tag21351q2", "data ${it.sp}")
            }


                Log.e("Tag21351q2", "-------------------")


            val filteredList = propertyListFiltered.filter { product ->

                if(product.post_type=="request"){
                    removeComma(product.budget_min).toLong() in removeComma(minPrice).toLong()..removeComma(
                        maxPrice
                    ).toLong()  &&

                            removeComma(product.budget_max).toLong() in removeComma(minPrice).toLong()..removeComma(
                        maxPrice
                    ).toLong()
                }else {
                    removeComma(product.sp).toLong() in removeComma(minPrice).toLong()..removeComma(
                        maxPrice
                    ).toLong()
                }


            }




                propertyListFiltered.clear()
            propertyListFiltered.addAll(filteredList)

        }else if(minPrice == "No min." && maxPrice != "No max.") {




            val filteredList = propertyListFiltered.filter { product ->

                if(product.post_type=="request"){
                    removeComma(product.budget_max).toLong() <= removeComma(
                        maxPrice
                    ).toLong()
                }else {
                    removeComma(product.sp).toLong() <= removeComma(
                        maxPrice
                    ).toLong()
                }


            }





            propertyListFiltered.clear()
            propertyListFiltered.addAll(filteredList)
        } else if(minPrice != "No min." && maxPrice == "No max.") {






            val filteredList = propertyListFiltered.filter { product ->

                if(product.post_type=="request"){
                    removeComma(product.budget_min).toLong() >= removeComma(
                        minPrice
                    ).toLong()
                }else {
                    removeComma(product.sp).toLong() >= removeComma(
                        minPrice
                    ).toLong()
                }


            }


            propertyListFiltered.clear()
            propertyListFiltered.addAll(filteredList)
        }
    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        when (key) {
            "chat" -> {
//                Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_LONG).show()

                findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToChatFragment2(propertyList[position],false,
                    propertyListFiltered[position].user_id!!,propertyListFiltered[position].user_name.toString(),
                    propertyListFiltered[position].user_picture.toString()
                ))



            }

            "open_request_detail"->{
                findNavController().navigate(
                    PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment(
                        propertyListFiltered[position]
                    )
                )
            }

            "click_on_item" -> {
                when (position) {
                    0->{
                        bottomSheetForPost()
                    }
                    1 -> {
                        bottomSheetForRent()
                    }

                    2 -> {
                        if (viewOnlyCommercialProperty) {
                            showCommercialProperty()
                        } else {
                            showAllPropertyType()
                        }

                        bottomSheetForPropertyType()
                    }

                    3 -> {

                        when (selectedCategory) {
                            "Rent" -> {
                                priceDataForRent()
                            }
                            "Sale" -> {
                                priceDataForBuy()
                            }
                            else -> {
                                priceDataForAll()
                            }
                        }

                        bottomSheetForPrice()
                    }

                    4 -> {
                        bathRoomData()
                        bedRoomData()
                        bottomSheetForBedAndBath()
                    }
                }

            }

            "property_type" -> {
                propertyItemList[previousPosition].selected = false

                if (!propertyItemList[position].selected) {
                    propertyItemList[position].selected = true
                    previousPosition = position
                }

                propertyType = propertyItemList[previousPosition].name

                selectedPropertyType = propertyType
                filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)

                propertyTypeAdapter.notifyDataSetChanged()
            }

            "click_on_bedroom_item" -> {
                bedRoomList[bedRoomPreviousPosition].selected = false

                if (!bedRoomList[position].selected) {

                    bedRoomList[position].selected = true
                    bedRoomPreviousPosition = position
                }
                bedRoomAdapter.notifyDataSetChanged()

                bedroomStr = bedRoomList[bedRoomPreviousPosition].name

                selectedBed = bedroomStr
                filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            }

            "click_on_bathroom_item" -> {
                bathRoomList[bathRoomPreviousPosition].selected = false

                if (!bathRoomList[position].selected) {

                    bathRoomList[position].selected = true
                    bathRoomPreviousPosition = position
                }
                bathRoomAdapter.notifyDataSetChanged()

                bathroomStr = bathRoomList[bathRoomPreviousPosition].name

                selectedBath = bathroomStr
                filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
            }

            "click_on_min_price" -> {

            }

            "cancel_filter" -> {
                when(position) {
                    0 -> {
                        selectedPostType = "All"
                        filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
                        propertyFilterList[0] = PropertyType(name = selectedPostType, selected = false)
                        propertyFiltersAdapter.notifyItemChanged(0)
                        adapter.notifyDataSetChanged()
                    }
                    1 -> {
                        selectedCategory = "All"
                        
                        viewOnlyCommercialProperty = false
                        filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
                        propertyFilterList[1] = PropertyType(name = selectedCategory, selected = false)
                        propertyFiltersAdapter.notifyItemChanged(1)
                        adapter.notifyDataSetChanged()
                    }
                    2 -> {
                        propertyType = ""
                        selectedPropertyType = ""
                        previousPosition = 0
                        propertyItemList[previousPosition].selected = false

                        filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
                        propertyFilterList[2] = PropertyType(name = "Property Type", selected = false)
                        propertyFiltersAdapter.notifyItemChanged(2)
                        adapter.notifyDataSetChanged()
                    }
                    3 -> {
                        selectedMinPrice = "No min."
                        selectedMaxPrice = "No max."
                        filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
                        propertyFilterList[3] = PropertyType(name = "Price", selected = false)
                        propertyFiltersAdapter.notifyItemChanged(3)
                        adapter.notifyDataSetChanged()
                    }
                    4 -> {
                        bedroomStr = ""
                        bathroomStr = ""
                        selectedBed = ""
                        selectedBath = ""
                        bedRoomPreviousPosition = 0
                        bathRoomPreviousPosition = 0
                        filterCategory(selectedCategory, selectedPropertyType, selectedBed, selectedBath, selectedMinPrice, selectedMaxPrice)
                        propertyFilterList[4] = PropertyType(name = "Beds & Baths", selected = false)
                        propertyFiltersAdapter.notifyItemChanged(4)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            else -> {
                Const.screenName=""




                propertyListFiltered[position].sender_name=Const.userName
                propertyListFiltered[position].sender_image=Const.userImage
                propertyListFiltered[position].sender_phone=Const.userPhone
                propertyListFiltered[position].sender_whatsapp=Const.userWhatsapp
                propertyListFiltered[position].sender_experience=Const.userExperience
                propertyListFiltered[position].sender_speciality=Const.userSpeciality
                propertyListFiltered[position].sender_company=Const.userCompany

                Log.e("Tag2133","sender_name" + propertyListFiltered[position].sender_name )
                Log.e("Tag2133","sender_image "+propertyListFiltered[position].sender_image )
                Log.e("Tag2133","sender_phone "+ propertyListFiltered[position].sender_phone)
                Log.e("Tag2133","sender_whatsapp "+ propertyListFiltered[position].sender_whatsapp)
                Log.e("Tag2133","sender_experience "+ propertyListFiltered[position].sender_experience)
                Log.e("Tag2133","sender_speciality "+  propertyListFiltered[position].sender_speciality)
                Log.e("Tag2133","sender_company "+ propertyListFiltered[position].sender_company)

                findNavController().navigate(
                    PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment(
                        propertyListFiltered[position]
                    )
                )
            }
        }
    }
}