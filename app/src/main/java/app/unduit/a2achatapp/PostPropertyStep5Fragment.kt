package app.unduit.a2achatapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.adapters.PostPropertyImagesAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep5Binding
import app.unduit.a2achatapp.helpers.ProgressDialog
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.helpers.showToast
import app.unduit.a2achatapp.models.PropertyData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PostPropertyStep5Fragment : Fragment() {
    private lateinit var binding: FragmentPostPropertyStep5Binding

    private val args: PostPropertyStep5FragmentArgs by navArgs()

    private lateinit var propertyData: PropertyData

    private var negotiationStr = "Negotiable"

    private val progressDialog by lazy {
        ProgressDialog(requireContext())
    }

    val adapter : PostPropertyImagesAdapter  by lazy {
        PostPropertyImagesAdapter()
    }
    private var propertyImages = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostPropertyStep5Binding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }
    override


    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {

        propertyData = args.propertyData

        spinnerManager()
        binding.postListBtn.setOnClickListener {
            uploadData()
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnUpload.setOnClickListener {
//            if (Build.VERSION.SDK_INT < 19) {
//                val intent = Intent()
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                intent.action = Intent.ACTION_GET_CONTENT
//                startActivityForResult(
//                    Intent.createChooser(intent, "Select Picture"), 10000
//                )
//            } else {
//                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                intent.addCategory(Intent.CATEGORY_OPENABLE)
//                intent.type = "image/*"
//                startActivityForResult(intent, 10000)
//            }
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"), 10000
            )
        }

        binding.rvImages.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvImages.adapter = adapter
    }

    private fun spinnerManager() {
        negotiationSpinner()
    }

    private fun negotiationSpinner(){
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.negotiationList())

        binding.spinnerNegotiation.adapter = adapter
        binding.spinnerNegotiation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.negotiationList()[position]
                    negotiationStr = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun uploadData(){
        val spStr = binding.sp.text.toString()
        val title = binding.propertyTitle.text.toString()

        if(spStr.isEmpty()) {
            requireContext().showToast("Please enter a value for SP")
        } else if(title.isEmpty()) {
            requireContext().showToast("Please enter a title")
        } else {

            progressDialog.progressBarVisibility(true)

            propertyData.op = binding.op.text.toString()
            propertyData.sp = binding.sp.text.toString()
            propertyData.roi = binding.roi.text.toString()
            propertyData.negotiation = negotiationStr
            propertyData.property_title = title

            val db = Firebase.firestore
            val id: String = db.collection("properties").document().id

            propertyData.uid = id

            db.collection("properties")
                .document(id)
                .set(propertyData)
                .addOnSuccessListener {
                    progressDialog.progressBarVisibility(false)
                    requireContext().showToast("Property Posted!")
                    findNavController().navigate(R.id.action_postPropertyStep5Fragment_to_homeFragment)
                }.addOnFailureListener {e ->
                    progressDialog.progressBarVisibility(false)
                    requireContext().showToast(e.localizedMessage.toString())
                }

//            findNavController().navigate(R.id.action_postPropertyStep5Fragment_to_homeFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10000 && resultCode == RESULT_OK) {

            if (data?.clipData != null) {
                val count = data.clipData?.itemCount
                for (i in 0 until count!!) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    propertyImages.add(imageUri)
                }
                adapter.submitList(null)
                adapter.submitList(propertyImages)
            }

//            val result = MultiImagePicker.Result(data)
//            if (result.isSuccess()) {
//                val imageListInUri = result.getImageList() // List os selected images as content Uri format
//
//                propertyImages.addAll(imageListInUri)
//                adapter.submitList(null)
//                adapter.submitList(propertyImages)
//
//
//                //You can also request list as absolute filepath instead of Uri as below
//                val imageListInAbsFilePath = result.getImageListAsAbsolutePath(requireContext())
//
//                //do your stuff from the selected images list received
//            }
        }
    }

}