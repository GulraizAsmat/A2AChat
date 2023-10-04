package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.BathroomItemAdapter
import app.unduit.a2achatapp.adapters.BedroomItemAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep1Binding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep2Binding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType
import app.unduit.a2achatapp.models.PropertyData


class PostPropertyStep2Fragment : Fragment(), View.OnClickListener, AdapterListener {

    val args: PostPropertyStep2FragmentArgs by navArgs()

    private var bedRoomList = ArrayList<BathBedType>()
    private var bedRoomPreviousPosition = 0

    var bathRoomList = ArrayList<BathBedType>()
    var bathRoomPreviousPosition = 0

    var developmentStatus = false

    var bedroomStr = "Studio"
    var bathroomStr = "1"

    private val bedRoomAdapter by lazy {
        BedroomItemAdapter(
            requireContext(),
            this,
            bedRoomList
        )
    }
    private val bathRoomAdapter by lazy {
        BathroomItemAdapter(
            requireContext(),
            this,
            bathRoomList
        )
    }

    var isEdit = false

    private lateinit var propertyData: PropertyData

    private lateinit var binding: FragmentPostPropertyStep2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep2Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Const.screenName = ""
        init()
    }


    fun init() {
        if(Const.REQUESDTED){
            binding.screenTitle.text="Post a Request"
            binding.propertyDevStatusGroup.visibility=View.GONE
            binding.clProcessBarForRent.visibility=View.VISIBLE
            binding.clProcessBar.visibility=View.INVISIBLE
        }


        isEdit = args.isEdit
        propertyData = args.propertyData

        listeners()
        bedRecyclerViewManager()
        bathRecyclerViewManager()
        bedRoomData()
        bathRoomData()

        if (isEdit) {
            setData()
        }
        defaultData(propertyData)
    }

    private fun defaultData(propertyData: PropertyData){

        if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Residential"){
            selectRentAndResidents()
        }else if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Commercial"){
            selectRentAndCommercial()
        }

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

    private fun bedRecyclerViewManager() {

        binding.rvBed.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBed.itemAnimator = DefaultItemAnimator()
        binding.rvBed.adapter = bedRoomAdapter
    }

    private fun bathRecyclerViewManager() {

        binding.rvBath.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBath.itemAnimator = DefaultItemAnimator()
        binding.rvBath.adapter = bathRoomAdapter
    }


    private fun selectOffPlan() {
        binding.clOffPlan.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.residentsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_white_shade_1
            )
        )

        binding.clReady.setBackgroundResource(R.drawable.ic_light_purple_bg_1)

        binding.commercialTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_purple_shade_1
            )
        )
        developmentStatus = false


    }

    private fun selectReady() {
        binding.clOffPlan.setBackgroundResource(R.drawable.ic_light_purple_bg_1)

        binding.residentsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_purple_shade_1
            )
        )

        binding.clReady.setBackgroundResource(R.drawable.ic_dark_purple_bg)

        binding.commercialTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_white_shade_1
            )
        )

        developmentStatus = true
    }


    fun spinnerData() {

    }

    fun listeners() {
        binding.nextBtn.setOnClickListener(this)
        binding.clReady.setOnClickListener(this)
        binding.clOffPlan.setOnClickListener(this)
        binding.backIcon.setOnClickListener(this)
    }

    private fun verifyData() {
        val areaCommunity = binding.areaCommunity.text.toString()
        val project = binding.building.text.toString()
//        val price = binding.price.text.toString()
//        if(price.isEmpty()) {
//            requireContext().showToast("Please enter a Price")
//        } else
        if (areaCommunity.isEmpty()) {
            requireContext().showToast("Please enter Area/Community")
        } else if (project.isEmpty()) {
            requireContext().showToast("Please enter Project")
        } else {

//            propertyData.price = price
            propertyData.area_community = areaCommunity
            propertyData.project = project
            propertyData.bedrooms = bedroomStr
            propertyData.bathrooms = bathroomStr
            propertyData.development_status = if (developmentStatus) "Ready" else "Off-plan"

            findNavController().navigate(
                PostPropertyStep2FragmentDirections.actionPostFragmentToPostPropertyStep3Fragment(
                    propertyData,
                    isEdit
                )
            )
        }
    }

    private fun setData() {
        binding.price.setText(propertyData.price)
        binding.areaCommunity.setText(propertyData.area_community)
        binding.building.setText(propertyData.project)

        if (propertyData.development_status.equals("Ready", true)) {
            selectReady()
        } else {
            selectOffPlan()
        }

        bedRoomList.forEachIndexed { index, type ->
            if (type.name.equals(propertyData.bedrooms, true)) {
                bedRoomList[bedRoomPreviousPosition].selected = false

                if (!bedRoomList[index].selected) {

                    bedRoomList[index].selected = true
                    bedRoomPreviousPosition = index
                }
                bedroomStr = bedRoomList[bedRoomPreviousPosition].name

                bedRoomAdapter.notifyDataSetChanged()
            }
        }

        bathRoomList.forEachIndexed { index, type ->
            if (type.name.equals(propertyData.bathrooms, true)) {
                bathRoomList[bathRoomPreviousPosition].selected = false

                if (!bathRoomList[index].selected) {

                    bathRoomList[index].selected = true
                    bathRoomPreviousPosition = index
                }
                bathroomStr = bathRoomList[bathRoomPreviousPosition].name

                bathRoomAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.next_btn -> {
                verifyData()
            }

            R.id.cl_off_plan -> {
                selectOffPlan()
            }

            R.id.cl_ready -> {
                selectReady()
            }

            R.id.back_icon -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        when (key) {
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
        }
    }

    private fun selectRentAndResidents(){
        binding.propertyDevStatusGroup.visibility=View.GONE
        binding.clProcessBarForRent.visibility=View.VISIBLE
        binding.clProcessBar.visibility=View.INVISIBLE
    }


    private fun selectRentAndCommercial(){
        binding.propertyDevStatusGroup.visibility=View.GONE
        binding.propertyHideForComGroup.visibility=View.GONE
        binding.clProcessBarForRent.visibility=View.VISIBLE
        binding.clProcessBar.visibility=View.INVISIBLE
    }
}