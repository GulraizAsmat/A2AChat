package app.unduit.a2achatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.unduit.a2achatapp.databinding.FragmentPostPropertyStep4Binding
import app.unduit.a2achatapp.databinding.FragmentPostRequestBinding
import app.unduit.a2achatapp.databinding.FragmentPropertyBottomSheetBinding


class PostRequestFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentPostRequestBinding
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

    override fun onClick(v: View?) {
      when(v!!.id){
          R.id.btn_back->{
              requireActivity().onBackPressed()
          }
          R.id.next_btn->{
              Toast.makeText(requireContext(),"Under Development",Toast.LENGTH_LONG).show()
          }
      }
    }

}