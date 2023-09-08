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
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep5Binding
import app.unduit.a2achatapp.helpers.SpinnersHelper


class PostPropertyStep5Fragment : Fragment() {
    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentPostPropertyStep5Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep5Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }
    override


    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {

        spinnerManager()
        binding.postListBtn.setOnClickListener {

            findNavController().navigate(R.id.action_postPropertyStep5Fragment_to_homeFragment)
        }
    }

    private fun spinnerManager() {
        negotiationSpinner()
    }



    private fun negotiationSpinner(){


        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.negotiationList())

        binding.spinnerNegotiation.adapter = adapter
        binding.spinnerNegotiation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.negotiationList()[position]
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