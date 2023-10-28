package app.unduit.a2achatapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.adapters.CustomSpinnerAdapter
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep3Binding
import app.unduit.a2achatapp.databinding.FragmentPurchaseLandBinding
import app.unduit.a2achatapp.fragments.PostPropertyStep1FragmentArgs
import app.unduit.a2achatapp.helpers.Const
import app.unduit.a2achatapp.helpers.SpinnersHelper
import app.unduit.a2achatapp.helpers.gone
import app.unduit.a2achatapp.helpers.visible
import app.unduit.a2achatapp.models.PropertyData
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale


class PurchaseLandFragment : Fragment(),View.OnClickListener {


    val args: PurchaseLandFragmentArgs by navArgs()
    private var propertyData: PropertyData? = null

    private lateinit var binding: FragmentPurchaseLandBinding

    var useStr=""
    var ownerStr=""
    var heightStr=""
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPurchaseLandBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init(){
        Const.screenName=""
        propertyData =args.propertyData

        if(Const.REQUESDTED){
            binding.screenTitle.text="Post a Request"

        }

        spinnersManager()
        listeners()
        try {
            isEdit = args.propertyData != null

            propertyData = args.propertyData ?: PropertyData()


        }
        catch (ex:Exception){

        }
        if(isEdit){

            setData()

        }


        binding.spValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(text: Editable?) {
                try {
                    binding.spValue.removeTextChangedListener(this)

                    var origStr = text.toString()
                    if(origStr.isNotEmpty()) {
                        if (origStr.contains(",")) {
                            origStr = origStr.replace(",", "");
                        }

                        val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                        formatter.applyPattern("###,###,###,###,###")
                        val s = formatter.format(origStr.toLong())

                        binding.spValue.setText(s)
                        binding.spValue.setSelection(binding.spValue.text.length)
                    }
                    binding.spValue .addTextChangedListener(this)
                } catch (e: java.lang.Exception) {
                    Log.e("TextWatch", e.message.toString())
                }
            }
        })



    }

    fun listeners(){
    binding.nextBtn.setOnClickListener(this)
    binding.backIcon.setOnClickListener(this)
    }

    private fun spinnersManager(){
        ownerSpinner()
        useSpinner()
        heightSpinner()
    }
   private fun  setData(){

       binding.areaCommunity.setText(propertyData!!.area_community)
       binding.gfa.setText(propertyData!!.gfa)
       binding.plotSize.setText(propertyData!!.area_size)
       binding.far.setText(propertyData!!.far)
       binding.gValue.setText(propertyData!!.g_value)
       binding.spValue.setText(propertyData!!.sp)



       SpinnersHelper.ownerShipList().forEachIndexed { index, s ->
           if(  propertyData!!.ownership==s){
               binding.spinnerOwnership.setSelection(index)
           }
       }

       SpinnersHelper.useList().forEachIndexed { index, s ->
           if(  propertyData!!.use==s){
               binding.spinnerUse.setSelection(index)
           }
       }
       SpinnersHelper.heightList().forEachIndexed { index, s ->
           if(  propertyData!!.height==s){
               binding.spinnerHeight.setSelection(index)
           }
       }


   }



    private fun ownerSpinner() {
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.ownerShipList())

        binding.spinnerOwnership.adapter = adapter
        binding.spinnerOwnership.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.ownerShipList()[position]
                    ownerStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun useSpinner() {
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.useList())

        binding.spinnerUse.adapter = adapter
        binding.spinnerUse.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.useList()[position]
                    useStr = selectedItem

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }



    private fun heightSpinner() {
        val adapter = CustomSpinnerAdapter(requireContext(), SpinnersHelper.heightList())

        binding.spinnerHeight.adapter = adapter
        binding.spinnerHeight.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = SpinnersHelper.heightList()[position]
                    heightStr = selectedItem

                    if(position==1){
                        binding.gValue.visibility=View.GONE
                        binding.gTitle.visibility=View.GONE
                        binding.gStar.visibility=View.GONE
                    }
                    else {
                        binding.gValue.visibility=View.VISIBLE
                        binding.gTitle.visibility=View.VISIBLE
                        binding.gStar.visibility=View.VISIBLE
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // This method is called when nothing is selected, if needed
                }
            }
    }

    private fun checkValidation(){
        if(binding.areaCommunity.text.isNotEmpty()){


                    if(binding.gfa.text.isNotEmpty()){


                        if(binding.far.text.isNotEmpty()){

                            if(binding.spValue.text.isNotEmpty()){

                                propertyData!!.area_community=binding.areaCommunity.text.toString()
                                propertyData!!.ownership=ownerStr
                                propertyData!!.gfa=binding.gfa.text.toString()
                                propertyData!!.area_size=binding.plotSize.text.toString()
                                propertyData!!.far=binding.far.text.toString()
                                propertyData!!.use=useStr
                                propertyData!!.height=heightStr
                                propertyData!!.g_value=binding.gValue.text.toString()
                                propertyData!!.sp=binding.spValue.text.toString()



                                if(Const.REQUESDTED){
                                    findNavController().navigate(PurchaseLandFragmentDirections.actionPurchaseLandFragmentToPostRequestFragment(
                                        propertyData!!,args.isEdit,true))

                                }else {

                                    findNavController().navigate(PurchaseLandFragmentDirections.actionPurchaseLandFragmentToPostPropertyStep5Fragment(
                                        propertyData!!,args.isEdit,true))
                                }

                            }else {
                                Toast.makeText(requireContext(),"Please enter the Sp ",Toast.LENGTH_LONG).show()
                            }


                        }
                        else {
                            Toast.makeText(requireContext(),"Please enter the Far ",Toast.LENGTH_LONG).show()

                        }
                    }else {
                        Toast.makeText(requireContext(),"Please enter the GFA ",Toast.LENGTH_LONG).show()

                    }


        }else {
            Toast.makeText(requireContext(),"Please enter the area community ",Toast.LENGTH_LONG).show()
        }



    }

    override fun onClick(v: View?) {
       when(v!!.id){
           R.id.next_btn->{
               checkValidation()
           }
           R.id.back_icon->{
               requireActivity().onBackPressed()
           }


       }
    }


}