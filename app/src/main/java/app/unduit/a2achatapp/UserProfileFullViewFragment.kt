package app.unduit.a2achatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import app.unduit.a2achatapp.databinding.FragmentFullViewImageBinding
import app.unduit.a2achatapp.databinding.FragmentUserProfileFullViewBinding
import com.bumptech.glide.Glide


class UserProfileFullViewFragment : Fragment() {
    private val args: UserProfileFullViewFragmentArgs by navArgs()
    private lateinit var binding: app.unduit.a2achatapp.databinding.FragmentUserProfileFullViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileFullViewBinding.inflate(inflater)
        binding.lifecycleOwner = this

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init(){

            Log.e("Tagss23","url" + args.chatUserImage)

        Glide.with(this).load(args.chatUserImage)

            .placeholder(R.drawable.ic_deafult_profile_icon)
            .into(binding.userImage)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()

        }
    }


}