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
import app.unduit.a2achatapp.adapters.BathroomItemAdapter
import app.unduit.a2achatapp.adapters.BedroomItemAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep1Binding
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep2Binding
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.BathBedType


class PostPropertyStep2Fragment : Fragment(),View.OnClickListener, AdapterListener {

    private var bedRoomList=ArrayList<BathBedType>()
    private var bedRoomPreviousPosition=0

    var bathRoomList=ArrayList<BathBedType>()
    var bathRoomPreviousPosition=0

    var developmentStatus=false



    private val bedRoomAdapter by lazy {
        BedroomItemAdapter(requireContext(),
            this,
            bedRoomList)
    }
    private val bathRoomAdapter by lazy {
        BathroomItemAdapter(requireContext(),
            this,
            bathRoomList)
    }


    private lateinit var binding: FragmentPostPropertyStep2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep2Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }


    fun init(){
        listeners()
        bedRecyclerViewManager()
        bathRecyclerViewManager()
        bedRoomData()
        bathRoomData()
    }


    private fun bedRoomData() {
        bedRoomPreviousPosition=0
        bedRoomList.clear()
        bedRoomList.add(BathBedType("Studio", true))
        bedRoomList.add(BathBedType("1", false))
        bedRoomList.add(BathBedType("2", false))
        bedRoomList.add(BathBedType("3", false))
        bedRoomList.add(BathBedType("4", false))
        bedRoomList.add(BathBedType("5", false))
        bedRoomList.add(BathBedType("6", false))
        bedRoomList.add(BathBedType("7", false))
        bedRoomList.add(BathBedType("8+", false))
        bedRoomAdapter.notifyDataSetChanged()

    }
    private fun bathRoomData() {
        bathRoomList.clear()
        bathRoomPreviousPosition=0
        bathRoomList.add(BathBedType("1", true))
        bathRoomList.add(BathBedType("2", false))
        bathRoomList.add(BathBedType("3", false))
        bathRoomList.add(BathBedType("4", false))
        bathRoomList.add(BathBedType("5", false))
        bathRoomList.add(BathBedType("6", false))
        bathRoomList.add(BathBedType("7+", false))

        bathRoomAdapter.notifyDataSetChanged()

    }

    private fun bedRecyclerViewManager(){

        binding.rvBed.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBed.itemAnimator = DefaultItemAnimator()
        binding.rvBed.adapter = bedRoomAdapter
    }
    private fun bathRecyclerViewManager(){

        binding.rvBath.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBath.itemAnimator = DefaultItemAnimator()
        binding.rvBath.adapter = bathRoomAdapter
    }



    private fun selectOffPlan(){
        binding.clOffPlan.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.residentsTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))

        binding.clReady.setBackgroundResource(R.drawable.ic_light_purple_bg_1)

        binding.commercialTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_purple_shade_1))
        developmentStatus=false
    }
    private fun selectReady(){
        binding.clOffPlan.setBackgroundResource(R.drawable.ic_light_purple_bg_1)

        binding.residentsTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_purple_shade_1))

        binding.clReady.setBackgroundResource(R.drawable.ic_dark_purple_bg)

        binding.commercialTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))

        developmentStatus=true
    }


    fun spinnerData(){

    }
    fun listeners(){
        binding.nextBtn.setOnClickListener(this)
        binding.clReady.setOnClickListener(this)
        binding.clOffPlan.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.next_btn ->{
                findNavController().navigate(R.id.action_postFragment_to_postPropertyStep3Fragment)
            }

            R.id.cl_off_plan->{
                selectOffPlan()
            }
            R.id.cl_ready->{
                selectReady()
            }
        }
    }
    override fun onAdapterItemClicked(key: String, position: Int) {
        when(key){
            "click_on_bedroom_item"->{

                bedRoomList[bedRoomPreviousPosition].selected=false

                if(!bedRoomList[position].selected){

                    bedRoomList[position].selected=true
                    bedRoomPreviousPosition=position
                }
                bedRoomAdapter.notifyDataSetChanged()
            }

            "click_on_bathroom_item"->{
                bathRoomList[bathRoomPreviousPosition].selected=false

                if(!bathRoomList[position].selected){

                    bathRoomList[position].selected=true
                    bathRoomPreviousPosition=position
                }
                bathRoomAdapter.notifyDataSetChanged()
            }
        }
    }

}