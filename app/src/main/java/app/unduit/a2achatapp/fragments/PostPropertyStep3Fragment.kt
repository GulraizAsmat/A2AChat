package app.unduit.a2achatapp.fragments

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
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType
import app.unduit.a2achatapp.models.PropertyData


class PostPropertyStep3Fragment : Fragment() {


    private lateinit var binding: FragmentPostPropertyStep3Binding

    private val args: PostPropertyStep3FragmentArgs by navArgs()

    private lateinit var propertyData: PropertyData
    var isEdit = false

    var occupancyStr = "Vacant"
    var furnishingStr = "Unfurnished"

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
        finishingSpinner()

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
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun finishingSpinner() {


        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.finishingList())

        binding.spinnerFinishing.adapter = adapter
        binding.spinnerFinishing.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.finishingList()[position]
                    furnishingStr = selectedItem
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

    private fun setData() {
        binding.area.setText(propertyData.area_size)

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
            }
        }

        SpinnersHelper.finishingList().forEachIndexed { index, item ->
            if(item.equals(propertyData.furnishing, true)) {
                binding.spinnerFinishing.setSelection(index)
            }
        }
    }

    private fun verifyData(){
        val area = binding.area.text.toString()
        if(area.isEmpty()) {
            requireContext().showToast("Please enter Area Size")
        } else {

            propertyData.area_size = area
            propertyData.maidroom = binding.cbMaidYes.isChecked
            propertyData.balcony = binding.cbBalconyYes.isChecked
            propertyData.occupancy = occupancyStr
            propertyData.furnishing = furnishingStr

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
    }

}