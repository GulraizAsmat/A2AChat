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
    }


    private fun byDefaultData() {
        binding.cbMaidNo.isChecked = true
        binding.cbBalconyNo.isChecked = true
    }


    private fun spinnerManager() {
        occupancySpinner()
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
            binding.rentedTill.setText("$dayOfMonth/$monthOfYear/$year")

        }, year, month, day)

        dpd.show()
    }

    private fun setData() {
        binding.area.setText(propertyData.area_size)
        binding.op.setText(propertyData.op)
        binding.sp.setText(propertyData.sp)
        binding.roi.setText(propertyData.roi)
        binding.rentedFor.setText(propertyData.rented_for)
        binding.rentedTill.setText(propertyData.rented_till)

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
                propertyData.rented_till = binding.rentedTill.text.toString()
            } else {
                propertyData.op = binding.op.text.toString()
                propertyData.sp = binding.sp.text.toString()
            }
            propertyData.roi = binding.roi.text.toString()

            findNavController().navigate(PostPropertyStep3FragmentDirections.actionPostPropertyStep3FragmentToPostPropertyStep4Fragment(propertyData, isEdit))
        }
    }


    fun listeners() {
        binding.nextBtn.setOnClickListener {
            verifyData()
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rentedTill.setOnClickListener {
            openCalender()
        }
    }

}