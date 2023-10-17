package app.unduit.a2achatapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.databinding.FragmentBuyAndCommercialBinding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep2Binding
import app.unduit.a2achatapp.fragments.PostPropertyStep1FragmentDirections
import app.unduit.a2achatapp.fragments.PostPropertyStep2FragmentArgs
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.models.PropertyData
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale


class BuyAndCommercialFragment : Fragment(),View.OnClickListener {

    private lateinit var binding: FragmentBuyAndCommercialBinding
    val args: BuyAndCommercialFragmentArgs by navArgs()

    var isEdit = false
    private lateinit var propertyData: PropertyData

    var fittingStr = "Shall & core"
    var occupancyStr = "Vacant"
    var paymentStr = "Cash"
    var purchaseStr = "Investment"
    var numberOfCheckStr = "1"
    var movingTimeStr = "1"
    var furnitureStr = "Furnished"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBuyAndCommercialBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init(){
        isEdit = args.isEdit
        propertyData = args.propertyData
        Const.screenName = ""
        listeners()
        spinnersManager()

        if(isEdit){
            binding.areaCommunity.setText(propertyData.area_community)
            binding.budgetMin.setText(propertyData.budget_min)
            binding.budgetMax.setText(propertyData.budget_max)
            binding.propertyMin.setText(propertyData.property_size_min)
            binding.propertyMax.setText(propertyData.property_size_max)


            SpinnersHelper.fittingList().forEachIndexed { index, s ->
                if(  propertyData.fitting==s){
                    binding.fitting.setSelection(index)
                }
            }

            SpinnersHelper.occupancyList1().forEachIndexed { index, s ->
                if(  propertyData.occupancy==s){
                    binding.spinnerOccupancy1.setSelection(index)
                }
            }

            SpinnersHelper.purchaseGaolList().forEachIndexed { index, s ->
                if(  propertyData.purchase_goal==s){
                    binding.spinnerPurchase.setSelection(index)
                }
            }

            SpinnersHelper.paymentList().forEachIndexed { index, s ->
                if(  propertyData.payment_method==s){
                    binding.spinnerPayment.setSelection(index)
                }
            }


            SpinnersHelper.movingTimeList().forEachIndexed { index, s ->
                if(  propertyData.property_moving_time==s){
                    binding.movingTimeSpinner.setSelection(index)
                }
            }


            SpinnersHelper.furnitureList().forEachIndexed { index, s ->
                if(  propertyData.property_furniture==s){
                    binding.furnitureSpinner.setSelection(index)
                }
            }
        }
    }

    fun listeners(){
        binding.nextBtn.setOnClickListener(this)
//        binding.nextBtn1.setOnClickListener(this)
        binding.backIcon.setOnClickListener(this)

        binding.budgetMin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(text: Editable?) {
                try {
                    binding.budgetMin.removeTextChangedListener(this)

                    var origStr = text.toString()
                    if(origStr.isNotEmpty()) {
                        if (origStr.contains(",")) {
                            origStr = origStr.replace(",", "");
                        }

                        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                        formatter.applyPattern("###,###,###,###,###")
                        val s = formatter.format(origStr.toLong())

                        binding.budgetMin.setText(s)
                        binding.budgetMin.setSelection(binding.budgetMin.text.length)
                    }
                    binding.budgetMin.addTextChangedListener(this)
                } catch (e: Exception) {
                    Log.e("TextWatch", e.message.toString())
                }
            }
        })

        binding.budgetMax.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(text: Editable?) {
                try {
                    binding.budgetMax.removeTextChangedListener(this)

                    var origStr = text.toString()
                    if(origStr.isNotEmpty()) {
                        if (origStr.contains(",")) {
                            origStr = origStr.replace(",", "");
                        }

                        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                        formatter.applyPattern("###,###,###,###,###")
                        val s = formatter.format(origStr.toLong())

                        binding.budgetMax.setText(s)
                        binding.budgetMax.setSelection(binding.budgetMax.text.length)
                    }
                    binding.budgetMax.addTextChangedListener(this)
                } catch (e: Exception) {
                    Log.e("TextWatch", e.message.toString())
                }
            }
        })
    }

    private fun spinnersManager(){
        setState()
        fittingSpinner()
        occupancySpinner1()
        paymentSpinner()
        purchaseSpinner()
        movingTimeSpinner()
        furnitureSpinner()
        numberOfCheckSpinner()

    }

    private fun setState(){
        if(propertyData.purpose=="Sale" && propertyData.purpose_type=="Commercial"){

            binding.numberOfChecks.visibility=View.GONE
            binding.numberOfChecksIcon.visibility=View.GONE
            binding.numberOfChecksTitle.visibility=View.GONE

            binding.furnitureSpinner.visibility=View.GONE
            binding.furnishIcon.visibility=View.GONE
            binding.furnishTitle.visibility=View.GONE

            binding.movingTimeIcon.visibility=View.GONE
            binding.movingTimeTitle.visibility=View.GONE
            binding.movingTimeSpinner.visibility=View.GONE

        }
        else {
            binding.numberOfChecks.visibility=View.VISIBLE
            binding.numberOfChecksIcon.visibility=View.VISIBLE
            binding.numberOfChecksTitle.visibility=View.VISIBLE

            binding.furnitureSpinner.visibility=View.VISIBLE
            binding.furnishIcon.visibility=View.VISIBLE
            binding.furnishTitle.visibility=View.VISIBLE

            binding.movingTimeIcon.visibility=View.VISIBLE
            binding.movingTimeTitle.visibility=View.VISIBLE
            binding.movingTimeSpinner.visibility=View.VISIBLE


            binding.spinnerOccupancy1.visibility=View.GONE
            binding.occupancyIcArrow1.visibility=View.GONE
            binding.occupancyTitle1.visibility=View.GONE


            binding.purchaseTitle.visibility=View.GONE
            binding.spinnerPurchase.visibility=View.GONE
            binding.purchaseIcArrow.visibility=View.GONE

            binding.paymentTitle.visibility=View.GONE
            binding.paymentIcArrow.visibility=View.GONE
            binding.spinnerPayment.visibility=View.GONE
            binding.nextBtn.visibility=View.VISIBLE
            binding.nextBtn1.visibility=View.GONE


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


    private fun validation():Boolean{
        if(binding.budgetMin.text.isEmpty()){
            Toast.makeText(requireContext(),"Please enter the Minimum value of Budget",Toast.LENGTH_LONG).show()
                return false
        }
        else if(binding.budgetMax.text.isEmpty()){
            Toast.makeText(requireContext(),"Please enter the Maximum value of Budget",Toast.LENGTH_LONG).show()
            return false
        }
        else if(binding.propertyMin.text.isEmpty()){
            Toast.makeText(requireContext(),"Please enter the Minimum value of Property Size",Toast.LENGTH_LONG).show()
            return false
        }
        else if(binding.propertyMax.text.isEmpty()){
            Toast.makeText(requireContext(),"Please enter the Maximum value of Property Size",Toast.LENGTH_LONG).show()
            return false

        }
        return true
    }
    private fun moveDataNextScreen(){
        if(validation()){
            propertyData.fitting=fittingStr
            propertyData.area_community=binding.areaCommunity.text.toString()
            propertyData.occupancy=occupancyStr
            propertyData.payment_method=paymentStr
            propertyData.purchase_goal=purchaseStr
            propertyData.budget_max=binding.budgetMax.text.toString()
            propertyData.budget_min=binding.budgetMax.text.toString()
            propertyData.property_size_max=binding.propertyMax.text.toString()
            propertyData.property_size_min=binding.propertyMin.text.toString()

            propertyData.number_of_checks=numberOfCheckStr
            propertyData.property_moving_time=movingTimeStr
            propertyData.property_furniture=furnitureStr


        findNavController().navigate(BuyAndCommercialFragmentDirections.actionBuyAndCommercialFragmentToPostRequestFragment2(propertyData,isEdit))


        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.next_btn->{
                moveDataNextScreen()
            } R.id.next_btn1->{
                moveDataNextScreen()
            }
            R.id.back_icon -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }


}