package app.unduit.a2achatapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.databinding.FragmentPostRequestBinding
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.models.PropertyData
import app.unduit.a2achatapp.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PostRequestFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentPostRequestBinding
    private val args: PostRequestFragmentArgs by navArgs()

    private lateinit var propertyData: PropertyData
    var isEdit = false
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

        isEdit = args.isEdit
        propertyData = args.propertyData

        if(isEdit){
            binding.title.setText(propertyData.property_title)
            binding.description.setText(propertyData.property_description)
        }

        listeners()
        setData()




// Attach the TextWatcher to the EditText
        binding.description.addTextChangedListener(textWatcher)

    }


    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // This method is called before the text is changed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // This method is called when the text is being changed

            // Do something with the new text
            binding.textCounter.text= "${s!!.length}/500"


        }

        override fun afterTextChanged(s: Editable?) {
            if(s!!.length>500){
                binding.textCounter.setTextColor(ContextCompat.getColor(requireContext(),R.color.color_red_shade_1))
            }else {
                binding.textCounter.setTextColor(ContextCompat.getColor(requireContext(),R.color.color_black_shade_1))
            }
            // This method is called after the text has been changed
        }
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
        }  else {
            uploadData(title, description)
        }
    }

    private fun uploadData(title: String, description: String){
        progressDialog.progressBarVisibility(true)

        val db = Firebase.firestore
        var id=""
        if(isEdit){
            id = propertyData.uid
        }
        else{
            id = db.collection("properties").document().id

        }



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
                    propertyData.brn = data?.brn.toString()
                    propertyData.verified = data?.verified == true
                    db.collection("properties")
                        .document(id)
                        .set(propertyData)
                        .addOnSuccessListener {
                            progressDialog.progressBarVisibility(false)
                            requireContext().showToast("Property Posted!")
                            findNavController().navigate(R.id.action_postRequestFragment_to_homeFragment)
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


    fun setData(){
        Log.e("Ghas","------------B*R-------------- : ")
        Log.e("Ghas","PT : "+propertyData.property_type)
        Log.e("Ghas","AC : "+propertyData.area_community)
        Log.e("Ghas","P : "+propertyData.project)
        Log.e("Ghas","BE : "+propertyData.bedrooms)
        Log.e("Ghas","BA : "+propertyData.bathrooms)
        Log.e("Ghas","Bud-max : "+propertyData.budget_max)
        Log.e("Ghas","Bud-min : "+propertyData.budget_min)
        Log.e("Ghas","PS-max : "+propertyData.property_size_max)
        Log.e("Ghas","PS-min : "+propertyData.property_size_min)
        Log.e("Ghas","MR : "+propertyData.maidroom)
        Log.e("Ghas","BAL : "+propertyData.balcony)
        Log.e("Ghas","OC : "+propertyData.occupancy)
        Log.e("Ghas","Pur : "+propertyData.purchase_goal)
        Log.e("Ghas","PAY : "+propertyData.payment_method)
        Log.e("Ghas","property_type : "+propertyData.property_type)

        Log.e("Ghas","------------R*R-------------- : ")

        Log.e("Ghas","Number of Checks : " +propertyData.number_of_checks)
        Log.e("Ghas","property_moving_time : " +propertyData.property_moving_time)
        Log.e("Ghas","property_furniture : " +propertyData.property_furniture)


        Log.e("Ghas","------------B*C-------------- : ")

        Log.e("Ghas","Fitting : " +propertyData.fitting)

    }

    override fun onClick(v: View?) {
      when(v!!.id){
          R.id.btn_back -> {
              requireActivity().onBackPressed()
          }
          R.id.next_btn -> {
//              Toast.makeText(requireContext(),"Under Development",Toast.LENGTH_LONG).show()
//              setData()
              verifyData()
          }
      }
    }

}