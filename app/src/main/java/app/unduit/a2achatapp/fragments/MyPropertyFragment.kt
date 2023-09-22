package app.unduit.a2achatapp.fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.MyPropertyListAdapter
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class MyPropertyFragment : Fragment(), AdapterListener {

    private val TAG = "MyPropertyFragment"

    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentMyPropertyBinding
    var propertylist = ArrayList<PropertyData>()

    private lateinit var auth: FirebaseAuth

    private val propertyListAdapter by lazy {
        MyPropertyListAdapter(
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
        binding = app.unduit.a2achatapp.databinding.FragmentMyPropertyBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {
        recyclerViewManager()
        getData()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getData() {
        progressDialog.progressBarVisibility(true)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val db = Firebase.firestore
            val ref = db.collection("properties").whereEqualTo("user_id", cUser.uid)

            ref.get()
                .addOnSuccessListener { documents ->
                    propertylist.clear()
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        propertylist.add(document.toObject(PropertyData::class.java))
                    }


                    val list =propertylist.sortedByDescending { it.created_date }
                    propertylist.clear()
                    propertylist.addAll(list)

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


    private fun recyclerViewManager() {

        binding.rvMyProperty.layoutManager =
            LinearLayoutManager(context)
        binding.rvMyProperty.itemAnimator = DefaultItemAnimator()
        binding.rvMyProperty.adapter = propertyListAdapter
    }

    private fun deleteProperty(position: Int) {
        AlertDialog.Builder(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog)
            .setTitle("Are you sure you want to delete?")
            .setMessage("You will not be able to recover this property")
            .setNegativeButton("Cancel") { view, _ ->
                view.dismiss()
            }
            .setPositiveButton("Delete") { view, _ ->
                progressDialog.progressBarVisibility(true)
                val item = propertylist[position]

                val db = Firebase.firestore
                val ref = db.collection("properties").document(item.uid)
                ref.delete().addOnCompleteListener { task ->
                    progressDialog.progressBarVisibility(false)
                    if (task.isSuccessful) {

                        propertylist.removeAt(position)
                        propertyListAdapter.notifyItemRemoved(position)

                        requireContext().showToast("Property Deleted!")
                    } else {
                        requireContext().showToast("An error occurred. Please try again later")
                    }
                    view.dismiss()
                }.addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                    requireContext().showToast("An error occurred. Please try again later")
                    progressDialog.progressBarVisibility(false)
                    view.dismiss()
                }
            }
            .show()
    }


    override fun onAdapterItemClicked(key: String, position: Int) {
        when (key) {
            "open" -> {
                Log.e(TAG, "open $position")
                findNavController().navigate(
                    MyPropertyFragmentDirections.actionMyPropertyFragmentToPropertyDetailFragment(
                        propertylist[position], "MyProperty"
                    )
                )
            }

            "edit" -> {
                Log.e(TAG, "edit $position")
                findNavController().navigate(
                    MyPropertyFragmentDirections.actionMyPropertyFragmentToPostPropertyStep1Fragment(
                        propertylist[position]
                    )
                )
            }

            "delete" -> {
                Log.e(TAG, "delete $position")
                deleteProperty(position)
            }
        }
    }

}