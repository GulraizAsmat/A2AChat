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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.BathroomItemAdapter
import app.unduit.a2achatapp.adapters.BedroomItemAdapter
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep3Binding
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType


class PostPropertyStep3Fragment : Fragment(){


    private lateinit var binding: FragmentPostPropertyStep3Binding








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

        binding.nextBtn.setOnClickListener {

            findNavController().navigate(R.id.action_postPropertyStep3Fragment_to_postPropertyStep4Fragment)
        }
    }

    fun init(){

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


        byDefaultData()
        maidCheckManager()
        balconyCheckManager()
        spinnerManager()

    }


    private fun byDefaultData(){
        binding.cbMaidNo.isChecked=true
        binding.cbBalconyNo.isChecked=true
    }


    private fun spinnerManager() {
        occupancySpinner()
        finishingSpinner()

    }

    private fun occupancySpinner(){


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
                    // Handle the selected item here
                ""

                    // Do something with the selected data, like displaying it or performing an action
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun finishingSpinner(){


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
                    // Handle the selected item here
                ""

                    // Do something with the selected data, like displaying it or performing an action
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun maidCheckManager(){

        binding.cbMaidYes.setOnCheckedChangeListener { _, b ->

            binding.cbMaidNo.isChecked = !b

        }

        binding.cbMaidNo.setOnCheckedChangeListener { _, b ->

            binding.cbMaidYes.isChecked = !b

        }



    }

    private fun balconyCheckManager(){

        binding.cbBalconyYes.setOnCheckedChangeListener { _, b ->

            binding.cbBalconyNo.isChecked = !b

        }

        binding.cbBalconyNo.setOnCheckedChangeListener { _, b ->

            binding.cbBalconyYes.isChecked = !b

        }



    }





    fun listeners(){

    }




}