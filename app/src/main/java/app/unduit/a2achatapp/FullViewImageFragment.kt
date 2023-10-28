package app.unduit.a2achatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.databinding.FragmentFullViewImageBinding
import app.unduit.a2achatapp.databinding.FragmentPropertyDetailBinding
import app.unduit.a2achatapp.fragments.PropertyDetailFragmentArgs
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.models.PropertyData
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class FullViewImageFragment : Fragment() {

    private val args: FullViewImageFragmentArgs by navArgs()

    private var propertyData: PropertyData? = null

    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentFullViewImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFullViewImageBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        Const.screenName=""
        propertyData = args.propertyData



        val imageSliderList = ArrayList<SlideModel>()
        propertyData!!.property_images?.forEach { image ->
            imageSliderList.add(SlideModel(image, ScaleTypes.CENTER_INSIDE))
        }

        binding.ivPropertySlider.setImageList(imageSliderList)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


}