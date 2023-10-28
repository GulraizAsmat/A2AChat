package app.unduit.a2achatapp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FavouriteFragment : Fragment() ,View.OnClickListener ,AdapterListener{
    private val TAG = "FavouriteFragment"
    private lateinit var binding: FragmentFavouriteBinding


        var propertylist=ArrayList<PropertyData>()

    private lateinit var auth: FirebaseAuth
        private var propertyType:String=""

    private val progressDialog by lazy {

        ProgressDialog(requireContext())
    }


    private val propertyListAdapter by lazy {
        PropertyListAdapter(requireContext(),
            this,
            propertylist,
            propertyType)
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
        setScreenState()
    }


    private fun setScreenState(){
        when (requireArguments().getString("screen_type")) {
            "favourite" -> {
                selectFavourite()
            }
            "request" -> {
                selectRequest()
            }
            else -> {
            selectMatch()
            }
        }
    }
    fun listeners(){
        binding.backIcon.setOnClickListener(this)
        binding.clMatch.setOnClickListener(this)
        binding.clFavorite.setOnClickListener(this)
        binding.clRequest.setOnClickListener(this)
    }


    private fun selectFavourite(){
        propertylist.clear()


            Const.PropertyType="favourite"
        binding.clFavorite.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.clRequest.setBackgroundResource(R.color.white)
        binding.clMatch.setBackgroundResource(R.color.white)
        binding.favourite.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))
        binding.request.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.match.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        getFavouriteData()


    }
    private fun selectRequest(){
        propertylist.clear()
//            propertyListAdapter.setStatus("request")
        Const.PropertyType="request"
        binding.clFavorite.setBackgroundResource(R.color.white)
        binding.clRequest.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.clMatch.setBackgroundResource(R.color.white)
        binding.favourite.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.request.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))
        binding.match.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))

        getRequestData()
    }

    private fun selectMatch(){
        propertylist.clear()
        Const.PropertyType="match"
        binding.clFavorite.setBackgroundResource(R.color.white)
        binding.clRequest.setBackgroundResource(R.color.white)
        binding.clMatch.setBackgroundResource(R.drawable.ic_dark_purple_bg)
        binding.favourite.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.request.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_black_shade_1))
        binding.match.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white_shade_1))
        getMatchData()
    }


    private fun recyclerViewManager(){

        binding.rvProperty.layoutManager =
            LinearLayoutManager(context)
        binding.rvProperty.itemAnimator = DefaultItemAnimator()
        binding.rvProperty.adapter = propertyListAdapter
    }



    private fun getRequestData(){

        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("requests/${cUser.uid}/posts").whereEqualTo("property_status","sender")

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }

                    val list =propertylist.sortedByDescending { it.created_date }
                            propertylist.clear()
                            propertylist.addAll(list)

                    if(propertylist.isEmpty()){
                        binding.clEmpty.visibility=View.VISIBLE
                    }else {
                        binding.clEmpty.visibility=View.GONE
                    }
                    propertyListAdapter.notifyDataSetChanged()

                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }




    }


    private fun getFavouriteData(){

        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("favourites/${cUser.uid}/posts")

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }
                    propertyType="favourite"
                    Log.e("Tag2135","getFavouriteData")
                    val list =propertylist.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(list)

                    if(propertylist.isEmpty()){
                        binding.clEmpty.visibility=View.VISIBLE
                    }else {
                        binding.clEmpty.visibility=View.GONE
                    }

                    propertyListAdapter.notifyDataSetChanged()
                    Log.d(TAG, "propertylist size => ${propertylist.size}")
                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }




    }

    private fun getMatchData(){

        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("requests/${cUser.uid}/posts").whereEqualTo("property_status","matched")

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }
                    val list =propertylist.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(list)


                    if(propertylist.isEmpty()){
                        binding.clEmpty.visibility=View.VISIBLE
                    }else {
                        binding.clEmpty.visibility=View.GONE
                    }
                    propertyListAdapter.notifyDataSetChanged()

                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }




    }


    private fun unFavourites(position: Int){

        progressDialog.progressBarVisibility(true)
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {

            val collectionReference = db.collection("favourites").document(currentUser.uid).collection("posts").document(propertylist[position].uid)

            collectionReference.delete()
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e(TAG,"Delete")
                    progressDialog.progressBarVisibility(false)
                propertylist.removeAt(position)
                    propertyListAdapter.notifyItemRemoved(position)
                    requireContext().showToast("Property successfully removed from favorites.")

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e(TAG, "Fail$e")
                }
        }

    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_icon->{

                requireActivity().onBackPressed()
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


    fun phoneCall(number:String){


        // Check if there is an app available to handle the intent
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
    override fun onAdapterItemClicked(key: String, position: Int) {
        when(key){
            "un_favourite"->{
                unFavourites(position)
            }
            "match"->{
                findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToPropertyDetailFragment(propertylist[position], "matches"))

            }
            "phone_call"->{
                if (Const.userId==propertylist[position].user_id){
                    makePhoneCall(propertylist[position].sender_phone)
                }else {
                    makePhoneCall(propertylist[position].user_phone)
                }

            }
            "whatsapp"->{
                if (Const.userId==propertylist[position].user_id){
                    whatsapp(propertylist[position].sender_whatsapp)
                }else {
                    whatsapp(propertylist[position].user_whatsapp)
                }

            }
            "chat"->{
                findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToChatFragment())



            }
            else ->{
                findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToPropertyDetailFragment(propertylist[position], "favourite"))

            }
        }

         }



    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    private fun whatsapp(phoneNumber: String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/$phoneNumber")
        startActivity(intent)
        // Verify if WhatsApp is installed on the device

    }

}