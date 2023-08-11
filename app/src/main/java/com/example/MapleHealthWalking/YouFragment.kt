package com.example.MapleHealthWalking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.MapleHealthWalking.databinding.FragmentYouBinding


class YouFragment : Fragment() {

    private val TAG: String = this::class.java.simpleName
    private var _binding: FragmentYouBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentYouBinding.inflate(inflater, container, false)
        var image: Int = when (Global.processState) {
            true -> R.drawable.running
            false -> R.drawable.watching_tv
        }

        Glide.with(requireContext()).asGif().load(image).into(binding.youImageStatus)

        binding.youStartBtn.setOnClickListener {
            when (Global.processState) {
                true -> {
                    println("我們出發!!!!!")
                    Global.processState = false
                    Glide.with(requireContext()).asGif().load(R.drawable.watching_tv)
                        .into(binding.youImageStatus)
                    binding.youStartBtn.text = this.getString(R.string.start)
                }

                false -> {
                    println("我不行了 好累!!!!!")
                    Global.processState = true
                    Glide.with(requireContext()).asGif().load(R.drawable.running)
                        .into(binding.youImageStatus)
                    binding.youStartBtn.text = this.getString(R.string.end)
                }
            }
        }
        Global.GlobalVariable.stepCount.observe(viewLifecycleOwner, Observer { newVal ->
            binding.youStepCountTextView.text = newVal.toString()
        })
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}