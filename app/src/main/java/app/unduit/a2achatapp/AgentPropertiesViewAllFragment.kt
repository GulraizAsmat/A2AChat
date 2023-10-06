package app.unduit.a2achatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.adapters.AgentRequestBuySaleAdapter
import app.unduit.a2achatapp.adapters.AgentPropertiesAdapter
import app.unduit.a2achatapp.adapters.AgentPropertiesBuySaleAdapter
import app.unduit.a2achatapp.databinding.FragmentAgentPropertiesViewAllBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AgentPropertiesViewAllFragment : Fragment(),View.OnClickListener,AdapterListener {

    var TAG="AgentPropertiesViewAllFragment"
    private val args: AgentPropertiesViewAllFragmentArgs by navArgs()

    private var propertyData: PropertyData? = null

    var propertylist = ArrayList<PropertyData>()
    var requestList = ArrayList<PropertyData>()
    var filterList = ArrayList<PropertyData>()



    private val agentRequestBuySaleAdapter by lazy {
        AgentRequestBuySaleAdapter(
            requireContext(),
            this,
            filterList
        )
    }
    private val agentPropertiesAdapter by lazy {
        AgentPropertiesBuySaleAdapter(
            requireContext(),
            this,
            filterList
        )
    }
    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }



    private lateinit var binding: FragmentAgentPropertiesViewAllBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAgentPropertiesViewAllBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    fun init(){
        Const.screenName=""
        defaultData()
        listeners()
    }
    private fun defaultData(){

        selectType(args.postType)
        recyclerViewManager()
        getUserProperties(args.userId)

    }

    fun listeners(){
        binding.clSelector1.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.clSelector2.setOnClickListener(this)
    }

    private fun selectType(postType:String){

        if(postType=="request"){
            binding.screenTitle.text="All Requests"
            binding.buyTitle.text="Buy"






        }
        else {
            binding.screenTitle.text="All Properties"
            binding.buyTitle.text="Sale"

        }


    }


    private fun recyclerViewManager(){
        requestRecyclerView()

    }

    private fun requestRecyclerView(){

        binding.rvProperties.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvProperties.itemAnimator = DefaultItemAnimator()

        if(args.postType=="request"){
            binding.rvProperties.adapter = agentRequestBuySaleAdapter
        }
        else {
            binding.rvProperties.adapter = agentPropertiesAdapter
        }


    }





    private fun selectFirst(){
        filterList.clear()
        binding.clSelector1.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)
        binding.clSelector2.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)

        binding.buyTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_white_shade_1
            )
        )
        binding.rentTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_black_shade_1
            )
        )


        if(args.postType=="request"){
            filterList.addAll(requestList.filter { it.purpose=="Sale" })
            agentRequestBuySaleAdapter.notifyDataSetChanged()

        }
        else {
            filterList.addAll(propertylist.filter { it.purpose=="Sale" })
            agentPropertiesAdapter.notifyDataSetChanged()
        }
    }

    private fun selectSecond(){
        filterList.clear()
        binding.clSelector1.setBackgroundResource(R.drawable.aa_ic_rounded_corner_white)
        binding.clSelector2.setBackgroundResource(R.drawable.aa_ic_rounded_purple_light)

        binding.buyTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_black_shade_1
            )
        )
        binding.rentTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_white_shade_1
            )
        )

        if(args.postType=="request"){
            filterList.addAll(requestList.filter { it.purpose=="Rent" })
            agentRequestBuySaleAdapter.notifyDataSetChanged()
        }
        else {
            filterList.addAll(propertylist.filter { it.purpose=="Rent" })
            agentPropertiesAdapter.notifyDataSetChanged()
        }
    }


    private fun getUserProperties(userId:String){

        filterList.clear()

        Log.e("Tag213", "user id :$userId")
        val db = Firebase.firestore
        val ref = db.collection("properties").whereEqualTo("user_id",userId )

        ref.get()
            .addOnSuccessListener { documents ->
                propertylist.clear()
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    propertylist.add(document.toObject(PropertyData::class.java))
                }

                if(args.postType=="request"){
                    requestList.clear()
                    requestList.addAll(propertylist.filter { it.post_type=="request" }.sortedByDescending { it.created_date })

                    filterList.addAll(requestList.filter { it.purpose=="Sale" })

                    agentRequestBuySaleAdapter.notifyDataSetChanged()

                }else {
                    val propertyFilterList=     propertylist.filter { it.post_type!="request" }.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(propertyFilterList)

                    filterList.addAll(propertylist.filter { it.purpose=="Sale" })
                    agentPropertiesAdapter.notifyDataSetChanged()
                }










                progressDialog.progressBarVisibility(false)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                requireContext().showToast("An error occurred. Please try again later")
                progressDialog.progressBarVisibility(false)
            }




    }

    override fun onClick(v: View?) {
            when(v!!.id){
                R.id.cl_selector_1->{
                    selectFirst()
                }
                R.id.cl_selector_2->{
                    selectSecond()

                }
                R.id.btn_back->{
                    requireActivity().onBackPressed()
                }


            }
    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        when(key){
            "view_detail_property"->{
                findNavController().navigate(AgentPropertiesViewAllFragmentDirections.actionAgentPropertiesViewAllFragmentToPropertyDetailFragment(propertylist[position]))
            }
            "view_detail_request"->{
                findNavController().navigate(AgentPropertiesViewAllFragmentDirections.actionAgentPropertiesViewAllFragmentToPropertyDetailFragment(requestList[position]))

            }
        }
    }

}