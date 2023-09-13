package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep4Binding
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.models.PropertyData

class PostPropertyStep4Fragment : Fragment() {

    private lateinit var binding: FragmentPostPropertyStep4Binding

    private val args: PostPropertyStep4FragmentArgs by navArgs()

    private lateinit var propertyData: PropertyData
    private var isEdit = false

    var floorStr = "Low"
    var handoverYearStr = "2010"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep4Binding.inflate(inflater)
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

        spinnerManager()

        if(isEdit) {
            setData()
        }
    }

    fun listeners() {
        binding.nextBtn.setOnClickListener {
            setPropertyData()
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun spinnerManager() {

        floorSpinner()
        handOverPaymentYearSpinner()
    }


    private fun floorSpinner() {


        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.floorList())

        binding.spinnerFloor.adapter = adapter
        binding.spinnerFloor.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.floorList()[position]
                    floorStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun handOverPaymentYearSpinner() {


        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.yearList())

        binding.paymentPlanSpinner.adapter = adapter
        binding.paymentPlanSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.yearList()[position]
                    handoverYearStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun setData() {
        binding.serviceCharge.setText(propertyData.service_charge)
        binding.duringConstruction.setText(propertyData.payment_during_construction)
        binding.onHandover.setText(propertyData.payment_on_handover)
        binding.postHandover.setText(propertyData.payment_post_handover)

        SpinnersHelper.floorList().forEachIndexed { index, item ->
            if(item.equals(propertyData.floor, true)) {
                binding.spinnerFloor.setSelection(index)
            }
        }

        SpinnersHelper.yearList().forEachIndexed { index, item ->
            if(item.equals(propertyData.handover_year, true)) {
                binding.paymentPlanSpinner.setSelection(index)
            }
        }
    }

    private fun setPropertyData() {

        propertyData.service_charge = binding.serviceCharge.text.toString()
        propertyData.floor = floorStr
        propertyData.handover_year = handoverYearStr
        propertyData.payment_during_construction = binding.duringConstruction.text.toString()
        propertyData.payment_on_handover = binding.onHandover.text.toString()
        propertyData.payment_post_handover = binding.postHandover.text.toString()

        findNavController().navigate(PostPropertyStep4FragmentDirections.actionPostPropertyStep4FragmentToPostPropertyStep5Fragment(propertyData, isEdit))
    }

}










