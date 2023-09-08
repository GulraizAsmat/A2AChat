package app.unduit.a2achatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.adapters.NotificationAdapter
import app.unduit.a2achatapp.databinding.FragmentNotificationBinding

import app.unduit.a2achatapp.models.Notification


class NotificationFragment : Fragment() ,View.OnClickListener {

    private lateinit var binding: FragmentNotificationBinding

    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        listeners()

        val list = ArrayList<Notification>()
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())
        list.add(Notification())

        adapter = NotificationAdapter{ item, action ->

        }

        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter

        adapter.submitList(list)

    }
    private fun listeners(){
        binding.btnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_back->{
                requireActivity().onBackPressed()
            }



        }
    }


}