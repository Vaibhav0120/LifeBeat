package com.example.lifebeat.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.lifebeat.Domain.DoctorsModel
import com.example.lifebeat.R
import com.example.lifebeat.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
    }

    private fun getBundle() {
        item = intent.getParcelableExtra("Object")!!

        binding.apply {
            titleTxt.text = item.Name
            specialTxt.text = item.Special
            addressTxt.text = item.Address
            bioTxt.text = item.Biography
            patientsTxt.text = item.Patients
            experienceTxt.text = "${item.Experience} Years"
            ratingTxt.text = "${item.Rating}"
            backBtn.setOnClickListener { finish() }

            websiteBtn.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(item.Site)
                startActivity(i)
            }

            messageBtn.setOnClickListener {
                val uri = Uri.parse("smsto:${item.Mobile}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra("sms_body", "Hello ${item.Name}")
                startActivity(intent)
            }

            callBtn.setOnClickListener {
                val uri = Uri.parse("tel:${item.Mobile}")
                val intent = Intent(Intent.ACTION_DIAL, uri)
                startActivity(intent)
            }

            directionBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.Location))
                startActivity(intent)
            }

            shareBtn.setOnClickListener {
                val shareMessage = """
                    Doctor's Profile
                    
                    Dr. ${item.Name}
                    Specialization: ${item.Special}
                    Experience: ${item.Experience} years
                    Contact: ${item.Mobile}
                    Clinic Address: ${item.Address}

                    Dr. ${item.Name} is a trusted medical professional with extensive expertise in ${item.Special}. Known for compassionate care and high standards, ${item.Name} is dedicated to improving patient health and well-being.

                    Book Your Appointment Today
                    Looking for reliable healthcare services? Download LifeBeat—your gateway to seamless doctor consultations, appointment booking, and personalized care.

                    Get LifeBeat on the Play Store: [Play Store Link]

                    Your health. Your choice. LifeBeat.
                """.trimIndent()

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Doctor's Profile")
                intent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(intent, "Choose one"))
            }

            Glide.with(this@DetailActivity).load(item.Picture).into(img)
        }
    }
}
