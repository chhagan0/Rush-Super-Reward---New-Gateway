package com.superreward.amount

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


import com.superreward.rushsuperreward.databinding.ActivityAddMoneyBinding
import java.io.IOException
import kotlin.math.abs

class AddMoneyActivity : AppCompatActivity() {



    private val REQUEST_STORAGE_PERMISSION = 1
    private val PICK_IMAGE = 2
    lateinit var binding: ActivityAddMoneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)



        startTimer(2 * 60 * 1000)
        binding.btnCopy.setOnClickListener {
            binding.llUpiapps.visibility=View.VISIBLE
            copyTextToClipboard("chaganswami1@axl")
            Toast.makeText(this, "UPI id copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.lldot.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_STORAGE_PERMISSION
                    )
                } else {
                    openGallery()
                }
            } else {
                openGallery()
            }
        }
        binding.btnGpay.setOnClickListener { openapp("com.google.android.apps.nbu.paisa.user") }
        binding.btnPpay.setOnClickListener { openapp("com.phonepe.app") }
        binding.btnPaytm.setOnClickListener { openapp("net.one97.paytm") }
        binding.btnOther.setOnClickListener { otherapps() }





    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                extractImageTime(selectedImageUri)
//                getPhonePayAmount(selectedImageUri)
                processImage(selectedImageUri)
//                getGpayamount(selectedImageUri)
              }
        }
    }
     private fun extractImageTime(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            if (inputStream != null) {
                val exifInterface = ExifInterface(inputStream)
                val dateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
                if (dateTime != null) {
                    Toast.makeText(this, "Image time: $dateTime", Toast.LENGTH_LONG).show()
                    d("TIME", dateTime)
                } else {
                    Toast.makeText(this, "No date/time information found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to extract image time", Toast.LENGTH_SHORT).show()
        }
    }
    private fun startTimer(durationMillis: Long) {
        object : CountDownTimer(durationMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                // Update the UI with the remaining time
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.tvtime.text = "Timer: $minutes:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                // Update UI when timer finishes
                binding.tvtime.text = "Time's up!"
            }

        }.start()
    }
    @SuppressLint("ServiceCast")
    private fun copyTextToClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)


    }
    private fun openapp(packagename:String) {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packagename)
        intent?.let {
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "App not Installed !!", Toast.LENGTH_SHORT).show()
        }
    }
private fun otherapps(){
      val paymentApps = listOf(
        "com.google.android.apps.nbu.paisa.user",
        "com.phonepe.app",
        "net.one97.paytm",
        "com.paytmmall",
        "in.org.npci.upiapp",
        "in.amazon.mShop.android.shopping",
        "com.csam.icici.bank.imobile",
        "com.sbi.upi",
        "com.myairtelapp",
        "n.code.cashtime",
        "com.icicibank.pockets"
    )
    val packageManager = packageManager
    val intentList = mutableListOf<Intent>()

    // Create intents for each payment app
    for (packageName in paymentApps) {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            intentList.add(it)
        }
    }

    // Check if there are any apps to handle the intent
    if (intentList.isNotEmpty()) {
        // Create chooser dialog with all installed payment apps
        val chooserIntent = Intent.createChooser(Intent(), "Open Payment App")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())

        // Verify there's at least one activity to handle the intent
        if (chooserIntent.resolveActivity(packageManager) != null) {
            startActivity(chooserIntent)
        } else {
            Toast.makeText(
                this,
                "No payment app found",
                Toast.LENGTH_SHORT
            ).show()
        }
    } else {
        Toast.makeText(
            this,
            "No payment app found",
            Toast.LENGTH_SHORT
        ).show()
    }
    }

//    private fun getGpayamount(imageUri: Uri) {
//        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val source = ImageDecoder.createSource(contentResolver, imageUri)
//            ImageDecoder.decodeBitmap(source)
//        } else {
//            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//        }
//
//        val image = InputImage.fromBitmap(bitmap, 0)
//        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//
//        recognizer.process(image)
//            .addOnSuccessListener { visionText ->
//                Log.d("TextRecognition", "Detected text blocks: ${visionText.textBlocks.size}")
//
//                if (visionText.textBlocks.isNotEmpty()) {
//                    // Get the first text block
//                    val firstTextBlock = visionText.textBlocks[0]
//                    // Get the first line of text within the first block
//                    val firstLineText = if (firstTextBlock.lines.isNotEmpty()) {
//                        firstTextBlock.lines[0].text
//                    } else {
//                        "No text found in the first block"
//                    }
//
//                    Log.d("TextRecognition", "First detected line text: $firstLineText")
//                    Toast.makeText(this, firstLineText, Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this, "No text found", Toast.LENGTH_LONG).show()
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("TextRecognition", "Failed to process image", e)
//                Toast.makeText(this, "Failed to process image", Toast.LENGTH_LONG).show()
//            }
//    }


    private fun processImage(imageUri: Uri) {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }

        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                Log.d("TextRecognition", "Detected text blocks: ${visionText.textBlocks.size}")
                var allText = ""

                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        val text = line.text
                        Log.d("TextRecognition", "Detected line text: $text")
                        allText += "$text\n"
                    }
                }

                if (allText.isNotEmpty()) {
                    Toast.makeText(this, allText, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "No text found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image", e)
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_LONG).show()
            }
    }

    private fun getPhonePayAmount(imageUri: Uri) {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }

        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                Log.d("TextRecognition", "Detected text blocks: ${visionText.textBlocks.size}")
                val linesList = mutableListOf<String>()

                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        val text = line.text
                        Log.d("TextRecognition", "Detected line text: $text")
                        linesList.add(text)
                    }
                }

                if (linesList.isNotEmpty()) {
                    val lastLineText = linesList.last()  // Get the last line of text
                    if (lastLineText.all { it.isDigit() } ) {
                        Toast.makeText(this, lastLineText, Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(this, "Please upload payment receipt", Toast.LENGTH_SHORT).show()

                    }

                }
                else {
                    Toast.makeText(this, "No text found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image", e)
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_LONG).show()
            }
    }


}
