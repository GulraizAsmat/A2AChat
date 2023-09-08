package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.HomeSwiperAdapter
import app.unduit.a2achatapp.adapters.PropertyTypeItemAdapter
import app.unduit.a2achatapp.databinding.FragmentHomeBinding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep1Binding
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyType


class PostPropertyStep1Fragment : Fragment() , View.OnClickListener,AdapterListener{

    
    
    var salePurpose=true // forSale-> true , forRent -> false
    var propertyType=true //forResidents-> true , forCommercial -> false

    var propertyItemList=ArrayList<PropertyType>()
    var previousPosition=0


    private val propertyTypeAdapter by lazy {
        PropertyTypeItemAdapter(requireContext(),
            this,
            propertyItemList)
    }


    private lateinit var binding: FragmentPostPropertyStep1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep1Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }
    
    fun init(){

        recyclerViewManager()
        showResidentProperty()
        listeners()
    }
    
    fun listeners(){
        binding.clCommercial.setOnClickListener(this)
        binding.clForRent.setOnClickListener(this)
        binding.clForSale.setOnClickListener(this)
        binding.clResident.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
    }


    private fun recyclerViewManager(){

        binding.rvProperty.layoutManager =
            LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        binding.rvProperty.itemAnimator = DefaultItemAnimator()
        binding.rvProperty.adapter = propertyTypeAdapter
    }

    private fun selectForSaleOption(){
        binding.clForSale.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.clForRent.setBackgroundResource(R.drawable.ic_light_purple_bg_1)
        salePurpose=true
      
    }
    
    private fun selectForRentOption(){
        binding.clForSale.setBackgroundResource(R.drawable.ic_light_purple_bg_1)
        binding.clForRent.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        salePurpose=false
    }
    
    private fun selectResidents(){
        binding.clResident.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.residentIcon.setImageResource(R.drawable.ic_resident)
        binding.residentsTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))

        binding.clCommercial.setBackgroundResource(R.color.color_white_shade_1)
        binding.commercialIcon.setImageResource(R.drawable.ic_commercial)
        binding.commercialTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        propertyType=true
    }
    private fun selectCommercial(){
        binding.clResident.setBackgroundResource(R.color.color_white_shade_1)
        binding.residentIcon.setImageResource(R.drawable.ic_resident_purple)
        binding.residentsTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))

        binding.clCommercial.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.commercialIcon.setImageResource(R.drawable.ic_commercial_white)
        binding.commercialTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))

        propertyType=false
    }



    private fun showResidentProperty(){
        previousPosition=0
        propertyItemList.clear()

        propertyItemList.add(PropertyType(name="House", image =R.drawable.ic_resident_purple , selected = true))
        propertyItemList.add(PropertyType(name="Apartment", image =R.drawable.ic_apartment  , selected = false))
        propertyItemList.add(PropertyType(name="Villa", image = R.drawable.ic_villa , selected = false))
        propertyItemList.add(PropertyType(name="Penthouse", image = R.drawable.ic_penthouse , selected = false))

        propertyTypeAdapter.notifyDataSetChanged()
    }

    private fun showCommercialProperty(){
        previousPosition=0
        propertyItemList.clear()

        propertyItemList.add(PropertyType(name="Office", image =R.drawable.ic_office , selected = true))
        propertyItemList.add(PropertyType(name="Shop", image =R.drawable.ic_warehouse  , selected = false))
        propertyItemList.add(PropertyType(name="Warehouse", image = R.drawable.ic_shop , selected = false))
        propertyItemList.add(PropertyType(name="labour Camp", image = R.drawable.ic_labour_camp , selected = false))
        propertyTypeAdapter.notifyDataSetChanged()

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.cl_for_sale->{
                selectForSaleOption()
            }
            R.id.cl_for_rent->{
                selectForRentOption()
            }
            
            R.id.cl_resident->{
              selectResidents()
                showResidentProperty()
            }
            R.id.cl_commercial->{
                selectCommercial()
                showCommercialProperty()
            }
            R.id.next_btn->{
                findNavController().navigate(R.id.action_postPropertyStep1Fragment_to_postFragment)
            }
            
            
            
        }
    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        when(key){
            "click_on_item"->{

                propertyItemList[previousPosition].selected=false

                    if(!propertyItemList[position].selected){
                        propertyItemList[position].selected=true
                        previousPosition=position
                    }

                propertyTypeAdapter.notifyDataSetChanged()

            }
        }
    }

}