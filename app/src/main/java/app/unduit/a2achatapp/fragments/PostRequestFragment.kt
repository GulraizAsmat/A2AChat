package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentPostRequestBinding
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.models.PropertyData
import app.unduit.a2achatapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PostRequestFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentPostRequestBinding

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
        binding = FragmentPostRequestBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        listeners()
    }
    fun listeners(){
        binding.btnBack.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)

    }

    private fun verifyData(){
        val title = binding.title.text.toString()
        val description = binding.description.text.toString()

        if(title.isEmpty()) {
            requireContext().showToast("Please enter a title")
        } else if(description.isEmpty()) {
            requireContext().showToast("Please enter a description")
        } else {
            uploadData(title, description)
        }
    }

    private fun uploadData(title: String, description: String){
        progressDialog.progressBarVisibility(true)

        val db = Firebase.firestore
        val id = db.collection("properties").document().id

        val propertyData = PropertyData()
        propertyData.uid = id
        propertyData.property_title = title
        propertyData.property_description = description
        propertyData.created_date = System.currentTimeMillis().toString()
        propertyData.post_type = "request"



        val auth = Firebase.auth
        val currentUser = auth.currentUser

        currentUser?.let { cUser ->
            val ref = db.collection("users").document(cUser.uid)

            ref.get().addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val data = it.toObject(User::class.java)

                    propertyData.user_name = data?.name
                    propertyData.user_picture = data?.profile_image
                    propertyData.user_company = data?.company
                    propertyData.user_experience = data?.experience
                    propertyData.user_specialty = data?.speciality
                    propertyData.user_phone = data?.phone.toString()
                    propertyData.user_whatsapp = data?.whatsapp.toString()

                    db.collection("properties")
                        .document(id)
                        .set(propertyData)
                        .addOnSuccessListener {
                            progressDialog.progressBarVisibility(false)
                            requireContext().showToast("Property Posted!")
                            requireActivity().onBackPressed()
                        }.addOnFailureListener { e ->
                            progressDialog.progressBarVisibility(false)
                            requireContext().showToast(e.localizedMessage.toString())
                        }
                }?: run {
                    //Need to logout
                    progressDialog.progressBarVisibility(false)
                    requireContext().showToast("An error occurred. Try again later!")
                }
            }.addOnFailureListener { e ->
                progressDialog.progressBarVisibility(false)
                requireContext().showToast(e.localizedMessage.toString())
            }
        } ?: run {
            //Need to logout
            progressDialog.progressBarVisibility(false)
            requireContext().showToast("An error occurred. Try again later!")
        }
    }

    override fun onClick(v: View?) {
      when(v!!.id){
          R.id.btn_back -> {
              requireActivity().onBackPressed()
          }
          R.id.next_btn -> {
              verifyData()
          }
      }
    }

}