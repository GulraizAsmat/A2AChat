package app.unduit.a2achatapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.Application
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.HomeSwiperAdapter
import app.unduit.a2achatapp.databinding.FragmentHomeBinding
import app.unduit.a2achatapp.fcm.FcmNotificationSend

import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.Const.fcmToken

import app.unduit.a2achatapp.helpers.Const.userId
import app.unduit.a2achatapp.helpers.DateHelper
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.SharedPref
import app.unduit.a2achatapp.helpers.showToast

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import app.unduit.a2achatapp.models.roomModels.ExceptData
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.launch
import org.json.JSONObject


class HomeFragment : Fragment(), View.OnClickListener, CardStackListener, AdapterListener {
    private val TAG = "HomeFragment"

    private lateinit var auth: FirebaseAuth
    var cardPos=0
//    var userName=""
//    var userImage=""
//    var userWhatsapp=""
//    var userExperience=""
//    var userSpeciality=""
//    var userPhone=""
//    var Const.userCompany=""
    var userStatus="agent"


   var  chatCountArray=0
    private lateinit var binding: FragmentHomeBinding
    var propertylist = ArrayList<PropertyData>()
    var notificationCountList = ArrayList<PropertyData>()

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
        getNotificationCount()
        firebaseChatCountCalling()
//        homeDummyData()
    }

    private fun init() {


        Log.e("Tag2345", "init :: ")
        loadUserProfileImage()

        getExceptedData()
        listeners()
        sliderManager()
        getFcmToken()
//        homeDummyData()
    }

    private fun listeners() {
        binding.favouriteIcon.setOnClickListener(this)
        binding.notificationIcon.setOnClickListener(this)
        binding.profileImage.setOnClickListener(this)
        binding.propertyList.setOnClickListener(this)
        binding.matchIcon.setOnClickListener(this)
        binding.requestIcon.setOnClickListener(this)
        binding.addProperty.setOnClickListener(this)
        binding.chat.setOnClickListener(this)
        binding.guideCloseBtn.setOnClickListener(this)

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

    private fun getFcmToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
            Log.e("fcm_token","Fetching FCM registration token failed")
                return@OnCompleteListener
            }else {
                val token = task.result
                fcmToken=token
                updateFcmTokenUserTable()
                Log.e("fcm_token", "FCM token $token")
            }

            // fetching the token

        })
    }


    private fun bottomSheet(){

        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.item_bottomsheet_home, null)

        val btnPostProperty = view.findViewById<AppCompatButton>(R.id.post_property)
        val btnPostRequest = view.findViewById<AppCompatButton>(R.id.post_request)
        val btnBulk = view.findViewById<AppCompatButton>(R.id.bulk_upload)

            Log.e("Tag12", "userStatus ::$userStatus")
        if(userStatus=="User") {
            btnPostProperty.visibility = View.GONE
        }
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
        binding.btnNotClickable.visibility=View.VISIBLE
        progressDialog.progressBarVisibility(true)
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser
        Log.e("Tag123","Post Pos "+cardPos)
        Log.e("Tag123","Post uid "+ propertylist[cardPos].uid)


        if (currentUser != null) {
            Log.e("Tag21345","Created Sender user  "+ currentUser.uid)
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/sender/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("requests").document(currentUser.uid).collection("posts").document(propertylist[cardPos].uid)
            propertylist[cardPos].property_status="sender"
            propertylist[cardPos].created_date=currentTimeMillis.toString()
            collectionReference.set(propertylist[cardPos])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Uplaod")
                    moveToReceiverData(propertylist[cardPos].uid)
                    moveToNotificationData(propertylist[cardPos].uid)
                    sendNotification(propertylist[cardPos])

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
            val collectionReference = db.collection("requests").document(propertylist[cardPos].user_id.toString()).collection("posts").document(postId)
            propertylist[cardPos].property_status="receiver"

            propertylist[cardPos].sender_name=Const.userName
            propertylist[cardPos].sender_id=currentUser.uid
            propertylist[cardPos].sender_image=Const.userImage
            propertylist[cardPos].sender_phone=Const.userPhone
            propertylist[cardPos].sender_whatsapp=Const.userWhatsapp
            propertylist[cardPos].sender_experience=Const.userExperience
            propertylist[cardPos].sender_speciality=Const.userSpeciality
            propertylist[cardPos].sender_company=Const.userCompany
            propertylist[cardPos].sender_fcm=Const.fcmToken


            collectionReference.set(propertylist[cardPos])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Uplaod")


                    // Handle success here
                }
                .addOnFailureListener { e ->
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }



    }

    @SuppressLint("SuspiciousIndentation")
    private fun moveToNotificationData(postId:String){
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser


        Log.e("Tag21345","Created Property user  "+ propertylist[cardPos].user_id.toString())
        if (currentUser != null) {
            val currentTimeMillis = System.currentTimeMillis()
            var path ="requests/${currentUser.uid}/sender/${currentTimeMillis.toString()}"
            val collectionReference = db.collection("notifications").document(propertylist[cardPos].user_id.toString()).collection("posts").document(postId)
            propertylist[cardPos].property_status="receiver"

            propertylist[cardPos].sender_name=Const.userName
            propertylist[cardPos].sender_id=currentUser.uid
            propertylist[cardPos].sender_image=Const.userImage
            propertylist[cardPos].sender_phone=Const.userPhone
            propertylist[cardPos].sender_whatsapp=Const.userWhatsapp
            propertylist[cardPos].sender_experience=Const.userExperience
            propertylist[cardPos].sender_speciality=Const.userSpeciality
            propertylist[cardPos].sender_company=Const.userCompany
            propertylist[cardPos].sender_fcm=Const.fcmToken


            collectionReference.set(propertylist[cardPos])
                .addOnSuccessListener { documentReference ->
                    // Data added successfully
                    // You can get the document ID using documentReference.id
                    Log.e("Tafg213","Uplaod moveToNotificationData")

                    addExceptDataUploadOnFirebase()

                    // Handle success here
                }
                .addOnFailureListener { e ->
                    // Handle errors here
                    Log.e("Tafg213", "Fail$e")
                }
        }
    }

    private fun sendNotification(data:PropertyData){


        val notificationJson = JSONObject()

        notificationJson.put("title",
           "Property Request" )
        notificationJson.put("body", Const.userName.toString()+" send the request for "+data.property_type )


        val jsonMain = JSONObject()

        jsonMain.put("to", data.user_fcm )
        jsonMain.put("notification", notificationJson)



        Log.e("TAG123","USER oOFFLINE "+jsonMain);





                FcmNotificationSend.post("https://fcm.googleapis.com/fcm/send", jsonMain, {
                    Log.e("Notif","SEnd happy mode")
                }, {
                    Log.e("Notif","SEnd happy Error")
                })
            }

    @SuppressLint("SetTextI18n")
    private fun getNotificationCount(){
        propertylist.clear()


        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore

            val ref = db.collection("notifications/${cUser.uid}/posts").whereEqualTo("property_status","receiver")

            ref.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                try {
                    if (snapshot != null) {
                    // Data has changed or the document was initially fetched
                    val data = snapshot.documents
                    if(SharedPref.getInt(requireContext(),Const.matchCount,0)>0) {



                        binding.clNotificationCount.visibility = View.VISIBLE
                        binding.notificationCount.text = (data.size + SharedPref.getInt(
                            requireContext(),
                            Const.matchCount,
                            0
                        )).toString()

                    }else {
                        if (data.size > 0) {
                            binding.clNotificationCount.visibility = View.VISIBLE
                            binding.notificationCount.text = (data.size ).toString()
                        } else {
                            binding.clNotificationCount.visibility = View.GONE
                        }
                    }





                    Log.e("Tag23fd","Count Listener "+ data.size)
                    // Process the data as needed
                }

                }catch (ex:Exception){

                }


            }


        }


    }

    private fun firebaseChatCountCalling() {


        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")
            .child(Const.userId)





        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                chatCountArray = 0

                snapshot.children.forEach { it ->

                    it.child("conversation").children.forEach { data ->


                    }


                    if (it.child("lastseen").child("isSeen").value == false) {

                        chatCountArray++

                    }


                }
                try {
                    if (chatCountArray > 0) {
                        binding.clChatCount.visibility = View.VISIBLE

                        if(chatCountArray>9){
                            binding.chatCount.text = "9+"
                        }else {
                            binding.chatCount.text = chatCountArray.toString()
                        }

                    } else {
                        binding.clChatCount.visibility = View.GONE
                    }
                }
                catch (ex:Exception){

                }


                Log.e("Tag12354", "chatCountArray $chatCountArray")
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
    private fun addExceptDataUploadOnFirebase(){
        val db = FirebaseFirestore.getInstance()

        auth = Firebase.auth
        val currentUser = auth.currentUser

        val collectionReference = db.collection("excepted").document(currentUser!!.uid).collection("posts").document(propertylist[cardPos].uid)
        collectionReference.set(ExceptData(post_id = propertylist[cardPos].uid.toString()))
            .addOnSuccessListener { documentReference ->
                // Data added successfully
                // You can get the document ID using documentReference.id
                Log.e("Tafg213","Uplaod addExceptDataUploadOnFirebase")
                addExceptDataLocalDb()

                // Handle success here
            }
            .addOnFailureListener { e ->
                // Handle errors here
                Log.e("Tafg213", "Fail$e")
            }
    }



    private  fun addExceptDataLocalDb(){

        lifecycleScope.launch {
            val database = Application.database

            database.exceptDataDao()
                .insert(ExceptData(post_id = propertylist[cardPos].uid.toString()))
            cardPos++
            binding.btnNotClickable.visibility=View.GONE
            progressDialog.progressBarVisibility(false)
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {


    }

    override fun onCardSwiped(direction: Direction?) {


        if(direction.toString()=="Right"){
            Log.e("Tag2345", "onCardSwiped ::$direction  :: $cardPos")
            moveToSenderData()
        }
        else {
            addExceptDataUploadOnFirebase()
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
        Log.e("Tag2345", "Pso $position")
        when(key){

            "view_detail"->{
                propertylist[cardPos].sender_name=Const.userName
                propertylist[cardPos].sender_image=Const.userImage
                propertylist[cardPos].sender_phone=Const.userPhone
                propertylist[cardPos].sender_whatsapp=Const.userWhatsapp
                propertylist[cardPos].sender_experience=Const.userExperience
                propertylist[cardPos].sender_speciality=Const.userSpeciality
                propertylist[cardPos].sender_company=Const.userCompany
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

                Log.e("Tag23","user Data "+ snapshot.data)

                snapshot?.let {
                    val data = it.data

                    Const.userName= (data?.get("name") as String?).toString()
                    if((data?.get("status") as String?).toString().isNotEmpty()) {
                        userStatus = (data?.get("status") as String?).toString()
                    }


                    Const.userImage= (data?.get("profile_image") as String?).toString()
                    Const. userPhone= (data?.get("phone") as String?).toString()
                    Const. userWhatsapp= (data?.get("whatsapp") as String?).toString()
                    Const.userCompany= (data?.get("company") as String?).toString()
                    try{
                        Const.  userSpeciality= (data?.get("speciality") as String?).toString()
                        Const.  userExperience= (data?.get("experience") as String?).toString()
                    }
                    catch (ex:Exception){

                    }





                    try {
                        val image = data?.get("profile_image") as String
                        Glide.with(this).load(image)
                            .fallback(R.drawable.ic_deafult_profile_icon)
                            .placeholder(R.drawable.ic_deafult_profile_icon)
                            .into(binding.profileImage)

                        Const.userImage=image
                    }catch (ex:Exception){

                    }

                }
            }
        }




    }

    private fun updateFcmTokenUserTable(){
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        Log.e("Tsd","User id "+ currentUser!!.uid)
        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("users").document(cUser.uid)

            val updates = hashMapOf<String, Any>(
                "fcm_token" to fcmToken
            )
            ref.update(updates)
                .addOnSuccessListener {
                    // The update was successful
                    Log.e("Tagxx23","Data is Update ")
                }
                .addOnFailureListener { e ->
                    Log.e("Tagxx23", "Data is Error $e")
                    // Handle any errors that occurred during the update
                }
        }
    }

    private fun getExcepted(){
        var exceptedList=ArrayList<ExceptData>()
        progressDialog.progressBarVisibility(true)
        auth = Firebase.auth
        val currentUser = auth.currentUser

        val db = Firebase.firestore
        val ref = db.collection("excepted/${currentUser!!.uid}/posts")

        ref.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.e(TAG, "${document.id} => ${document.data}")
                    exceptedList.add(document.toObject(ExceptData::class.java))

                }
                lifecycleScope.launch {
                    val database = Application.database

                    database.exceptDataDao()
                        .insertAll(exceptedList)
                }


            }
                    .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }



                }
    private fun loadHomeData(list: ArrayList<String>){
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
                    val filteredList = propertylist.filter { it.user_id != cUser.uid  && it.post_type!="request"}.distinctBy { it.uid }



                    propertylist.clear()
                    propertylist.addAll(filteredList)


                    Log.e("Raf","filteredList :: "+filteredList.size)

                    list.forEach {  local->
                        propertylist.apply {  forEachIndexed { index, main ->

                            if(main.uid==local){
                                Log.e("Raf","main.uid :: "+ main.uid +" local "+local +"propertylistTenp "+propertylist[index].uid  )

                                propertylist.removeAt(index)
                                return@apply

                            }

                        }
                        }
                    }




                    Log.e("Raf", "-------------------")
                 val data= propertylist.sortedByDescending { it.created_date }.distinctBy { it.uid }

                    data.forEach { it->
                         Log.e("Raf","Date :: "+  DateHelper.convertTimestampToTimeAgo(it.created_date.toLong()))

                     }

                    propertylist.clear()
                    propertylist.addAll(data)




                    homeSliderAdapter.notifyDataSetChanged()

                    if(propertylist.isEmpty()){
                        if(SharedPref.getBoolean(requireContext(),Const.FirstTimeAppLogin,false)){

                            binding.guideView.visibility=View.VISIBLE
                            SharedPref.setBoolean(requireContext(),Const.FirstTimeAppLogin,false)
                        }else {
                            binding.clEmpty.visibility=View.VISIBLE
                        }


                    }else{
                        if(SharedPref.getBoolean(requireContext(),Const.FirstTimeAppLogin,false)){

                            binding.guideView.visibility=View.VISIBLE

                            SharedPref.setBoolean(requireContext(),Const.FirstTimeAppLogin,false)
                        }else {
                            binding.clEmpty.visibility=View.GONE
                        }


                    }
                    Log.e("Raf", "propertylist size => ${propertylist.size}")
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

                    Log.e("Tag2135","Data "+filteredList.sortedByDescending { it.created_date }.distinctBy { it.uid }.size)
                    propertylist.clear()
                    propertylist.addAll(filteredList.sortedByDescending { it.created_date }.distinctBy { it.uid })

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

               }

//                   loadHomeDataWithExcepted(stringList)



               loadHomeData(stringList)

           }else {
               Log.e("Tag21345","DB is Empty")
               getExcepted()
               val stringList =ArrayList<String>()
               loadHomeData(stringList)

           }




        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.favourite_icon -> {
                Const.screenName="favourite_icon"
                val bundle =Bundle()
                bundle.putString("screen_type","favourite")
                findNavController().navigate(R.id.action_homeFragment_to_favouriteFragment,bundle)

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
            R.id.match_icon->{
                val bundle =Bundle()
                bundle.putString("screen_type","match")
                Const.screenName="favourite_icon"
                findNavController().navigate(R.id.action_homeFragment_to_favouriteFragment,bundle)

            }
            R.id.request_icon->{
                Const.screenName="favourite_icon"
                val bundle =Bundle()
                bundle.putString("screen_type","request")
                findNavController().navigate(R.id.action_homeFragment_to_favouriteFragment,bundle)
            }
            R.id.guide_close_btn->{
                binding.guideView.visibility=View.GONE
            }



        }
    }
}