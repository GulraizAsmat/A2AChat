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
import app.unduit.a2achatapp.adapters.PropertyListAdapter
import app.unduit.a2achatapp.databinding.FragmentFavouriteBinding
import app.unduit.a2achatapp.databinding.FragmentHomeBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData


class FavouriteFragment : Fragment() ,View.OnClickListener ,AdapterListener{

    private lateinit var binding: FragmentFavouriteBinding
        var propertylist=ArrayList<PropertyData>()





    private val propertyListAdapter by lazy {
        PropertyListAdapter(requireContext(),
            this,
            propertylist)
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavouriteBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        listeners()
        recyclerViewManager()
        selectFavourite()
    }
    fun listeners(){
        binding.backIcon.setOnClickListener(this)
        binding.clMatch.setOnClickListener(this)
        binding.clFavorite.setOnClickListener(this)
        binding.clRequest.setOnClickListener(this)
    }


    fun selectFavourite(){
        binding.clFavorite.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.clRequest.setBackgroundResource(R.color.white)
        binding.clMatch.setBackgroundResource(R.color.white)
        binding.favourite.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))
        binding.request.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.match.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))


        favDummyList()
    }
    fun selectRequest(){
        binding.clFavorite.setBackgroundResource(R.color.white)
        binding.clRequest.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.clMatch.setBackgroundResource(R.color.white)
        binding.favourite.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.request.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))
        binding.match.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        reqDummyList()
    }

    fun selectMatch(){

        binding.clFavorite.setBackgroundResource(R.color.white)
        binding.clRequest.setBackgroundResource(R.color.white)
        binding.clMatch.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.favourite.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.request.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.match.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))

        matchDummyList()
    }
        fun favDummyList(){
            propertylist.clear()

            propertylist.add(PropertyData(image = R.drawable.image2, bhk = "5 BHK" , price = "AED 300 / per month" , sqft = "900 sqft" ,location="Building no# 78 Street 3 near safa park , Dubai"))
            propertylist.add(PropertyData(image = R.drawable.image3, bhk = "3 BHK" , price = "AED 200 / per month" , sqft = "750 sqft" ,location="Building no# 18 Street 51 near Al Madam  , Dubai"))
            propertylist.add(PropertyData(image = R.drawable.image, bhk = "2 BHK" , price = "AED 150 / per month" , sqft = "600 sqft" ,location="Building no# 41 Street 8 near Margham , Dubai"))
            propertylist.add(PropertyData(image = R.drawable.image2, bhk = "5 BHK" , price = "AED 300 / per month" , sqft = "900 sqft" ,location="Building no# 78 Street 3 near safa park , Dubai"))

            propertyListAdapter.notifyDataSetChanged()

        }

    fun reqDummyList(){
        propertylist.clear()
        propertylist.add(PropertyData(image = R.drawable.image3, bhk = "3 BHK" , price = "AED 200 / per month" , sqft = "750 sqft" ,location="Building no# 18 Street 51 near Al Madam  , Dubai"))
        propertylist.add(PropertyData(image = R.drawable.image, bhk = "2 BHK" , price = "AED 150 / per month" , sqft = "600 sqft" ,location="Building no# 41 Street 8 near Margham , Dubai"))
        propertylist.add(PropertyData(image = R.drawable.image2, bhk = "5 BHK" , price = "AED 300 / per month" , sqft = "900 sqft" ,location="Building no# 78 Street 3 near safa park , Dubai"))


        propertyListAdapter.notifyDataSetChanged()

    }

    fun matchDummyList(){
        propertylist.clear()
        propertylist.add(PropertyData(image = R.drawable.image3, bhk = "3 BHK" , price = "AED 200 / per month" , sqft = "750 sqft" ,location="Building no# 18 Street 51 near Al Madam  , Dubai"))


        propertyListAdapter.notifyDataSetChanged()
    }

    private fun recyclerViewManager(){

        binding.rvProperty.layoutManager =
            LinearLayoutManager(context)
        binding.rvProperty.itemAnimator = DefaultItemAnimator()
        binding.rvProperty.adapter = propertyListAdapter
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_icon->{

                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            R.id.cl_favorite->{
                selectFavourite()
            }
            R.id.cl_request->{
                selectRequest()
            }
            R.id.cl_match->{
                selectMatch()
            }
        }
    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToPropertyDetailFragment(
            propertylist[position].image))
    }
}