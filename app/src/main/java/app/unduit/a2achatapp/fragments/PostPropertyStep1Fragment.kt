package app.unduit.a2achatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.HomeSwiperAdapter
import app.unduit.a2achatapp.adapters.PropertyTypeItemAdapter
import app.unduit.a2achatapp.databinding.FragmentHomeBinding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep1Binding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import app.unduit.a2achatapp.models.PropertyType
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class PostPropertyStep1Fragment : Fragment(), View.OnClickListener, AdapterListener {

    val args: PostPropertyStep1FragmentArgs by navArgs()

    var salePurpose = true // forSale-> true , forRent -> false
    var propertyType = true //forResidents-> true , forCommercial -> false

    var propertyItemList = ArrayList<PropertyType>()
    var previousPosition = 0

    private var propertyData: PropertyData? = null
    private var isEdit = false

    var purpose: String = "Sale"
    var purpose_type: String = "Residential"
    var property_type: String = "House"


    private val propertyTypeAdapter by lazy {
        PropertyTypeItemAdapter(
            requireContext(),
            this,
            propertyItemList
        )
    }


    private lateinit var binding: FragmentPostPropertyStep1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep1Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Const.screenName = "add_property"

        init()
    }

    fun init() {

        isEdit = args.propertyData != null
        propertyData = args.propertyData ?: PropertyData()

        recyclerViewManager()
        showResidentProperty()
        listeners()

        if(isEdit) {
            setData()
        }
    }

    fun listeners() {
        binding.clCommercial.setOnClickListener(this)
        binding.clForRent.setOnClickListener(this)
        binding.clForSale.setOnClickListener(this)
        binding.backIcon.setOnClickListener(this)
        binding.clResident.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        binding.selectedPropertyType.setOnClickListener(this)
    }


    private fun recyclerViewManager() {

        binding.rvProperty.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvProperty.itemAnimator = DefaultItemAnimator()
        binding.rvProperty.adapter = propertyTypeAdapter
    }

    private fun selectForSaleOption() {
        binding.clForSale.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.clForRent.setBackgroundResource(R.drawable.ic_light_purple_bg_1)
        salePurpose = true

        purpose = "Sale"
    }

    private fun selectForRentOption() {
        binding.clForSale.setBackgroundResource(R.drawable.ic_light_purple_bg_1)
        binding.clForRent.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        salePurpose = false

        purpose = "Rent"
    }

    private fun selectResidents() {
        binding.clResident.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.residentIcon.setImageResource(R.drawable.ic_resident)
        binding.residentsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_white_shade_1
            )
        )

        binding.clCommercial.setBackgroundResource(R.color.color_white_shade_1)
        binding.commercialIcon.setImageResource(R.drawable.ic_commercial)
        binding.commercialTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_black_shade_1
            )
        )
        propertyType = true

        purpose_type = "Residential"
    }

    private fun selectCommercial() {
        binding.clResident.setBackgroundResource(R.color.color_white_shade_1)
        binding.residentIcon.setImageResource(R.drawable.ic_resident_purple)
        binding.residentsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_black_shade_1
            )
        )

        binding.clCommercial.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.commercialIcon.setImageResource(R.drawable.ic_commercial_white)
        binding.commercialTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_white_shade_1
            )
        )

        propertyType = false

        purpose_type = "Commercial"
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
                name = "labour Camp",
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

    private fun setData() {
        propertyData?.let { data ->
            if(data.purpose.equals("Rent", true)) {
                selectForRentOption()
            } else {
                selectForSaleOption()
            }

            if(data.purpose_type.equals("Residential", true)) {
                selectResidents()
                showResidentProperty()
            } else {
                selectCommercial()
                showCommercialProperty()
            }

            val propertyType = data.property_type
            propertyItemList.forEachIndexed { index, type ->
                if(type.name.equals(propertyType, true)) {
                    propertyItemList[previousPosition].selected = false

                    if (!propertyItemList[index].selected) {
                        propertyItemList[index].selected = true
                        previousPosition = index
                    }
                    property_type = propertyItemList[previousPosition].name
                }
            }
        }
    }

    fun bottomSheet(){

        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_add_property_step1, null)

        val rvPropertyType = view.findViewById<RecyclerView>(R.id.rv_property_type)
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexWrap = FlexWrap.WRAP  // Items will wrap to the next line if there's not enough space
        layoutManager.flexDirection = FlexDirection.ROW
        rvPropertyType.layoutManager = layoutManager


        rvPropertyType.adapter = propertyTypeAdapter
        propertyTypeAdapter.notifyDataSetChanged()
        dialog.setContentView(view)
        dialog.show()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cl_for_sale -> {
                selectForSaleOption()
            }

            R.id.cl_for_rent -> {
                selectForRentOption()
            }

            R.id.cl_resident -> {
                selectResidents()
                showResidentProperty()
                bottomSheet()
            }

            R.id.cl_commercial -> {
                selectCommercial()
                showCommercialProperty()
                bottomSheet()
            }

            R.id.next_btn -> {
            if(binding.selectedPropertyType.text!="Please Select Property Type") {
                val auth = Firebase.auth
                val currentUser = auth.currentUser

                propertyData?.user_id = currentUser?.uid
                propertyData?.purpose = purpose
                propertyData?.purpose_type = purpose_type
                propertyData?.property_type = property_type

                propertyData?.let {
                    findNavController().navigate(
                        PostPropertyStep1FragmentDirections.actionPostPropertyStep1FragmentToPostFragment(
                            it, isEdit
                        )
                    )
                }
            }else {
                Toast.makeText(requireContext(),"Please select property type",Toast.LENGTH_LONG).show()
            }

            }

            R.id.back_icon -> {


                requireActivity().onBackPressed()
            }

            R.id.selected_property_type->{
                bottomSheet()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onAdapterItemClicked(key: String, position: Int) {
        when (key) {
            "click_on_item" -> {

                propertyItemList[previousPosition].selected = false

                if (!propertyItemList[position].selected) {
                    propertyItemList[position].selected = true
                    previousPosition = position
                }

                property_type = propertyItemList[previousPosition].name
                binding.selectedPropertyType.text=property_type
                binding.selectedPropertyType.setBackgroundResource(R.drawable.bg_btn_login)
                binding.selectedPropertyType.setTextColor(ContextCompat.getColor(requireContext(),R.color.color_white_shade_1))
                propertyTypeAdapter.notifyDataSetChanged()

            }
        }
    }

}