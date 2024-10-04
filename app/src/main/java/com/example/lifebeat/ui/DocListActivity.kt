package com.example.lifebeat.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lifebeat.Adapter.DocListAdapter
import com.example.lifebeat.MainActivity
import com.example.lifebeat.ViewModel.MainViewModel
import com.example.lifebeat.databinding.ActivityDocListBinding

class DocListActivity : MainActivity() {

    private lateinit var binding: ActivityDocListBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDocList()
    }

    private fun initDocList() {
        binding.apply {
            progressBarDocList.visibility = View.VISIBLE
            viewModel.doctors.observe(this@DocListActivity, Observer {
                viewDocList.layoutManager =
                    LinearLayoutManager(this@DocListActivity, LinearLayoutManager.VERTICAL, false)
                viewDocList.adapter = DocListAdapter(it)
                progressBarDocList.visibility = View.GONE
            })
            viewModel.loadDoctors()

            backBtn.setOnClickListener {  finish() }
        }
    }
}
