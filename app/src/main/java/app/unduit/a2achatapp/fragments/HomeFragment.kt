package app.unduit.a2achatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.Application
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.HomeSwiperAdapter
import app.unduit.a2achatapp.databinding.FragmentHomeBinding
import app.unduit.a2achatapp.helpers.Const

import app.unduit.a2achatapp.helpers.Const.userId
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import app.unduit.a2achatapp.models.User
import app.unduit.a2achatapp.models.roomModels.ExceptData
import app.unduit.a2achatapp.room.Database
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.launch
import java.security.Timestamp


class HomeFragment : Fragment(), View.OnClickListener, CardStackListener, AdapterListener {
    private val TAG = "HomeFragment"

    private lateinit var auth: FirebaseAuth
    var cardPos=0
    var userName=""
    var userImage=""
    var userWhatsapp=""
    var userExperience=""
    var userSpeciality=""
    var userPhone=""

    var userCompany=""
    private lateinit var binding: FragmentHomeBinding
    var propertylist = ArrayList<PropertyData>()

    private val cardStackView by lazy { requireActivity().findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(requireContext(), this) }



    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    private val homeSliderAdapter by lazy {
        HomeSwiperAdapter(
            requireContext(),
            this,
            propertylist
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
        Const.PropertyType=""
        sliderManager()
//        homeDummyData()
    }

    private fun init() {
        Log.e("Tag2345", "init :: ")
        loadUserProfileImage()

        getExceptedData()
        listeners()
        sliderManager()
//        homeDummyData()
    }

    private fun listeners() {
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



    fun bottomSheet(){

        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_home, null)

        val btnPostProperty = view.findViewById<AppCompatButton>(R.id.post_property)
        val btnPostRequest = view.findViewById<AppCompatButton>(R.id.post_request)
        val btnBulk = view.findViewById<AppCompatButton>(R.id.bulk_upload)

        btnPostProperty.setOnClickListener {
            dialog.dismiss()
            Const.REQUESDTED =false
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPostPropertyStep1Fragment())
        }

        btnPostRequest.setOnClickListener {
            dialog.dismiss()
            Const.REQUESDTED =true
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPostPropertyStep1Fragment())
        }

        btnBulk.setOnClickListener {
            dialog.dismiss()
            //TODO
        }

        dialog.setContentView(view)
        dialog.show()
    }


    private fun moveToSenderData(){
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser



        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/sender/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("send").document(propertylist[cardPos].uid)
            propertylist[cardPos].property_status="sender"
            propertylist[cardPos].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[cardPos])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Uplaod")
                    moveToReceiverData(propertylist[cardPos].uid)

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }

    @SuppressLint("SuspiciousIndentation")
    private fun moveToReceiverData(postId:String){
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser


            Log.e("Tag21345","Created Property user  "+ propertylist[cardPos].user_id.toString())
        if (currentUser != null) {
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/sender/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(propertylist[cardPos].user_id.toString()).collection("receive").document(postId)
            propertylist[cardPos].property_status="receive"

            propertylist[cardPos].sender_name=userName
            propertylist[cardPos].sender_id=currentUser.uid
            propertylist[cardPos].sender_image=userImage
            propertylist[cardPos].sender_phone=userPhone
            propertylist[cardPos].sender_whatsapp=userWhatsapp
            propertylist[cardPos].sender_experience=userExperience
            propertylist[cardPos].sender_speciality=userSpeciality
            propertylist[cardPos].sender_company=userCompany


            collectionReference.set(propertylist[cardPos])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Uplaod")
                    addExceptDataLocalDb()
                    // Handle success here
                }
                .addOnFailureListener { e ->
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }

    private  fun addExceptDataLocalDb(){

        lifecycleScope.launch {
            val database = Application.database

            database.exceptDataDao()
                .insert(ExceptData(post_id = propertylist[cardPos].uid.toString()))
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {


    }

    override fun onCardSwiped(direction: Direction?) {


        if(direction.toString()=="Right"){
            Log.e("Tag2345", "onCardSwiped ::$direction  :: $cardPos")
            moveToSenderData()
        }else {
            addExceptDataLocalDb()
        }




    }


    override fun onCardRewound() {
        Log.e("Tag2345", "onCardRewound :: ")

    }

    override fun onCardCanceled() {
        Log.e("Tag2345", "onCardCanceled :: ")

    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.e("Tag2345", "onCardAppeared :: ")

    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.e("Tag2345", "onCardDisappeared :: ")

    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        cardPos=position
        when(key){

            "view_detail"->{
                Const.screenName="Property_detail"
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPropertyDetailFragment(
                    propertylist[position], "home"
                ))
            }
        }


    }

    private fun loadUserProfileImage() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("users").document(cUser.uid)
            userId=cUser.uid
            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.data

                 userName= (data?.get("name") as String?).toString()
                    Const.userName=userName

                 userImage= (data?.get("profile_image") as String?).toString()
                    userPhone= (data?.get("phone") as String?).toString()
                    userWhatsapp= (data?.get("whatsapp") as String?).toString()
                    try{
                        userSpeciality= (data?.get("speciality") as String?).toString()
                        userExperience= (data?.get("experience") as String?).toString()
                    }
                    catch (ex:Exception){

                    }

                    userCompany= (data?.get("company") as String?).toString()



                    try {
                        val image = data?.get("profile_image") as String
                        Glide.with(this).load(image)
                            .fallback(R.drawable.ic_deafult_profile_icon)
                            .placeholder(R.drawable.ic_deafult_profile_icon)
                            .into(binding.profileImage)

                        Const.userImage=userImage
                    }catch (ex:Exception){

                    }

                }
            }
        }




    }
    private fun loadHomeData(){
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("properties").whereNotEqualTo("user_id", cUser.uid)

            ref.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }
                    val list =propertylist.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(list)
                    homeSliderAdapter.notifyDataSetChanged()
                    Log.d(TAG, "propertylist size => ${propertylist.size}")
                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }
    }



    private fun loadHomeDataWithExcepted(list: ArrayList<String>) {
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val batchSize = 10
            propertylist.clear()

            // Split the 'list' into batches of 10 and query Firestore in chunks
            val batches = list.chunked(batchSize)

            val queryTasks = batches.map { batch ->
                val ref = db.collection("properties").whereNotIn(FieldPath.documentId(), batch)

                ref.get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")
                        propertylist.add(document.toObject(PropertyData::class.java))
                    }
                }
            }

            // Use Tasks.whenAllSuccess to wait for all query tasks to complete
            Tasks.whenAllSuccess<Unit>(queryTasks)
                .addOnSuccessListener {
                    // Filter out properties for the current user
                    val filteredList = propertylist.filter { it.user_id != cUser.uid  && it.post_type!="request"}

                    Log.e("Tag2135","Data "+filteredList.size)
                    propertylist.clear()
                    propertylist.addAll(filteredList.sortedByDescending { it.created_date })

                    homeSliderAdapter.notifyDataSetChanged()
                    progressDialog.progressBarVisibility(false)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred.loadHomeDataWithExcepted")
                    progressDialog.progressBarVisibility(false)
                }
        }
    }


    private fun getExceptedData(){
        lifecycleScope.launch {
            val database = Application.database


           if( database.exceptDataDao().getRowCount()!=0){
               val data = database.exceptDataDao().getAllEntities()

               val stringList =ArrayList<String>()

               data.forEach {
                   stringList.add(it.post_id)
                   Log.e("Tag12345","Data 12334"+it.post_id)
               }


               if(stringList.isEmpty()){


               }
               else {
                   Log.e("Tag21345","DB is  not Empty "+stringList.size)
                   loadHomeDataWithExcepted(stringList)
               }

//                   loadHomeDataWithExcepted(stringList)





           }else {
               Log.e("Tag21345","DB is Empty")
               loadHomeData()
           }



        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.favourite_icon -> {
                Const.screenName="favourite_icon"
                findNavController().navigate(R.id.action_homeFragment_to_favouriteFragment)

            }

            R.id.notification_icon -> {
                Const.screenName="notification_icon"
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }

            R.id.profile_image -> {
                Const.screenName="profile_image"
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            }

            R.id.property_list -> {
//                Toast.makeText(requireContext(),"Under Development",Toast.LENGTH_LONG).show()
                Const.screenName = "property_list"
                findNavController().navigate(R.id.action_homeFragment_to_propertyListFragment)
            }

            R.id.add_property -> {
                Const.screenName="add_property"
//                findNavController().navigate(R.id.action_homeFragment_to_propertyBottomSheetFragment)
                bottomSheet()
            }

            R.id.chat -> {

                Const.screenName="chat"
                findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
            }



        }
    }
}