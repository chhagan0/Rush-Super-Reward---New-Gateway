package com.superreward.rushsuperreward

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.superreward.rushsuperreward.databinding.ActivityLoginBinding
import com.nkomapp.rupeequiz.Utils.Utils


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Account Creating...")
        progressDialog.setCancelable(false)
        binding.btnLogin.setOnClickListener {
            if (binding.etUserName.text.isEmpty()) {
                Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show()
            } else if (binding.etUserNumber.text.isEmpty() || binding.etUserNumber.text.length != 10) {
                Toast.makeText(this, "Enter A Valid Number", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog.show()
                Utils.saveData(this, "username", binding.etUserName.text.toString())
                Utils.saveData(this, "usernumber", binding.etUserNumber.text.toString())
                Utils.saveData(this, "spin", "50")
                Utils.saveData(this, "imagename", "nomame")

                val handler = Handler()
                handler.postDelayed(Runnable {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account Created Successfully!!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }, 3000)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}