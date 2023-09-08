package app.unduit.a2achatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.adapters.UserExperienceAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep3Binding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep4Binding
import app.unduit.a2achatapp.helpers.SpinnersHelper

class PostPropertyStep4Fragment : Fragment() {

    private lateinit var binding: FragmentPostPropertyStep4Binding

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

        binding.nextBtn.setOnClickListener {

            findNavController().navigate(R.id.action_postPropertyStep4Fragment_to_postPropertyStep5Fragment)
        }

        init()
    }


    fun init() {

        spinnerManager()
    }

    private fun spinnerManager() {

        floorSpinner()
        handOverPaymentYearSpinner()
    }



    private fun floorSpinner(){


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
                    // Handle the selected item here
                ""

                    // Do something with the selected data, like displaying it or performing an action
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }
    private fun handOverPaymentYearSpinner(){


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
                    // Handle the selected item here
                ""

                    // Do something with the selected data, like displaying it or performing an action
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

}










