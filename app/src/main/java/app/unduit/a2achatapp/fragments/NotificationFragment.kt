package app.unduit.a2achatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.NotificationListAdapter
import app.unduit.a2achatapp.databinding.FragmentNotificationBinding
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener

import app.unduit.a2achatapp.models.PropertyData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NotificationFragment : Fragment() ,View.OnClickListener ,AdapterListener{

    private val TAG = "NotificationFragment"

    private lateinit var binding: FragmentNotificationBinding



    private lateinit var auth: FirebaseAuth

    var propertylist=ArrayList<PropertyData>()

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    private val notificationListAdapter by lazy {
        NotificationListAdapter(requireContext(),
            this,
            propertylist,
       )
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        listeners()
        recyclerViewManager()
        getNotificationData()



    }


    private fun recyclerViewManager(){

        binding.rvNotifications.layoutManager =
            LinearLayoutManager(context)
        binding.rvNotifications.itemAnimator = DefaultItemAnimator()
        binding.rvNotifications.adapter = notificationListAdapter
    }

    private fun listeners(){
        binding.btnBack.setOnClickListener(this)
    }


    private fun getNotificationData(){
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore

            val ref = db.collection("requests/${cUser.uid}/receive")

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }
                  val list=  propertylist.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(list)

                    notificationListAdapter.notifyDataSetChanged()
                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }


    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_back->{
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }



        }
    }


    private fun matchRequest(position: Int){

            progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("match").document(propertylist[position].uid)
            propertylist[position].property_status="match"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Uplaod")

                    moveToSenderMatch(position)
                    matchRequestNotificationUpdateStatus(position)
                    matchRequestNotificationForSenderUpdateStatus(position)
                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }


    private fun matchRequestNotificationUpdateStatus(position: Int){

        progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("receive").document(propertylist[position].uid)
            propertylist[position].property_status="matched"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Status has been updated")

                    notificationListAdapter.notifyItemChanged(position)



                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }


    private fun matchRequestNotificationForSenderUpdateStatus(position: Int){

        progressDialog.progressBarVisibility(true)

        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/match/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("send").document(propertylist[position].uid)
            propertylist[position].property_status="matched"
            propertylist[position].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Status has been updated")

                    notificationListAdapter.notifyItemChanged(position)



                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }

    @SuppressLint("SuspiciousIndentation")
    private fun moveToSenderMatch(position: Int){
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser


        Log.e("Tag21345","Created Property user  "+ propertylist[position].user_id.toString())
        if (currentUser != null) {
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/sender/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(propertylist[position].sender_id.toString()).collection("match").document(propertylist[position].uid)
            propertylist[position].property_status="match"

            collectionReference.set(propertylist[position])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    progressDialog.progressBarVisibility(false)
                    Log.e("Tafg213","Match has been successfully")

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    progressDialog.progressBarVisibility(false)
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }


    override fun onAdapterItemClicked(key: String, position: Int) {
            when(key){
                "match_request"->{
                    matchRequest(position)
                }
            }
    }


}