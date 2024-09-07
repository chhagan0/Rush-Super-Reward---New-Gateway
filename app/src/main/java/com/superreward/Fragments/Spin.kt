package com.superreward.Fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.superreward.rushsuperreward.MainActivity
import com.superreward.rushsuperreward.R
 import com.nkomapp.rupeequiz.Utils.Utils
import com.nkomapp.rupeequiz.Utils.Utils.addDepositAmount
import com.nkomapp.rupeequiz.Utils.Utils.addWinningAmount
import com.nkomapp.rupeequiz.Utils.Utils.getDepositAmount
import com.nkomapp.rupeequiz.Utils.Utils.getWinningAmount
import com.google.android.material.card.MaterialCardView
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.superreward.rushsuperreward.databinding.FragmentSpinBinding
import kotlin.random.Random


class Spin : Fragment() {
    var mAmount = 0
    var amount1: Int = 0
    var amount2: Int = 0
    var amount3: Int = 0
    var amount4: Int = 0
    var amount5: Int = 0
    var amount6: Int = 0
    var totalamount: Int = 0

    lateinit var binding: FragmentSpinBinding
    private var spinWheel: ImageView? = null
    private var player: MediaPlayer? = null
    private var coin = 0
    var pos = ""

    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpinBinding.inflate(layoutInflater, container, false)
        finditem()
        isConnectionAvailable(requireContext())
        val time = Utils.getData(requireContext(), "time")!!.toInt()
        if (time == 1) {

            setupAmountOnEditText()
            clickLister()
            binding.editTextText.setText("₹ " + amount2.toString())

            com.nkomapp.rupeequiz.Utils.Utils.saveData(requireContext(), "time", "2")
            binding.textView11.setText("Minimum Deposit Amount ₹ $amount1/-")


        } else {

            updateamountonedittext()
            clickLister()

            binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))
            binding.editTextText.setText("₹ " + amount2.toString())
            binding.textView11.setText("Minimum Deposit Amount ₹ $amount1/-")


        }
        btnclickLister()



         val data = arguments?.getString("pos")
        if (data == "hide") {
            updateLayout()
        }
        binding.button3.setOnClickListener {

            val intent=Intent(requireContext(),MainActivity::class.java)
            intent.putExtra("HIDE","getway")
            Utils.saveData(requireContext(),"AMOUNT",mAmount.toString())
            intent.putExtra("AMOUNT",mAmount.toString())
            startActivity(intent)
//            binding.spinLayout.visibility=View.VISIBLE
//            binding.cardView.visibility=View.GONE
         }
        (activity as MainActivity).updatadata()
        val position = Utils.getData(requireContext(), "spam")
        pos = position.toString()
        binding.btnSpinnow.setOnClickListener {
            val spin = Utils.getData(requireContext(), "spin")!!.toInt()
            if (pos == "false") {
                val random = Random.nextInt(1, 14)
                spinNow(random, pos)

            } else {

                if (spin >= 10) {
                    startSpin()
                } else {
                    showDialog()
                }
            }

        }
        binding.spinBtn.setOnClickListener {
            val spin = Utils.getData(requireContext(), "spin")!!.toInt()

            if (pos == "false") {
                val random = Random.nextInt(1, 14)
                spinNow(random, pos)

            } else {

                if (spin >= 10) {
                    startSpin()
                } else {
                    showDialog()
                }
            }
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                handleBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    private fun updateamountonedittext() {
        amount1 = Utils.getData(requireContext(), "amount1")!!.toInt()
        amount2 = Utils.getData(requireContext(), "amount2")!!.toInt()
        amount3 = Utils.getData(requireContext(), "amount3")!!.toInt()
        amount4 = Utils.getData(requireContext(), "amount4")!!.toInt()
        amount5 = Utils.getData(requireContext(), "amount5")!!.toInt()
        amount6 = Utils.getData(requireContext(), "amount6")!!.toInt()
        val amount11 = Utils.getData(requireContext(), "amount1")!!.toInt()
        val amount12 = Utils.getData(requireContext(), "amount2")!!.toInt()
        val amount13 = Utils.getData(requireContext(), "amount3")!!.toInt()
        val amount14 = Utils.getData(requireContext(), "amount4")!!.toInt()
        val amount15 = Utils.getData(requireContext(), "amount5")!!.toInt()
        val amount16 = Utils.getData(requireContext(), "amount6")!!.toInt()
        binding.editTextText2.setText("₹ " + amount11.toString())
        binding.editTextText3.setText("₹ " + amount12.toString())
        binding.editTextText4.setText("₹ " + amount13.toString())
        binding.editTextText21.setText("₹ " + amount14.toString())
        binding.editTextText31.setText("₹ " + amount15.toString())
        binding.editTextText41.setText("₹ " + amount16.toString())
    }

    private fun btnclickLister() {
        binding.editTextText2.setOnClickListener {
            binding.editTextText2.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))
            binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText4.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText21.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText31.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText41.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))



            binding.editTextText2.setTextColor(requireContext().getColor(R.color.white))
            binding.editTextText3.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText4.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText21.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText31.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText41.setTextColor(requireContext().getColor(R.color.black))


            setupAmountOnEdittext(amount1)
            totalamount = mAmount
            binding.editTextText.setText("₹ " + amount1.toString())


        }

        binding.editTextText3.setOnClickListener {
            binding.editTextText2.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))
            binding.editTextText4.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText21.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText31.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText41.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))



            binding.editTextText2.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText3.setTextColor(requireContext().getColor(R.color.white))
            binding.editTextText4.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText21.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText31.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText41.setTextColor(requireContext().getColor(R.color.black))

            val total = mAmount + 11
            totalamount = total
            setupAmountOnEdittext(amount2)
            binding.editTextText.setText("₹ " + amount2.toString())


        }

        binding.editTextText4.setOnClickListener {
            binding.editTextText2.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText4.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))
            binding.editTextText21.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText31.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText41.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))



            binding.editTextText2.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText3.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText4.setTextColor(requireContext().getColor(R.color.white))
            binding.editTextText21.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText31.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText41.setTextColor(requireContext().getColor(R.color.black))

            setupAmountOnEdittext(amount3)
            val total = mAmount + 33
            totalamount = total
            binding.editTextText.setText("₹ " + amount3.toString())


        }

        binding.editTextText21.setOnClickListener {
            binding.editTextText2.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText4.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText21.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))
            binding.editTextText31.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText41.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))



            binding.editTextText2.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText3.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText4.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText21.setTextColor(requireContext().getColor(R.color.white))
            binding.editTextText31.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText41.setTextColor(requireContext().getColor(R.color.black))


            setupAmountOnEdittext(amount4)
            val total = mAmount + 52
            totalamount = total
            binding.editTextText.setText("₹ " + amount4.toString())

        }


        binding.editTextText31.setOnClickListener {
            binding.editTextText2.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText4.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText21.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText31.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))
            binding.editTextText41.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))



            binding.editTextText2.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText3.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText4.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText21.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText31.setTextColor(requireContext().getColor(R.color.white))
            binding.editTextText41.setTextColor(requireContext().getColor(R.color.black))

            setupAmountOnEdittext(amount5)
            val total = mAmount + 105
            totalamount = total
            binding.editTextText.setText("₹ " + amount5.toString())


        }

        binding.editTextText41.setOnClickListener {
            binding.editTextText2.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText4.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText21.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText31.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
            binding.editTextText41.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))



            binding.editTextText2.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText3.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText4.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText21.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText31.setTextColor(requireContext().getColor(R.color.black))
            binding.editTextText41.setTextColor(requireContext().getColor(R.color.white))





            setupAmountOnEdittext(amount6)

            totalamount = mAmount
            binding.editTextText.setText("₹ " + amount6.toString())

        }
    }


    private fun updateLayout() {
        binding.cardView.visibility = View.VISIBLE
        binding.spinLayout.visibility = View.GONE
    }

    private fun handleBackPressed() {
        val mainActivity=activity as MainActivity
        if (mainActivity!!.binding.layoutGetway.visibility==View.VISIBLE){
            mainActivity!!.binding.layoutGetway.visibility=View.GONE
            mainActivity!!.binding.layoutMain.visibility=View.VISIBLE
            binding.spinLayout.visibility = View.VISIBLE
            binding.cardView.visibility = View.GONE
         }else  if (binding.cardView.visibility == View.VISIBLE   ) {
                binding.spinLayout.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE
            }
            else {
                val dialog = Dialog(requireContext())
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.exit_dialog)
                val cancel: MaterialCardView = dialog.findViewById(R.id.cvcancel)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val exit: CardView = dialog.findViewById(R.id.cvexit)
                cancel.setOnClickListener { dialog.dismiss() }
                exit.setOnClickListener { requireActivity().finishAffinity() }
                dialog.create()
                dialog.show()
            }




    }

    private fun showDialog() {
        val insufficientDialog = AlertDialog.Builder(requireContext())
        insufficientDialog.apply {
            setTitle("Spin & Win Money")
            setMessage("You don't have sufficient money to spin")
            setPositiveButton("Add Money", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    updateLayout()
                }
            })
        }
        insufficientDialog.show()
    }

    private fun startSpin() {

        if (pos == "false") {
            val random = Random.nextInt(1, 14)
            spinNow(random, pos)
        } else {
            val spin = Utils.getData(requireContext(), "spin")!!.toInt()

            if (spin >= 10) {
                val newAmount = requireContext().getDepositAmount() - 10
                requireContext().addDepositAmount(newAmount)
                if (requireContext().getWinningAmount() == 0) {
                    spinNow(4, pos)
                } else if (requireContext().getWinningAmount() == 45) {
                    spinNow(5, pos)
                } else if (requireContext().getWinningAmount() == 105) {
                    spinNow(10, pos)
                } else if (requireContext().getWinningAmount() == 175) {
                    spinNow(5, pos)
                } else if (requireContext().getWinningAmount() == 235) {

                    spinNow(0, pos)
                } else if (requireContext().getWinningAmount() == 250) {
                    //1st payment start

                    spinNow(11, pos)
                } else if (requireContext().getWinningAmount() == 260) {
                    spinNow(9, pos)
                } else if (requireContext().getWinningAmount() == 290) {
                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 280) {
                    spinNow(3, pos)
                } else if (requireContext().getWinningAmount() == 305) {
                    spinNow(0, pos)

                    //2nd payment start
                } else if (requireContext().getWinningAmount() == 320) {

                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 310) {
                    spinNow(3, pos)
                } else if (requireContext().getWinningAmount() == 335) {
                    spinNow(8, pos)
                } else if (requireContext().getWinningAmount() == 340) {
                    spinNow(9, pos)
                } else if (requireContext().getWinningAmount() == 370) {
                    spinNow(11, pos)
                    //3rd payment start
                } else if (requireContext().getWinningAmount() == 380) {
                    spinNow(1, pos)
                } else if (requireContext().getWinningAmount() == 400) {
                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 390) {
                    spinNow(8, pos)
                } else if (requireContext().getWinningAmount() == 395) {
                    spinNow(4, pos)
                } else if (requireContext().getWinningAmount() == 440) {
                    spinNow(2, pos)

                    //3rd dpayment start
                } else if (requireContext().getWinningAmount() == 430) {
                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 420) {
                    spinNow(3, pos)
                } else if (requireContext().getWinningAmount() == 445) {
                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 435) {
                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 425) {
                    spinNow(7, pos)

                    //4th payment
                } else if (requireContext().getWinningAmount() == 460) {
                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 450) {
                    spinNow(1, pos)
                } else if (requireContext().getWinningAmount() == 470) {
                    spinNow(3, pos)
                } else if (requireContext().getWinningAmount() == 495) {
                    spinNow(2, pos)
                } else if (requireContext().getWinningAmount() == 485) {
                    spinNow(2, pos)
                } else {
                    val random = Random.nextInt(0, 2)

                    spinNow(random, pos)
                }


            }
        }
    }

    private fun finditem() {
        spinWheel = binding.spinWheel
    }

    private fun spinNow(randomInt: Int, isUpiAvailable: String) {
        val spinOption = 15
        val SpinOptionDeg = 360 / spinOption
        val rotate = RotateAnimation(
            0f,
            (360 - SpinOptionDeg * randomInt).toFloat(),
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = (SpinOptionDeg * (spinOption - randomInt) + 2000).toLong()
        rotate.interpolator = DecelerateInterpolator()
        rotate.fillAfter = true
        spinWheel?.setDrawingCacheEnabled(true)
        val spinAnim =
            AnimationUtils.loadAnimation(requireContext(), R.anim.spinrotate) as RotateAnimation
        spinAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                spinWheel?.startAnimation(rotate)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        rotate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                Log.e("value for spin : ", randomInt.toString() + "")
                if (player != null) {
                    player?.release()
                }
                when (randomInt) {
                    0 -> coin = resources.getString(R.string.case_1).toInt()
                    1 -> coin = resources.getString(R.string.case_2).toInt()
                    2 -> coin = resources.getString(R.string.case_3).toInt()
                    3 -> coin = resources.getString(R.string.case_4).toInt()
                    4 -> coin = resources.getString(R.string.case_5).toInt()
                    5 -> coin = resources.getString(R.string.case_6).toInt()
                    6 -> coin = resources.getString(R.string.case_7).toInt()
                    7 -> coin = resources.getString(R.string.case_8).toInt()
                    8 -> coin = resources.getString(R.string.case_9).toInt()
                    9 -> coin = resources.getString(R.string.case_10).toInt()
                    10 -> coin = resources.getString(R.string.case_11).toInt()
                    11 -> coin = resources.getString(R.string.case_12).toInt()
                    12 -> coin = resources.getString(R.string.case_13).toInt()
                    13 -> coin = resources.getString(R.string.case_14).toInt()
                    14 -> coin = resources.getString(R.string.case_15).toInt()
                }
                val previousdpin = Utils.getData(requireContext(), "spin")!!.toInt()
                val availablespin = previousdpin - 10
                Utils.saveData(requireContext(), "spin", availablespin.toString())
                if (coin == -10 || coin == -25) {
                    showLooseDialog(coin.toString(), isUpiAvailable)
                } else {
                    showWinningDialog(coin.toString(), isUpiAvailable)

                }


            }

            override fun onAnimationRepeat(animation: Animation) {
                Log.d("@@@", "onAnimationRepeat: ")
            }
        })
        spinWheel?.startAnimation(spinAnim)

        playMusic()
    }

    private fun playMusic() {
        player = MediaPlayer.create(requireContext(), R.raw.spin_sound)
        player?.setOnCompletionListener(MediaPlayer.OnCompletionListener { })
        player?.start()
    }

    private fun showWinningDialog(amount: String, isUpiAvailable: String) {
        val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        dialog.setContentView(R.layout.win_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.text1).text = "Congrats!! you have won $amount"
        dialog.create()
        dialog.show()

        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.winner)
        mediaPlayer.start()


        val spinMore = dialog.findViewById<TextView>(R.id.btn_spin_more)
        spinMore.setOnClickListener {
            mediaPlayer.release()
            dialog.dismiss()


        }


        val winningAmount = requireContext().getWinningAmount()
        val newWinningAmount = winningAmount + amount.toInt()
        requireContext().addWinningAmount(newWinningAmount)
        (activity as MainActivity).updatadata()


    }

    private fun showLooseDialog(amount: String, isUpiAvailable: String) {
        val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        dialog.setContentView(R.layout.loose_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.text1).text = "Oops!! you have loose $amount"
        dialog.create()
        dialog.show()

        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.winner)
        mediaPlayer.start()


        val spinMore = dialog.findViewById<TextView>(R.id.btn_spin_more)
        spinMore.setOnClickListener {
            mediaPlayer.release()
            dialog.dismiss()


        }


        val winningAmount = requireContext().getWinningAmount()
        val newWinningAmount = winningAmount + amount.toInt()
        requireContext().addWinningAmount(newWinningAmount)
        (activity as MainActivity).updatadata()


    }


    //coin fun


    fun clickLister() {
        binding.textView11.setText("Minimum Deposit Amount ₹ $amount1/-")
        binding.textView12.setText("Get 10% extra (₹11) on ₹$amount2 or above payment")
        binding.textView121.setText("Get 20% extra (₹33) on ₹$amount3 or above payment")
        binding.textView122.setText("Get 30% extra (₹52) on ₹$amount4 or above payment")
        binding.textView123.setText("Get 40% extra (₹105) on ₹${amount5} or above payment")




        binding.editTextText2.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
        binding.editTextText3.setBackgroundDrawable(resources.getDrawable(R.drawable.select_edit_bg))
        binding.editTextText4.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
        binding.editTextText21.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
        binding.editTextText31.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))
        binding.editTextText41.setBackgroundDrawable(resources.getDrawable(R.drawable.edit_bg))



        binding.editTextText2.setTextColor(requireContext().getColor(R.color.black))
        binding.editTextText3.setTextColor(requireContext().getColor(R.color.white))
        binding.editTextText4.setTextColor(requireContext().getColor(R.color.black))
        binding.editTextText21.setTextColor(requireContext().getColor(R.color.black))
        binding.editTextText31.setTextColor(requireContext().getColor(R.color.black))
        binding.editTextText41.setTextColor(requireContext().getColor(R.color.black))



        setupAmountOnEdittext(amount2)

        totalamount = mAmount


    }

    private fun setupAmountOnEdittext(amount: Int) {
        mAmount = amount
        binding.editTextText.setText("₹ " + amount.toString())
    }

    private fun setupAmountOnEditText() {

        Utils.saveData(requireContext(), "amount1", Random.nextInt(50, 60).toString())
        Utils.saveData(requireContext(), "amount2", Random.nextInt(100, 120).toString())
        Utils.saveData(requireContext(), "amount3", Random.nextInt(150, 180).toString())
        Utils.saveData(requireContext(), "amount4", Random.nextInt(200, 230).toString())
        Utils.saveData(requireContext(), "amount5", Random.nextInt(250, 280).toString())
        Utils.saveData(requireContext(), "amount6", Random.nextInt(300, 350).toString())
        amount1 = Utils.getData(requireContext(), "amount1")!!.toInt()
        amount2 = Utils.getData(requireContext(), "amount2")!!.toInt()
        amount3 = Utils.getData(requireContext(), "amount3")!!.toInt()
        amount4 = Utils.getData(requireContext(), "amount4")!!.toInt()
        amount5 = Utils.getData(requireContext(), "amount5")!!.toInt()
        amount6 = Utils.getData(requireContext(), "amount6")!!.toInt()
        mAmount = amount2
        val amount11 = Utils.getData(requireContext(), "amount1")!!.toInt()
        val amount12 = Utils.getData(requireContext(), "amount2")!!.toInt()
        val amount13 = Utils.getData(requireContext(), "amount3")!!.toInt()
        val amount14 = Utils.getData(requireContext(), "amount4")!!.toInt()
        val amount15 = Utils.getData(requireContext(), "amount5")!!.toInt()
        val amount16 = Utils.getData(requireContext(), "amount6")!!.toInt()

        binding.editTextText2.setText("₹ " + amount11.toString())
        binding.editTextText3.setText("₹ " + amount12.toString())
        binding.editTextText4.setText("₹ " + amount13.toString())
        binding.editTextText21.setText("₹ " + amount14.toString())
        binding.editTextText31.setText("₹ " + amount15.toString())
        binding.editTextText41.setText("₹ " + amount16.toString())

    }






    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected
                    && netInfo.isConnectedOrConnecting
                    && netInfo.isAvailable
                ) {
                    return true
                }
            }
            return false
        }
    }

}