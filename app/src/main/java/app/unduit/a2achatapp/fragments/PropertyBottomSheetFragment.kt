package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentPropertyBottomSheetBinding
import app.unduit.a2achatapp.helpers.Const


class PropertyBottomSheetFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentPropertyBottomSheetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPropertyBottomSheetBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    fun init(){
        Const.screenName="add_property"
        listeners()
    }
    fun listeners(){
        binding.clBottomSheet.setOnClickListener(this)
        binding.closeBottomSheet.setOnClickListener(this)
        binding.postProperty.setOnClickListener(this)
        binding.postRequest.setOnClickListener(this)
        binding.bulkUpload.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.cl_bottom_sheet -> {

                requireActivity().onBackPressed()

            }
            R.id.close_bottom_sheet -> {

            requireActivity().onBackPressed()

            }
            R.id.post_property -> {
                Const.screenName=""
                findNavController().navigate(R.id.action_propertyBottomSheetFragment_to_postPropertyStep1Fragment)

            }
            R.id.post_request -> {


            }
            R.id.bulk_upload -> {


            }


        }
    }

}