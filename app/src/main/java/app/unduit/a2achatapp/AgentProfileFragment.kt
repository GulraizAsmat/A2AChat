package app.unduit.a2achatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.adapters.AgentPropertiesAdapter
import app.unduit.a2achatapp.adapters.AgentRequestAdapter
import app.unduit.a2achatapp.adapters.PropertyFiltersAdapter
import app.unduit.a2achatapp.databinding.FragmentAgentProfileBinding
import app.unduit.a2achatapp.databinding.FragmentPropertyDetailBinding
import app.unduit.a2achatapp.fragments.PropertyDetailFragmentArgs
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AgentProfileFragment : Fragment() ,AdapterListener,View.OnClickListener{
        var TAG="AgentProfileFragment"
    private val args: PropertyDetailFragmentArgs by navArgs()

    var userid:String=""
    private var propertyData: PropertyData? = null


    private lateinit var binding: FragmentAgentProfileBinding


    var propertylist = ArrayList<PropertyData>()
    var requestList = ArrayList<PropertyData>()
    var picUrl=""

    private val agentRequestAdapter by lazy {
        AgentRequestAdapter(
            requireContext(),
            this,
            requestList
        )
    }
    private val agentPropertiesAdapter by lazy {
        AgentPropertiesAdapter(
            requireContext(),
            this,
            propertylist
        )
    }


    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAgentProfileBinding.inflate(inflater)
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
        recyclerViewManager()
    }

    fun listeners(){
        binding.requestViewAll.setOnClickListener(this)
        binding.propertiesViewAll.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.userImage.setOnClickListener(this)
    }


    private fun defaultData(){
        propertyData = args.propertyData
        userDetails()
    }


    private fun recyclerViewManager(){
        requestRecyclerView()
        propertyRecyclerView()
    }

    private fun requestRecyclerView(){

        binding.rvRequest.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRequest.itemAnimator = DefaultItemAnimator()
        binding.rvRequest.adapter = agentRequestAdapter

    }


    private fun propertyRecyclerView(){

        binding.rvProperties.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvProperties.itemAnimator = DefaultItemAnimator()
        binding.rvProperties.adapter = agentPropertiesAdapter

    }





    private fun userDetails(){



        binding.brn.text = propertyData!!.brn

        if(propertyData!!.verified){
            binding.verifiedIcon.visibility=View.VISIBLE
        }


        if(propertyData!!.user_id== Const.userId){

            binding.userName.text= propertyData!!.sender_name
            binding.companyName.text= propertyData!!.sender_company
            picUrl=propertyData!!.sender_image
            Glide.with(this).load(propertyData!!.sender_image)
                .fallback(R.drawable.ic_deafult_profile_icon)
                .placeholder(R.drawable.ic_deafult_profile_icon)
                .into(binding.userImage)

            getUserProperties(propertyData!!.sender_id)
            userid=propertyData!!.sender_id



        }else {


            binding.userName.text= propertyData!!.user_name
            binding.companyName.text= propertyData!!.user_company
            picUrl= propertyData!!.user_picture.toString()
            Glide.with(this).load(propertyData!!.user_picture)
                .fallback(R.drawable.ic_deafult_profile_icon)
                .placeholder(R.drawable.ic_deafult_profile_icon)
                .into(binding.userImage)
            getUserProperties(propertyData!!.user_id.toString())

            userid= propertyData!!.user_id.toString()
        }



    }


    private fun getUserProperties(userId:String){



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
                requestList.clear()
                requestList.addAll(propertylist.filter { it.post_type=="request" }.sortedByDescending { it.created_date })



           val propertyFilterList=     propertylist.filter { it.post_type!="request" }.sortedByDescending { it.created_date }
                propertylist.clear()
                propertylist.addAll(propertyFilterList)

            if(requestList.isNotEmpty()){
                binding.clViewAllRequest.visibility=View.VISIBLE
            }

                if(propertylist.isNotEmpty()){
                    binding.clViewAllProperties.visibility=View.VISIBLE
                }
                binding.requestsCounts.text="(${requestList.size})"
                binding.propertiesCounts.text="(${propertylist.size})"

//                propertyListAdapter.notifyDataSetChanged()
                Log.e(TAG, "propertylist size => ${propertylist.size}")

                agentRequestAdapter.notifyDataSetChanged()
                agentPropertiesAdapter.notifyDataSetChanged()

                progressDialog.progressBarVisibility(false)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                requireContext().showToast("An error occurred. Please try again later")
                progressDialog.progressBarVisibility(false)
            }




    }

    override fun onAdapterItemClicked(key: String, position: Int) {

        when(key){
            "view_detail_property"->{

                findNavController().navigate(AgentProfileFragmentDirections.actionAgentProfileFragmentToPropertyDetailFragment(propertylist[position],""))
            }
            "view_detail_request"->{

                findNavController().navigate(AgentProfileFragmentDirections.actionAgentProfileFragmentToPropertyDetailFragment(requestList[position],""))
            }

        }

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.request_view_all->{
                findNavController().navigate(AgentProfileFragmentDirections.actionAgentProfileFragmentToAgentPropertiesViewAllFragment("request",userid))

            }
            R.id.properties_view_all->{
                findNavController().navigate(AgentProfileFragmentDirections.actionAgentProfileFragmentToAgentPropertiesViewAllFragment("property",userid))

            }
            R.id.btn_back->{
                requireActivity().onBackPressed()
            }
            R.id.user_image->{
                    findNavController().navigate(AgentProfileFragmentDirections.actionAgentProfileFragmentToUserProfileFullViewFragment(picUrl))
            }
        }
    }
}


