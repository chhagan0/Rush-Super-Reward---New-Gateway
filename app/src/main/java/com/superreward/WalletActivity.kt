package com.superreward.rushsuperreward

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.superreward.rushsuperreward.databinding.ActivityWalletBinding
import com.nkomapp.rupeequiz.Utils.Utils
import com.nkomapp.rupeequiz.Utils.Utils.addWinningAmount
import com.nkomapp.rupeequiz.Utils.Utils.getWinningAmount


class WalletActivity : AppCompatActivity() {
    lateinit var binding: ActivityWalletBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(binding.root)
        val dialog = Dialog(this, R.style.CustomAlertDialog)
        dialog.setContentView(R.layout.balance_dialog)
        dialog.create()
        updatedata()
        val withdrawDialog = Dialog(this, R.style.CustomAlertDialog)
        withdrawDialog.setContentView(R.layout.withdraw_dialog)
        withdrawDialog.create()
        binding.btnBack.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra("HIDE","no")
            startActivity(intent)
            finish()

    }
        binding.imageView7.setOnClickListener {
//            binding.imageView7.setBackgroundColor(Color.parseColor("#E9432B"));
//            binding.imageView8.setBackgroundColor(Color.parseColor("#FFD5D5"));

            binding.outlinedTextField.setHint("Enter Your UPI")
        }
        binding.imageView8.setOnClickListener {
//            binding.imageView8.setBackgroundColor(Color.parseColor("#E9432B"));
//            binding.imageView7.setBackgroundColor(Color.parseColor("#FFD5D5"));
            binding.outlinedTextField.setHint("Enter Your Number")
        }

        withdrawDialog.findViewById<TextView>(R.id.btn_spin_more).setOnClickListener {
            withdrawDialog.dismiss()


        }

        dialog.findViewById<TextView>(R.id.btn_spin_more).setOnClickListener {
            dialog.dismiss()


        }


        binding.button5.setOnClickListener {
            if (binding.outlinedTextField?.text.toString().length < 10) {
                binding.outlinedTextField.error = "Invalid Phone"
                return@setOnClickListener
            }


            if (binding.outlinedTextField1?.text.toString().isEmpty()) {
                binding.outlinedTextField1.error = "Enter Amount"
                return@setOnClickListener
            }


            if (getWinningAmount() <=500) {
                val dialog = Dialog(this, R.style.CustomAlertDialog)
                dialog.setContentView(R.layout.balance_dialog)
                val btn:TextView=dialog.findViewById(R.id.btn_spin_more)
                btn.setOnClickListener { dialog.dismiss() }
                dialog.create()
                 dialog.show()
            } else {
                val progressbar=ProgressDialog(this)
                progressbar.setCancelable(false)
                progressbar.setMessage("Please Wait..")
                progressbar.show()
                val handler= Handler()
                handler.postDelayed(Runnable {
                    updatedata()
                    progressbar.dismiss()
                    withdrawDialog.show()

                },2000)


//                val amount =
//                    getWinningAmount() - binding.outlinedTextField1.editText?.text.toString()
//                        .toInt()
                val amount =0


                addWinningAmount(amount)


            }


        }

    }
fun updatedata(){
    val isUPiAvailable = Utils.getData(this, "spam")


    if (isUPiAvailable == "true") {
        binding.textView19.text = "Minimum withdrawal Amount ₹ 500"
        binding.tvBalance.text = "Total Available ₹ " + getWinningAmount().toString()



    } else {
        binding.textView19.text = "Minimum withdrawal 500 coins"
        binding.tvBalance.text = "Total Available Coin " + getWinningAmount().toString()

    }
}

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent=Intent(this,MainActivity::class.java)
        intent.putExtra("HIDE","no")
        startActivity(intent)
        finish()
    }
}