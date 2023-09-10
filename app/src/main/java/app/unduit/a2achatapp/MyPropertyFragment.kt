package app.unduit.a2achatapp


import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.adapters.MyPropertyListAdapter

import app.unduit.a2achatapp.listeners.AdapterListener
import app.unduit.a2achatapp.models.PropertyData


class MyPropertyFragment : Fragment(),AdapterListener {

    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentMyPropertyBinding
    var propertylist=ArrayList<PropertyData>()





    private val propertyListAdapter by lazy {
        MyPropertyListAdapter(requireContext(),
            this,
            propertylist)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  app.unduit.a2achatapp.databinding.FragmentMyPropertyBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        recyclerViewManager()


    }


    private fun recyclerViewManager(){

        binding.rvMyProperty.layoutManager =
            LinearLayoutManager(context)
        binding.rvMyProperty.itemAnimator = DefaultItemAnimator()
        binding.rvMyProperty.adapter = propertyListAdapter
    }


    override fun onAdapterItemClicked(key: String, position: Int) {
        when(key){
            "menu"->{
                Log.e("open", "menu $position")

            }
        }
    }

}