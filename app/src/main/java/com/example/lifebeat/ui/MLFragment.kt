package com.example.lifebeat.ui

import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.lifebeat.R
import com.google.firebase.database.FirebaseDatabase
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.InputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MLFragment : Fragment() {
    private lateinit var tflite: Interpreter
    private var selectedImageUri: Uri? = null
    private lateinit var patientNameEditText: EditText
    private lateinit var contactNumberEditText: EditText
    private lateinit var predictionResultCard: CardView
    private lateinit var predictionResultText: TextView
    private var imageCounter = 0 // Counter to track image selections

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tflite = Interpreter(loadModelFile())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ml, container, false)

        patientNameEditText = view.findViewById(R.id.Patient_name)
        contactNumberEditText = view.findViewById(R.id.Contact_number)
        predictionResultCard = view.findViewById(R.id.prediction_result_card)
        predictionResultText = view.findViewById(R.id.prediction_result_text)

        // Initialize buttons
        val uploadButtonMe: Button = view.findViewById(R.id.upload_button_me)
        val uploadButton: Button = view.findViewById(R.id.upload_button)
        val BookAppointmentBtn: CardView = view.findViewById(R.id.Book_Appointment_Btn)

        // Click listener for the first upload button (no name validation)
        uploadButtonMe.setOnClickListener {
            // Just open the image gallery without validating the name
            saveDataToFirebase() // Optional: If you want to save data regardless
            openImageGallery()
        }

        // Click listener for the second upload button (with name validation)
        uploadButton.setOnClickListener {
            if (validateInputs()) {
                saveDataToFirebase()
                openImageGallery()
            }
        }

        // Click listener for the new button to open a specific activity
        BookAppointmentBtn.setOnClickListener {
            val intent = Intent(requireContext(), DocListActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun validateInputs(): Boolean {
        if (patientNameEditText.text.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter patient name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (contactNumberEditText.text.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter contact number", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveDataToFirebase() {
        val database = FirebaseDatabase.getInstance().reference.child("users")
        val patientName = patientNameEditText.text.toString()
        val contactNumber = contactNumberEditText.text.toString()

        val patientId = "test1"
        val patientData = Patient(patientId, patientName, contactNumber)

        // Uncomment if you want to save data
        // database.child(patientId).setValue(patientData)
        //     .addOnSuccessListener {
        //         Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show()
        //     }
        //     .addOnFailureListener {
        //         Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()
        //     }
    }

    private fun openImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                runInference(uri)
            } ?: run {
                Log.e("MLFragment", "Data is null in onActivityResult")
                Toast.makeText(requireContext(), "Failed to get image data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = requireActivity().assets.openFd("cancer_detection.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength

        Log.d("MLFragment", "Model file loaded successfully.")
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val inputArray = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resizedBitmap.getPixel(x, y)
                inputArray[0][y][x][0] = (Color.red(pixel) / 255.0f)
                inputArray[0][y][x][1] = (Color.green(pixel) / 255.0f)
                inputArray[0][y][x][2] = (Color.blue(pixel) / 255.0f)
            }
        }
        return inputArray
    }

    private fun runInference(imageUri: Uri) {
        val inputStream: InputStream? = selectedImageUri?.let { requireActivity().contentResolver.openInputStream(it) }
        val bitmap = BitmapFactory.decodeStream(inputStream)

        if (bitmap != null) {
            val input = preprocessImage(bitmap)
            val output = Array(1) { FloatArray(3) }

            try {
                Log.d("MLFragment", "Running inference with input shape: ${input.size} x ${input[0].size} x ${input[0][0].size}")
                tflite.run(input, output)

                val probabilities = output[0]
                val maxProbability = probabilities.maxOrNull() ?: 0f
                val maxIndex = probabilities.toList().indexOf(maxProbability)

                val message: String
                val backgroundColor: Int
                val percentage = maxProbability * 100

                when {
                    percentage > 90 -> {
                        message = String.format("%.2f%% matched - seek immediate assistance", percentage)
                        backgroundColor = Color.RED
                    }
                    percentage < 40 -> {
                        message = String.format("%.2f%% matched - You do not need to worry", percentage)
                        backgroundColor = Color.GREEN
                    }
                    else -> {
                        message = String.format("%.2f%% matched - seek assistance", percentage)
                        backgroundColor = Color.rgb(255, 165, 0) // Orange
                    }
                }

                Log.d("MLFragment", "Model output: ${probabilities.joinToString(", ")}, Max Probability: $maxProbability")
                predictionResultText.text = message
                predictionResultCard.setCardBackgroundColor(backgroundColor)
                predictionResultCard.visibility = View.VISIBLE

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("MLFragment", "Error during inference: ${e.message}", e)
                Toast.makeText(requireContext(), "Inference failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("MLFragment", "Failed to decode bitmap from input stream")
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tflite.close() // Release TensorFlow Lite resources
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}

data class Patient(val id: String, val name: String, val contact: String)