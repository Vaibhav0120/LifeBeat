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
import com.example.lifebeat.Domain.CategoryModel
import com.example.lifebeat.ViewModel.MainViewModel
import com.example.lifebeat.databinding.FragmentHomeBinding
import com.example.lifebeat.ui.DocListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.lifebeat.R

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

        // Initialize category list and top doctors
        initCategory()
        initTopDoctors()

        // Open ProfileFragment when profile icon is clicked
        binding.profileButton.setOnClickListener {
            openProfileFragment()
        }

        // Move the OnClickListener for seeAllDocList here
        binding.seeAllDocList.setOnClickListener {
            val intent = Intent(requireContext(), DocListActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun openProfileFragment() {
        // Get a reference to the BottomNavigationView in MainActivity
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        // Simulate a click on the profile item in the navigation bar
        bottomNav.selectedItemId = R.id.navigation_profile
    }

    private fun initTopDoctors() {
        binding.apply {
            progressBarTopDoctors.visibility = View.VISIBLE
            viewModel.doctors.observe(viewLifecycleOwner, Observer { doctorList ->
                recyclerViewTopDoctors.layoutManager = LinearLayoutManager(
                    requireContext(),
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
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            // Pass a lambda function to handle category clicks
            binding.viewCategory.adapter = CategoryAdapter(categoryList) { category ->
                onCategorySelected(category)
            }
            binding.progressBarCategory.visibility = View.GONE
        })
        viewModel.loadCategory()
    }

    private fun onCategorySelected(category: CategoryModel) {
        // Filter doctors by selected category and open doctor details or list
        viewModel.doctors.observe(viewLifecycleOwner, Observer { doctorList ->
            val filteredDoctors = doctorList.filter { it.categoryId == category.id }

            if (filteredDoctors.isNotEmpty()) {
                // Start DetailActivity with the first doctor of the selected category
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("Object", filteredDoctors[0]) // Pass the first doctor object
                startActivity(intent)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
