package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.PropertyListMultipleViewAdapter
import app.unduit.a2achatapp.databinding.FragmentPropertyListBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PropertyListFragment : Fragment(), AdapterListener {

    private val TAG = "PropertyListFragment"

    private lateinit var binding: FragmentPropertyListBinding

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    private lateinit var auth: FirebaseAuth

    var propertylist = ArrayList<PropertyData>()

    private val adapter by lazy {
        PropertyListMultipleViewAdapter(
            requireContext(),
            this,
            propertylist
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPropertyListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        setListeners()
        setRV()
        getData()
        loadUserProfileImage()

    }

    private fun setListeners() {
        binding.favouriteIcon.setOnClickListener {
            Const.screenName = "favourite_icon"
            findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToFavouriteFragment())
        }

        binding.notificationIcon.setOnClickListener {
            Const.screenName = "notification_icon"
            findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToNotificationFragment())
        }

        binding.profileImage.setOnClickListener {
            Const.screenName = "profile_image"
            findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToProfileFragment())
        }

        binding.btnChat.setOnClickListener {
            Toast.makeText(requireContext(),"Under Development", Toast.LENGTH_LONG).show()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setRV() {
        binding.rvProperty.layoutManager =
            LinearLayoutManager(context)
        binding.rvProperty.itemAnimator = DefaultItemAnimator()
        binding.rvProperty.adapter = adapter
    }

    private fun getData() {
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("properties").whereNotEqualTo("user_id", cUser.uid)

            ref.get()
                .addOnSuccessListener { documents ->
                    propertylist.clear()
                    for (document in documents) {
                        Log.e(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }

                    val list = propertylist.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(list)
                    adapter.notifyDataSetChanged()
                    Log.d(TAG, "propertylist size => ${propertylist.size}")
                    progressDialog.progressBarVisibility(false)
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                }
        }
    }

    private fun loadUserProfileImage() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("users").document(cUser.uid)
            Const.userId =cUser.uid
            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.data
                    try {
                        val image = data?.get("profile_image") as String
                        Glide.with(this).load(image)
                            .fallback(R.drawable.ic_deafult_profile_icon)
                            .placeholder(R.drawable.ic_deafult_profile_icon)
                            .into(binding.profileImage)

                    }catch (ex:Exception){

                    }

                }
            }
        }

    }

    override fun onAdapterItemClicked(key: String, position: Int) {
        when(key) {
            "chat" -> {
                Toast.makeText(requireContext(),"Under Development",Toast.LENGTH_LONG).show()
            }
            else -> {
                findNavController().navigate(PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment(propertylist[position]))
            }
        }
    }
}