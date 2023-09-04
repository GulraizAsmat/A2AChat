package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.HomeSwiperAdapter
import app.unduit.a2achatapp.databinding.FragmentHomeBinding
import app.unduit.a2achatapp.helpers.Const

import app.unduit.a2achatapp.listeners.AdapterListener

import com.yuyakaido.android.cardstackview.*



class HomeFragment : Fragment() ,View.OnClickListener, CardStackListener,AdapterListener {


    private lateinit var binding:FragmentHomeBinding
    var homeSwipeList=ArrayList<Int>()

    private val cardStackView by lazy { requireActivity().findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(requireContext(), this) }



    private val homeSliderAdapter by lazy {
        HomeSwiperAdapter(requireContext(),
            this,
            homeSwipeList)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    init()
    }

    override fun onResume() {
        super.onResume()
        sliderManager()
    }

    private fun init(){
        Log.e("Tag2345","init :: ")
        listeners()
        sliderManager()
    }

    private fun listeners(){
        binding.favouriteIcon.setOnClickListener(this)
        binding.notificationIcon.setOnClickListener(this)
        binding.profileImage.setOnClickListener(this)
        binding.propertyList.setOnClickListener(this)
        binding.addProperty.setOnClickListener(this)
        binding.chat.setOnClickListener(this)

    }

    private fun sliderManager() {
        Const.screenName=""

        manager.setStackFrom(StackFrom.Bottom)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = homeSliderAdapter

    }



    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.favourite_icon->{
                Const.screenName="favourite_icon"
                findNavController().navigate(R.id.action_homeFragment_to_favouriteFragment)

            }
            R.id.notification_icon->{
                Const.screenName="notification_icon"
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }
            R.id.profile_image->{
                Const.screenName="profile_image"
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }
            R.id.property_list->{
                Const.screenName="property_list"
                findNavController().navigate(R.id.action_homeFragment_to_propertyListFragment)
            }
            R.id.add_property->{
                Const.screenName="add_property"
                findNavController().navigate(R.id.action_homeFragment_to_propertyBottomSheetFragment)
            }
            R.id.chat->{
                Const.screenName="chat"
                findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
            }



        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.e("Tag2345","onCardDragging :: "+ direction!!.name)

    }

    override fun onCardSwiped(direction: Direction?) {
        Log.e("Tag2345","onCardSwiped ::" )
    }

    override fun onCardRewound() {
        Log.e("Tag2345","onCardRewound :: ")

    }

    override fun onCardCanceled() {
        Log.e("Tag2345","onCardCanceled :: ")

    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.e("Tag2345","onCardAppeared :: ")

    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.e("Tag2345","onCardDisappeared :: ")

    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        TODO("Not yet implemented")
    }


}