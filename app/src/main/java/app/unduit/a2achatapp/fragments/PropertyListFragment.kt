package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class PropertyListFragment : Fragment(), AdapterListener {

    private val TAG = "PropertyListFragment"

    private lateinit var binding: FragmentPropertyListBinding
    var previousPosition = 0
    var propertyType = ""
    var bathRoomPreviousPosition = 0
    private var bedRoomPreviousPosition = 0
    private var minPricePreviousPosition = 0
    private var maxPricePreviousPosition = 0
    var viewOnlyCommercialProperty = false
    var bedroomStr = ""
    var bathroomStr = ""
    private lateinit var propertyTypeDialog: BottomSheetDialog

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    private lateinit var auth: FirebaseAuth

    private var propertyList = ArrayList<PropertyData>()
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
            propertyList
        )
    }


    private val propertyFiltersAdapter by lazy {
        PropertyFiltersAdapter(
            requireContext(),
            this,
            propertyFilterList
        )
    }

    private val propertyTypeAdapter by lazy {
        PropertyTypeFilterAdapter(
            requireContext(),
            this,
            propertyItemList
        )
    }


    private val bedRoomAdapter by lazy {
        BedroomItemFilterAdapter(
            requireContext(),
            this,
            bedRoomList
        )
    }
    private val bathRoomAdapter by lazy {
        BathroomItemFilterAdapter(
            requireContext(),
            this,
            bathRoomList
        )
    }

    private val minPriceAdapter by lazy {
        MinPriceFilterAdapter(
            requireContext(),
            this,
            bathRoomList
        )
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

        setListeners()
        setRV()
        getData()
        loadUserProfileImage()
        propertyFilterRecyclerViewManger()
        filtersListData()
    }


    private fun filtersListData() {

        propertyFilterList.clear()
        propertyFilterList.add(PropertyType(name = "Rent"))
        propertyFilterList.add(PropertyType(name = "Property Type"))
        propertyFilterList.add(PropertyType(name = "Price"))
        propertyFilterList.add(PropertyType(name = "Beds & Baths"))
//        propertyFilterList.add(PropertyType(name ="Amenitites"))

        propertyFiltersAdapter.notifyDataSetChanged()


    }

    private fun bedRoomData() {
        bedRoomPreviousPosition = 0
        bedRoomList.clear()
        bedRoomList.add(BathBedType("Studio", true))
        bedRoomList.add(BathBedType("1", false))
        bedRoomList.add(BathBedType("2", false))
        bedRoomList.add(BathBedType("3", false))
        bedRoomList.add(BathBedType("4", false))
        bedRoomList.add(BathBedType("5", false))
        bedRoomList.add(BathBedType("6", false))
        bedRoomList.add(BathBedType("7", false))
        bedRoomList.add(BathBedType("8+", false))
        bedRoomAdapter.notifyDataSetChanged()

    }

    private fun priceData() {
        minPricePreviousPosition = 0
        maxPricePreviousPosition = 0
        minPriceList.clear()
        maxPriceList.clear()
        minPriceList.add("No min.")
        minPriceList.add("20,000")
                        minPriceList . add ("30,000")
                        minPriceList . add ("40,000")
                        minPriceList . add ("50,000")
                        minPriceList . add ("60,000")
                        minPriceList . add ("70,000")
                        minPriceList . add ("80,000")
                        minPriceList . add ("90,000")
                        minPriceList . add ("100,000")
                        minPriceList . add ("100,000")
                        minPriceList . add ("150,000")
                        minPriceList . add ("200,000")
                        minPriceList . add ("250,000")
                        minPriceList . add ("300,000")
                        minPriceList . add ("350,000")
                        minPriceList . add ("400,000")
                        minPriceList . add ("450,000")
                        minPriceList . add ("500,000")
                        minPriceList . add ("550,000")
                        minPriceList . add ("600,000")
                        minPriceList . add ("650,000")
                        minPriceList . add ("700,000")
                        minPriceList . add ("750,000")
                        minPriceList . add ("800,000")
                        minPriceList . add ("850,000")
                        minPriceList . add ("900,000")
                        minPriceList . add ("950,000")
                        minPriceList . add ("1000,000")
                        minPriceList . add ("1000,000+")


        maxPriceList.addAll(minPriceList)


    }

    private fun bathRoomData() {
        bathRoomList.clear()
        bathRoomPreviousPosition = 0
        bathRoomList.add(BathBedType("1", true))
        bathRoomList.add(BathBedType("2", false))
        bathRoomList.add(BathBedType("3", false))
        bathRoomList.add(BathBedType("4", false))
        bathRoomList.add(BathBedType("5", false))
        bathRoomList.add(BathBedType("6", false))
        bathRoomList.add(BathBedType("7+", false))

        bathRoomAdapter.notifyDataSetChanged()

    }


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
            Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_LONG).show()
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


    private fun getData() {
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("properties").whereNotEqualTo("user_id", cUser.uid)

            ref.get()
                .addOnSuccessListener { documents ->
                    propertyList.clear()
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertyList.add(document.toObject(PropertyData::class.java))
                    }

                    val list = propertyList.sortedByDescending { it.created_date }
                    propertyList.clear()
                    propertyList.addAll(list)
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

        propertyTypeAdapter.notifyDataSetChanged()
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


        propertyTypeAdapter.notifyDataSetChanged()
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
        propertyTypeAdapter.notifyDataSetChanged()

    }

    private fun bottomSheetForRent() {

        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_for_rent, null)

        val forRent = view.findViewById<ConstraintLayout>(R.id.cl_selector_1)
        val forSale = view.findViewById<ConstraintLayout>(R.id.cl_selector_2)
        val rentTitle = view.findViewById<TextView>(R.id.rent_title)
        val buyTitle = view.findViewById<TextView>(R.id.buy_title)
        val onlyCommercial = view.findViewById<AppCompatCheckBox>(R.id.cb_commercial)

        forRent.setOnClickListener {
            forRent.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
            forSale.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
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
        }

        forSale.setOnClickListener {
            forRent.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
            forSale.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
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
        }

        onlyCommercial.setOnCheckedChangeListener { view, b ->

            Log.e("Taag34", " status $b")
            viewOnlyCommercialProperty = b

        }

        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()
    }

    private fun bottomSheetForPropertyType() {


        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_propertytype, null)

        val rvPropertyType = view.findViewById<RecyclerView>(R.id.rv_property_type)
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap =
            FlexWrap.WRAP  // Items will wrap to the next line if there's not enough space
        layoutManager.flexDirection = FlexDirection.ROW
        rvPropertyType.layoutManager = layoutManager


        rvPropertyType.adapter = propertyTypeAdapter
        propertyTypeAdapter.notifyDataSetChanged()
        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()

    }


    private fun bottomSheetForPrice() {


        propertyTypeDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_price, null)

        val rvMinPrice = view.findViewById<Spinner>(R.id.rv_min_price)
        val rvMaxPrice = view.findViewById<Spinner>(R.id.rv_max_price)
        val clMinPrice = view.findViewById<ConstraintLayout>(R.id.cl_min_price)




        val adapter = CustomPriceSpinnerAdapter(requireContext(), minPriceList)

        rvMinPrice.adapter = adapter
        rvMinPrice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem =minPriceList[position]

//                    floorStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }




        val adapterMax = CustomPriceSpinnerAdapter(requireContext(), maxPriceList)

        rvMaxPrice.adapter = adapter
        rvMaxPrice.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem =minPriceList[position]

//                    floorStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
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





        bedRoomAdapter.notifyDataSetChanged()
        bathRoomAdapter.notifyDataSetChanged()








        propertyTypeDialog.setContentView(view)
        propertyTypeDialog.show()

    }


    override fun onAdapterItemClicked(key: String, position: Int) {
        when (key) {
            "chat" -> {
                Toast.makeText(requireContext(), "Under Development", Toast.LENGTH_LONG).show()
            }

            "click_on_item" -> {
                when (position) {
                    0 -> {
                        bottomSheetForRent()
                    }

                    1 -> {
                        if (viewOnlyCommercialProperty) {
                            showCommercialProperty()
                        } else {
                            showAllPropertyType()
                        }

                        bottomSheetForPropertyType()
                    }

                    2 -> {
                        priceData()
                        bottomSheetForPrice()
                    }

                    3 -> {
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
            }

            "click_on_bathroom_item" -> {
                bathRoomList[bathRoomPreviousPosition].selected = false

                if (!bathRoomList[position].selected) {

                    bathRoomList[position].selected = true
                    bathRoomPreviousPosition = position
                }
                bathRoomAdapter.notifyDataSetChanged()

                bathroomStr = bathRoomList[bathRoomPreviousPosition].name
            }
            "click_on_min_price"->{

            }
            else -> {
                findNavController().navigate(
                    PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment(
                        propertyList[position]
                    )
                )
            }
        }
    }
}