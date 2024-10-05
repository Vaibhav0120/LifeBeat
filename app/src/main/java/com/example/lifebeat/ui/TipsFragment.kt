package com.example.lifebeat.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifebeat.Adapter.CategoryAdapter
import com.example.lifebeat.Adapter.TopDoctorAdapter
import com.example.lifebeat.ViewModel.MainViewModel
import com.example.lifebeat.databinding.FragmentHomeBinding
import com.example.lifebeat.databinding.FragmentTipsBinding
import com.example.lifebeat.ui.DocListActivity

class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
