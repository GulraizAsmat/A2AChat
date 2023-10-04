package app.unduit.a2achatapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.BathroomItemAdapter
import app.unduit.a2achatapp.adapters.BedroomItemAdapter
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep3Binding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.Const.REQUESDTED
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.helpers.gone
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType
import app.unduit.a2achatapp.models.PropertyData
import java.util.Calendar


class PostPropertyStep3Fragment : Fragment() {


    private lateinit var binding: FragmentPostPropertyStep3Binding

    private val args: PostPropertyStep3FragmentArgs by navArgs()

    private lateinit var propertyData: PropertyData
    var isEdit = false

    var occupancyStr = "Vacant"
    var numberOfCheckStr = "1"
    var fittingStr = "Shall & core"
    var paymentStr = "Cash"
    var purchaseStr = "Investment"
    var movingTimeStr = "1"
    var furnitureStr = "Furnished"
    var isRentedSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep3Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        listeners()
    }
    private fun defaultData(propertyData: PropertyData){

        if(Const.REQUESDTED){
            binding.screenTitle.text="Post a Request"
            if(propertyData.purpose=="Sale" && propertyData.purpose_type=="Residential"){

                binding.clProcessBarForRent.visibility=View.VISIBLE
                binding.clProcessBar.visibility=View.INVISIBLE
                binding.rentResidentsHideGroup.visibility=View.GONE

                binding.clRequestBudget.visibility=View.VISIBLE
                binding.buildingTitleOptional .visibility=View.GONE
                binding.areaTitleStar .visibility=View.GONE
                binding.area.visibility=View.GONE
                binding.sqFeet.visibility=View.GONE
                binding.viewSq.visibility=View.GONE
                binding.occupancyTitle1.visibility=View.VISIBLE
                binding.occupancyIcArrow1.visibility=View.VISIBLE
                binding.spinnerOccupancy1.visibility=View.VISIBLE

                binding.spinnerPayment.visibility=View.VISIBLE
                binding.paymentTitle.visibility=View.VISIBLE
                binding.paymentIcArrow.visibility=View.VISIBLE

                binding.spinnerPurchase.visibility=View.VISIBLE
                binding.purchaseTitle.visibility=View.VISIBLE
                binding.purchaseIcArrow.visibility=View.VISIBLE

            }else {
                binding.clProcessBarForRent.visibility=View.VISIBLE
                binding.clProcessBar.visibility=View.INVISIBLE
                binding.rentResidentsHideGroup.visibility=View.GONE
                binding.clRequestBudget.visibility=View.VISIBLE
                binding.areaTitleStar .visibility=View.GONE
                binding.area.visibility=View.GONE
                binding.buildingTitleOptional .visibility=View.GONE
                binding.sqFeet.visibility=View.GONE


                binding.numberOfChecks.visibility=View.VISIBLE
                binding.rentedTillTitle.visibility=View.VISIBLE
                binding.rentedTillIcon.visibility=View.VISIBLE

                binding.movingTimeTitle.visibility=View.VISIBLE
                binding.movingTimeIcon.visibility=View.VISIBLE
                binding.movingTimeSpinner.visibility=View.VISIBLE

                binding.furnishIcon.visibility=View.VISIBLE
                binding.furnishTitle.visibility=View.VISIBLE
                binding.furnitureSpinner.visibility=View.VISIBLE

            }



        }else {
            if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Residential"){
                selectRentAndResidents()
            }
            else if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Commercial"){
                selectRentAndCommercial()
            }
        }





    }

    fun init() {

        isEdit = args.isEdit
        propertyData = args.propertyData

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        byDefaultData()
        maidCheckManager()
        balconyCheckManager()
        spinnerManager()

        if(isEdit) {
            setData()
        }
        defaultData(propertyData)
    }


    private fun byDefaultData() {
        binding.cbMaidNo.isChecked = true
        binding.cbBalconyNo.isChecked = true
    }


    private fun spinnerManager() {
        occupancySpinner()
        occupancySpinner1()
        numberOfCheckSpinner()
        paymentSpinner()
        purchaseSpinner()
        fittingSpinner()
        movingTimeSpinner()
        furnitureSpinner()
    }

    private fun occupancySpinner() {
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.occupancyList())

        binding.spinnerOccupancy.adapter = adapter
        binding.spinnerOccupancy.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.occupancyList()[position]
                    occupancyStr = selectedItem
                    isRentedSelected = if(occupancyStr.equals("Rented", true)) {
                        binding.groupPrice.gone()
                        binding.groupRent.visible()
                        true
                    } else {
                        binding.groupPrice.visible()
                        binding.groupRent.gone()
                        false
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun occupancySpinner1() {
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.occupancyList1())

        binding.spinnerOccupancy1.adapter = adapter
        binding.spinnerOccupancy1.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.occupancyList1()[position]
                    occupancyStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun paymentSpinner() {
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.paymentList())

        binding.spinnerPayment.adapter = adapter
        binding.spinnerPayment.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.paymentList()[position]
                    paymentStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun purchaseSpinner() {
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.purchaseGaolList())

        binding.spinnerPurchase.adapter = adapter
        binding.spinnerPurchase.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.purchaseGaolList()[position]
                    purchaseStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun numberOfCheckSpinner(){
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.numberOfChecksList())

        binding.numberOfChecks.adapter = adapter
        binding.numberOfChecks.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.numberOfChecksList()[position]
                    numberOfCheckStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun fittingSpinner(){
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.fittingList())

        binding.fitting.adapter = adapter
        binding.fitting.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.fittingList()[position]
                    fittingStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun movingTimeSpinner(){
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.movingTimeList())

        binding.movingTimeSpinner.adapter = adapter
        binding.movingTimeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.movingTimeList()[position]
                    movingTimeStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun furnitureSpinner(){
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.furnitureList())

        binding.furnitureSpinner.adapter = adapter
        binding.furnitureSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.furnitureList()[position]
                    furnitureStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }


    private fun maidCheckManager() {

        binding.cbMaidYes.setOnCheckedChangeListener { _, b ->
            binding.cbMaidNo.isChecked = !b
        }

        binding.cbMaidNo.setOnCheckedChangeListener { _, b ->
            binding.cbMaidYes.isChecked = !b
        }


    }

    private fun balconyCheckManager() {

        binding.cbBalconyYes.setOnCheckedChangeListener { _, b ->
            binding.cbBalconyNo.isChecked = !b
        }

        binding.cbBalconyNo.setOnCheckedChangeListener { _, b ->
            binding.cbBalconyYes.isChecked = !b
        }


    }

    private fun openCalender() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
//            binding.numberOfChecks.setText("$dayOfMonth/$monthOfYear/$year")

        }, year, month, day)

        dpd.show()
    }

    private fun setData() {
        binding.area.setText(propertyData.area_size)
        binding.op.setText(propertyData.op)
        binding.sp.setText(propertyData.sp)
        binding.roi.setText(propertyData.roi)
        binding.rentedFor.setText(propertyData.rented_for)
//        binding.rentedTill.setText(propertyData.rented_till)

        if(propertyData.maidroom) {
            binding.cbMaidNo.isChecked = false
            binding.cbMaidYes.isChecked = true
        } else {
            binding.cbMaidNo.isChecked = true
            binding.cbMaidYes.isChecked = false
        }

        if(propertyData.balcony) {
            binding.cbBalconyNo.isChecked = false
            binding.cbBalconyYes.isChecked = true
        } else {
            binding.cbBalconyNo.isChecked = true
            binding.cbBalconyYes.isChecked = false
        }

        SpinnersHelper.occupancyList().forEachIndexed { index, item ->
            if(item.equals(propertyData.occupancy, true)) {
                binding.spinnerOccupancy.setSelection(index)

                isRentedSelected = if(item.equals("Rented", true)) {
                    binding.groupPrice.gone()
                    binding.groupRent.visible()
                    true
                } else {
                    binding.groupPrice.visible()
                    binding.groupRent.gone()
                    false
                }
            }
        }
    }

    private fun verifyData(){
        val area = binding.area.text.toString()
        val spStr = binding.sp.text.toString()
        val rentedForStr = binding.rentedFor.text.toString()

        if(REQUESDTED){

            propertyData.budget_min=binding.budgetMin.text.toString()
            propertyData.budget_max=binding.budgetMax.text.toString()
            propertyData.property_size_min=binding.propertyMin.text.toString()
            propertyData.property_size_max=binding.propertyMax.text.toString()
            propertyData.occupancy=occupancyStr
            propertyData.purchase_goal=purchaseStr
            propertyData.payment_method=paymentStr

            propertyData.number_of_checks=numberOfCheckStr
            propertyData.property_moving_time=movingTimeStr
            propertyData.property_furniture=furnitureStr



            findNavController().navigate(PostPropertyStep3FragmentDirections.actionPostPropertyStep3FragmentToPostRequestFragment(propertyData, isEdit))
        }else {
            if(spStr.isEmpty() && !isRentedSelected) {
                requireContext().showToast("Please enter a value for SP")
            } else if(rentedForStr.isEmpty() && isRentedSelected){
                requireContext().showToast("Please enter a value for Rented For")
            } else if(area.isEmpty()) {
                requireContext().showToast("Please enter Area Size")
            } else {

                propertyData.area_size = area
                propertyData.maidroom = binding.cbMaidYes.isChecked
                propertyData.balcony = binding.cbBalconyYes.isChecked
                propertyData.occupancy = occupancyStr
                if(isRentedSelected) {
                    propertyData.rented_for = binding.rentedFor.text.toString()
                    propertyData.number_of_checks = numberOfCheckStr

//                propertyData.rented_till = binding.rentedTill.text.toString()
                } else {
                    propertyData.op = binding.op.text.toString()
                    propertyData.sp = binding.sp.text.toString()
                }
                propertyData.roi = binding.roi.text.toString()



                if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Residential"){
                    findNavController().navigate(PostPropertyStep3FragmentDirections.actionPostPropertyStep3FragmentToPostPropertyStep5Fragment(propertyData, isEdit))
                }

                else if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Commercial"){
                    propertyData.fitting = fittingStr
                    findNavController().navigate(PostPropertyStep3FragmentDirections.actionPostPropertyStep3FragmentToPostPropertyStep5Fragment(propertyData, isEdit))
                }

                else {
                    findNavController().navigate(PostPropertyStep3FragmentDirections.actionPostPropertyStep3FragmentToPostPropertyStep4Fragment(propertyData, isEdit))
                }



            }
        }

    }


    fun listeners() {
        binding.nextBtn.setOnClickListener {
            if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Residential"){
                isRentedSelected=true
                verifyData()

            }

            else if(propertyData.purpose=="Rent" && propertyData.purpose_type=="Commercial"){
                isRentedSelected=true
                verifyData()

            }
            else {
                verifyData()
            }

        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

//        binding.rentedTill.setOnClickListener {
//            openCalender()
//        }
    }


    private fun selectRentAndResidents(){

        binding.clProcessBarForRent.visibility=View.VISIBLE

        binding.clProcessBar.visibility=View.INVISIBLE
        binding.fitting.visibility=View.GONE
        binding.fittingIcon.visibility=View.GONE
        binding.fittingTitle.visibility=View.GONE
        binding.rentResidentsHideGroup.visibility=View.GONE

        binding.groupRent.visibility=View.VISIBLE


    }

    private fun selectRentAndCommercial(){
        binding.propertyHideForComGroup.visibility=View.GONE
        binding.clProcessBarForRent.visibility=View.VISIBLE
        binding.clProcessBar.visibility=View.INVISIBLE
        binding.fitting.visibility=View.VISIBLE
        binding.fittingIcon.visibility=View.VISIBLE
        binding.fittingTitle.visibility=View.VISIBLE
        binding.rentResidentsHideGroup.visibility=View.GONE

        binding.groupRent.visibility=View.VISIBLE
    }
}