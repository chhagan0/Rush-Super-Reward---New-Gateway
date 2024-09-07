package com.superreward.rushsuperreward

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.impl.utils.checkWakeLocks
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.superreward.rushsuperreward.Adapter.NumberAdapter
import com.superreward.rushsuperreward.Adapter.SlowlyLinearLayoutManager
import com.superreward.Fragments.Spin
import com.superreward.rushsuperreward.databinding.ActivityMainBinding
import com.nkomapp.rupeequiz.Utils.Utils
import com.nkomapp.rupeequiz.Utils.Utils.addWinningAmount
import com.nkomapp.rupeequiz.Utils.Utils.getWinningAmount
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.Handler

class MainActivity : AppCompatActivity() {

    var db = FirebaseFirestore.getInstance()
    private lateinit var analytics: FirebaseAnalytics
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var adapter: NumberAdapter
    lateinit var binding: ActivityMainBinding
    val ONESIGNAL_APP_ID = "a093f4ce-a18f-44cb-b71e-45d9c78c961e"
    private val numbers = mutableListOf<String>()
    var amount = ""
    var paymentamount = ""
    var paymentapp = ""
    var uri: Uri? = null
    var imagetime = ""
    var ssValid = false
    private val REQUEST_STORAGE_PERMISSION = 1
    private val PICK_IMAGE = 2
    var imagename = ""
    var upi = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)
        fetchdata()
        val intent = intent.getStringExtra("HIDE")
        val getam = Utils.getData(this, "AMOUNT")
        amount = getam.toString()


        if (intent == "getway") {
            val upiid = Utils.getData(this, "UPI")
            val qrBitmap = upiid?.let { generateUPIQRCode(it, amount) }
            binding.ivQR.setImageBitmap(qrBitmap)
            binding.tvamount.setText("₹ $amount")
            binding.layoutMain.visibility = View.GONE
            binding.layoutGetway.visibility = View.VISIBLE
        } else {
            binding.layoutMain.visibility = View.VISIBLE
            binding.layoutGetway.visibility = View.GONE
        }


        clicklistners()
        updatadata()
        replaceFragment(Spin())
        menuItemClick()
        notification()
        checkspam()
        oneSignal()
        binding.ll1.setOnClickListener { replaceFragment2(Spin()) }
        binding.withdraw.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    WalletActivity::class.java
                )
            )

        }
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_open, R.string.nav_close)
        actionBarDrawerToggle!!.syncState()
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        binding.menuBtn.setOnClickListener {
            if (binding.drawerLayout != null) {
                binding.drawerLayout!!.openDrawer(GravityCompat.START, true)
            }
        }
    }

    private fun clicklistners() {
        binding.btnSubmit.setOnClickListener {
            if (paymentamount == null || paymentamount == "0") {
                paymentamount = amount

            }

            val pImgname = Utils.getData(this, "imagename")
            if (paymentapp == "phonepay") {
                if (ssValid == true && paymentamount.toInt() > 1 && pImgname != imagename) {
                    Utils.saveData(this, "imagename", imagename)
                    paymentsuccess()

                } else {
                    Toast.makeText(this, "This Screenshot Not Valid", Toast.LENGTH_SHORT)
                        .show()
                    paymentfailed()
                }
            } else if (paymentapp == "googlepay") {
                if (ssValid == true && paymentamount.toInt() > 1 && pImgname != imagename) {

                    Utils.saveData(this, "imagename", imagename)
                    paymentsuccess()

                } else {
                    Toast.makeText(this, "This Screenshot Not Valid", Toast.LENGTH_SHORT)
                        .show()
                    paymentfailed()
                }
            } else if (paymentapp == "paytm" || paymentamount.all { it.isDigit() }) {
                if (ssValid == true && pImgname != imagename) {

                    Utils.saveData(this, "imagename", imagename)
                    paymentsuccess()
                } else {
                    Toast.makeText(this, "This Screenshot Not Valid", Toast.LENGTH_SHORT)
                        .show()
                    paymentfailed()
                }
            } else {
                if (ssValid == true && pImgname != imagename) {

                    Utils.saveData(this, "imagename", imagename)
                    paymentsuccess()

                } else {
                    Toast.makeText(this, "This Screenshot Not Valid", Toast.LENGTH_SHORT)
                        .show()
                    paymentfailed()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            binding.layoutGetway.visibility = View.GONE
            binding.layoutMain.visibility = View.VISIBLE
            finish()

        }
        binding.llUpiapps.setOnClickListener {
            paymentapp = "paytm"

            binding.layoutPay.visibility = View.VISIBLE
        }

        startTimer(3 * 60 * 1000)
        binding.btnCopy.setOnClickListener {
            binding.llUpiapps.visibility = View.VISIBLE
            copyTextToClipboard(upi)
            Toast.makeText(this, "UPI id copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.lldot.setOnClickListener {
            checkAndRequestPermissions()
        }
        binding.btnGpay.setOnClickListener {
            paymentapp = "googlepay"
            openapp("com.google.android.apps.nbu.paisa.user")
        }
        binding.btnPpay.setOnClickListener {
            paymentapp = "phonepay"
            openapp("com.phonepe.app")
        }
        binding.btnPaytm.setOnClickListener {
            paymentapp = "paytm"
            openapp("net.one97.paytm")
        }
        binding.btnOther.setOnClickListener {
            paymentapp = "other"
            otherapps()
        }

    }

    private fun checkAndRequestPermissions() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Android 11 and below
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            requestPermission(Manifest.permission.READ_MEDIA_IMAGES)
        }


    }


    private fun checkspam() {
        val spam = Utils.getData(this, "spam")
        if (spam == "false") {
            binding.depositLayout.visibility = View.GONE
            binding.view1.visibility = View.GONE
            binding.ll1.visibility = View.GONE
            binding.ivCoin.visibility = View.VISIBLE
        }
    }

    private fun notification() {
        val spam = Utils.getData(this, "spam")
        if (spam == "true") {
            numbers.addAll(
                listOf(
                    "+91 922****569 Successfully withdraw  950.00!",
                    "+91 924****167 Successfully withdraw  563.75!",
                    "+91 927****843 Successfully withdraw  734.50!",
                    "+91 925****701 Successfully withdraw  1789.25!",
                    "+91 928****462 Successfully withdraw  632.90!",
                    "+91 923****935 Successfully withdraw  763.00!",
                    "+91 929****154 Successfully withdraw  889.15!",
                    "+91 921****578 Successfully withdraw  1067.80!",
                    "+91 926****320 Successfully withdraw  674.30!",
                    "+91 920****786 Successfully withdraw  833.70!",
                    "+91 923****112 Successfully withdraw  891.45!",
                    "+91 925****689 Successfully withdraw  856.20!",
                    "+91 928****547 Successfully withdraw  827.60!",
                    "+91 924****365 Successfully withdraw  820.95!",
                    "+91 929****978 Successfully withdraw  1950.10!",
                    "+91 921****780 Successfully withdraw  678.75!",
                    "+91 926****236 Successfully withdraw  832.30!",
                    "+91 920****845 Successfully withdraw  886.25!",
                    "+91 923****694 Successfully withdraw  789.50!",
                    "+91 927****143 Successfully withdraw  543.20!",
                    "+91 929****990 Successfully withdraw  810.70!",
                    "+91 924****658 Successfully withdraw  1345.00!",
                    "+91 921****426 Successfully withdraw  897.45!",
                    "+91 928****874 Successfully withdraw  678.30!",
                    "+91 925****513 Successfully withdraw  834.10!",
                    "+91 920****379 Successfully withdraw  732.50!",
                    "+91 923****745 Successfully withdraw  832.70!",
                    "+91 926****912 Successfully withdraw  887.80!",
                    "+91 929****165 Successfully withdraw  967.25!",
                    "+91 927****287 Successfully withdraw  512.90!",
                    "+91 928****578 Successfully withdraw  867.45!",
                    "+91 921****834 Successfully withdraw  789.60!",
                    "+91 925****654 Successfully withdraw  843.25!",
                    "+91 920****123 Successfully withdraw  976.30!",
                    "+91 924****985 Successfully withdraw  634.70!",
                    "+91 926****547 Successfully withdraw  889.80!",
                    "+91 929****768 Successfully withdraw  1023.50!",
                    "+91 923****452 Successfully withdraw  856.20!",
                    "+91 927****231 Successfully withdraw  807.90!",
                    "+91 928****873 Successfully withdraw  976.40!",
                    "+91 925****789 Successfully withdraw  634.90!",
                    "+91 929****567 Successfully withdraw  878.20!",
                    "+91 921****329 Successfully withdraw  932.75!",
                    "+91 927****589 Successfully withdraw  543.10!",
                    "+91 923****673 Successfully withdraw  856.80!",
                    "+91 926****698 Successfully withdraw  898.70!",
                    "+91 920****245 Successfully withdraw  765.80!",
                    "+91 924****514 Successfully withdraw  810.40!",
                    "+91 928****457 Successfully withdraw  876.90!",
                    "+91 929****321 Successfully withdraw  543.60!"
                )
            )
        } else {
            numbers.addAll(
                listOf(
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                    getString(R.string.note),
                )
            )
        }
        adapter = NumberAdapter(numbers)

        val timer = object : CountDownTimer(2000, 1000) {
            override fun onFinish() {
                binding.recyclerView.post {
                    binding.recyclerView.smoothScrollToPosition(49)
                }
            }

            override fun onTick(p0: Long) {
                binding.recyclerView.post {
                    binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        object : CountDownTimer(1000, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                timer.start()
            }
        }.start()

        binding.recyclerView.layoutManager =
            SlowlyLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter

    }

    private fun menuItemClick() {
        val menu = binding.navView?.menu
        val home = menu?.findItem(R.id.nav_home)

        val privacy = menu?.findItem(R.id.nav_policy)
        val terms = menu?.findItem(R.id.nav_terms)
        val exit = menu?.findItem(R.id.nav_exit)
        val headerView = binding.navView.getHeaderView(0)
        val username = headerView.findViewById<TextView>(R.id.tvusername)
        val usernumber = headerView.findViewById<TextView>(R.id.tvusernumber)
        val name = Utils.getData(this, "username")
        val number = Utils.getData(this, "usernumber")
        username.setText(name.toString())
        usernumber.setText(number.toString())

        home?.setOnMenuItemClickListener {
            binding.drawerLayout?.closeDrawers()
            true
        }





        privacy?.setOnMenuItemClickListener {
            binding.drawerLayout?.closeDrawers()
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/pocket-paisa")
                )
            )
            true
        }


        terms?.setOnMenuItemClickListener {
            binding.drawerLayout?.closeDrawers()
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/pocket-paisa")
                )
            )
            true
        }




        exit?.setOnMenuItemClickListener {
            binding.drawerLayout?.closeDrawers()
            finishAffinity()
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val manager: FragmentManager = supportFragmentManager
        val transation = manager.beginTransaction()
        transation.replace(R.id.frameLayout, fragment)
        transation.commit()
    }


    private fun replaceFragment2(fragment: Fragment) {

        val manager: FragmentManager = supportFragmentManager
        val transation = manager.beginTransaction()
        fragment.arguments = Bundle().apply {
            putString("pos", "hide")
        }
        transation.replace(R.id.frameLayout, fragment)
        transation.commit()
    }

    fun updatadata() {
        val spin = Utils.getData(this, "spin").toString()

        val pos = Utils.getData(this, "spam")
        if (pos == "false") {
            binding.userBalance.text = getWinningAmount().toString()
            binding.depositBalance.text = spin

        } else {
            binding.userBalance.text = "₹ " + getWinningAmount().toString()
            binding.depositBalance.text = "₹ " + spin


        }
    }

    override fun onStart() {
        val spin = Utils.getData(this, "spin").toString()

        updatadata()
        val pos = Utils.getData(this, "spam")
        pos.let {
            if (it == "true") {
                binding.depositBalance.text = "₹ " + spin
                binding.userBalance.text = "₹ " + getWinningAmount().toString()


            } else {
                binding.depositBalance.text = spin
                binding.userBalance.text = getWinningAmount().toString()
            }


        }
        super.onStart()
    }

    private fun oneSignal() {
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)


        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun requestPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionRationale(permission)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    REQUEST_STORAGE_PERMISSION
                )
            }
        } else {
            openGallery()
            // Permission already granted
            // Do your work here
        }
    }

    private fun showPermissionRationale(permission: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This permission is needed for the app to function correctly.")
            .setPositiveButton("Allow") { dialog, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    REQUEST_STORAGE_PERMISSION
                )
                dialog.dismiss()
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                binding.tvssmsg.setText("Screenshot selected successfully")
                Toast.makeText(
                    this,
                    "Submit your payment screenshot for quick verification!",
                    Toast.LENGTH_SHORT
                ).show()
                uri = selectedImageUri
                binding.ivSelected.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.selected
                    )
                );
                extractImageTime(selectedImageUri)
                imagename = getImageNameFromUri(this, selectedImageUri).toString()

                getGpayamount(selectedImageUri)
                checkImageTime()

                if (paymentamount.all { it.isDigit() }) {

                } else {
                    getPhonePayAmount(selectedImageUri)

                }


//                processImage(selectedImageUri)
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
                    // Split the date-time string to get the time part
                    val time =
                        dateTime.split(" ")[1] // The time is the second part of the split string
                    imagetime = time
                    // Display the time using a toast message
//                    Toast.makeText(this, "Image time: $time", Toast.LENGTH_LONG).show()
                    Log.d("TIME", time)
                } else {
//                    Toast.makeText(this, "No date/time information found", Toast.LENGTH_SHORT)
//                        .show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
//            Toast.makeText(this, "Failed to extract image time", Toast.LENGTH_SHORT).show()
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
                timeUpdialog()
            }

        }.start()
    }

    @SuppressLint("ServiceCast")
    private fun copyTextToClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)


    }

    private fun openapp(packagename: String) {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packagename)
        intent?.let {
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "App not Installed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun otherapps() {
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

    private fun getGpayamount(imageUri: Uri) {
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

                if (visionText.textBlocks.isNotEmpty()) {
                    // Get the first text block
                    val firstTextBlock = visionText.textBlocks[0]
                    // Get the first line of text within the first block
                    val firstLineText = if (firstTextBlock.lines.isNotEmpty()) {
                        val text = firstTextBlock.lines[0].text

                        // Regular expression to remove trailing zeros after the decimal point
                        val cleanedText = text.replace(Regex("""\.?0*$"""), "")
                        if (cleanedText.all { it.isDigit() }) {
                            paymentamount = cleanedText

                        } else {
                        }
                        if (cleanedText.all { it.isDigit() }) {
                            paymentapp = "googlepay"

                        } else {

                        }

                    } else {
                        paymentamount = "0"

                        "No text found in the first block"
                    }
                    Log.d("TextRecognition", "First detected line text: $firstLineText")
//                    Toast.makeText(this, firstLineText, Toast.LENGTH_LONG).show()
                } else {
//                    Toast.makeText(this, /"No text found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image", e)
//                Toast.makeText(this, "Failed to process image", Toast.LENGTH_LONG).show()
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
                    val lastLineText = linesList.last().replace(",", "").replace(".", "")

                    if (lastLineText.all { it.isDigit() }) {
                        paymentamount = lastLineText

                        if (lastLineText.all { it.isDigit() }) {
                            paymentapp = "phonepay"

                        }

                    } else {
                        ssValid = false
//                        Toast.makeText(this, "Please upload your payment reciept to proceed!", Toast.LENGTH_SHORT)
//                            .show()

                    }

                } else {
                    paymentamount = "0"

//                    Toast.makeText(this, "No text found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Failed to process image", e)
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkImageTime() {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        try {
            // Parse the extracted image time
            val imageTimeDate = dateFormat.parse(imagetime)
            // Format the current time to the same format
            val currentTimeString = dateFormat.format(currentTime)
            val currentTimeDate = dateFormat.parse(currentTimeString)

            if (imageTimeDate != null && currentTimeDate != null) {
                // Calculate the time difference in milliseconds
                val timeDifference = currentTimeDate.time - imageTimeDate.time

                // Convert milliseconds to minutes
                val timeDifferenceInMinutes = timeDifference / (60 * 1000)

                // Check if the difference is within 2 minutes
                if (timeDifferenceInMinutes in 0..3) {

                    ssValid = true
                } else {

                    ssValid = false
                }

                Log.d(
                    "TIME",
                    "Image time: $imagetime, Current time: $currentTimeString, Difference in minutes: $timeDifferenceInMinutes"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
//            Toast.makeText(this, "Failed to compare times", Toast.LENGTH_SHORT).show()
        }
    }

    fun getImageNameFromUri(context: Context, uri: Uri): String? {
        var imageName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    imageName = it.getString(nameIndex)
                }
            }
        }
        return imageName
    }

    private fun paymentsuccess() {
        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        dialog.setMessage("Payment Verifying...")
        dialog.show()
        val handler = android.os.Handler()
        handler.postDelayed(
            {

                val previoudam = Utils.getData(this, "spin")
                if (paymentamount != null) {
                    val totalam = previoudam!!.toInt() + amount.toInt()
                    Utils.saveData(this, "spin", totalam.toString())

                }

                updatadata()
                dialog.dismiss()
                successdialog()
                handler.postDelayed({
                    binding.layoutGetway.visibility = View.GONE
                    binding.layoutMain.visibility = View.VISIBLE
                }, 3000)
            }, 3000
        )
    }

    private fun paymentfailed() {
        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        dialog.setMessage("Payment Verifying...")
        dialog.show()

        val handler = android.os.Handler()
        handler.postDelayed(
            {
                dialog.dismiss()
                faileddialog()


                handler.postDelayed({
                    binding.layoutGetway.visibility = View.GONE
                    binding.layoutMain.visibility = View.VISIBLE
                }, 3000)
            }, 3000
        )
    }

    private fun generateUPIQRCode(upiId: String, amount: String): Bitmap? {
        val upiString = "upi://pay?pa=$upiId&am=$amount"
        val size = 512 // QR code image size
        val qrCodeWriter = QRCodeWriter()
        return try {
            val bitMatrix = qrCodeWriter.encode(upiString, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun successdialog() {
        val dialog = Dialog(this, R.style.CustomAlertDialog)
        dialog.setContentView(R.layout.win_dialog)
        dialog.findViewById<ImageView>(R.id.top_image)
            .setImageDrawable(getDrawable(R.drawable.check))
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.text1).text =
            "₹$amount successfully added to your wallet"
        dialog.create()
        dialog.show()


        val handler = android.os.Handler()
        handler.postDelayed({

            finish()
            dialog.dismiss()
        }, 3000)

        dialog.findViewById<TextView>(R.id.btn_spin_more).visibility = View.INVISIBLE


    }

    private fun faileddialog() {
        val dialog = Dialog(this, R.style.CustomAlertDialog)
        dialog.setContentView(R.layout.win_dialog)
        dialog.findViewById<ImageView>(R.id.top_image)
            .setImageDrawable(getDrawable(R.drawable.failed))
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.text1).setText("Screenshot went wrong !! Try again")
        dialog.findViewById<TextView>(R.id.text2)
            .setText("Invalid screenshot detected, Please try with a new Payment Screenshot")

        dialog.create()
        dialog.show()
        val handler = android.os.Handler()
        handler.postDelayed({
            finish()
            dialog.dismiss()
        }, 3000)



        dialog.findViewById<TextView>(R.id.btn_spin_more).visibility = View.INVISIBLE


    }

    private fun timeUpdialog() {
        val dialog = Dialog(this, R.style.CustomAlertDialog)
        dialog.setContentView(R.layout.win_dialog)
        dialog.findViewById<ImageView>(R.id.top_image)
            .setImageDrawable(getDrawable(R.drawable.clock))
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.text1).text = "OOPs !! Time up"
        dialog.findViewById<TextView>(R.id.text2).setText("Something went wrong")
        dialog.create()
        dialog.show()
        val handler = android.os.Handler()
        handler.postDelayed({

            binding.layoutGetway.visibility = View.GONE
            binding.layoutMain.visibility = View.VISIBLE
            startActivity(Intent(this, MainActivity::class.java))
            dialog.dismiss()
        }, 3000)
        dialog.findViewById<TextView>(R.id.btn_spin_more).visibility = View.INVISIBLE
    }
    fun fetchdata() {
        analytics = Firebase.analytics
        val ref = db.collection("data").document("upi")
        ref.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    var upii = document.getString("Upi")
                    binding.tvupi.setText(upii)
                    Utils.saveData(this, "UPI", upii.toString())
                    upi = upii.toString()
                }
            }
    }

}