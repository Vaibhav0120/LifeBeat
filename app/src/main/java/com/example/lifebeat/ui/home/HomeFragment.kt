package com.example.lifebeat.ui.home

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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Use the viewModels delegate to initialize ViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize category list and set up observer
        initCategory()
        initTopDoctors()

        return binding.root
    }

    private fun initTopDoctors() {
        binding.apply {
            progressBarTopDoctors.visibility = View.VISIBLE
            viewModel.doctors.observe(viewLifecycleOwner, Observer { doctorList ->
                recyclerViewTopDoctors.layoutManager = LinearLayoutManager(
                    requireContext(), // Fixed context here
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                recyclerViewTopDoctors.adapter = TopDoctorAdapter(doctorList)
                progressBarTopDoctors.visibility = View.GONE
            })
            viewModel.loadDoctors()
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.category.observe(viewLifecycleOwner, Observer { categoryList ->
            binding.viewCategory.layoutManager = LinearLayoutManager(
                requireContext(), // Use requireContext() here as well
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.viewCategory.adapter = CategoryAdapter(categoryList)
            binding.progressBarCategory.visibility = View.GONE
        })
        viewModel.loadCategory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
