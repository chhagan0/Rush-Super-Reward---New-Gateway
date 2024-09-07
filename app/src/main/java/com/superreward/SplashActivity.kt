package com.superreward.rushsuperreward

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.superreward.rushsuperreward.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    var installedPackageNameList = arrayListOf<String>()
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(binding.root)

        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (packageInfo in packages) {
            if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                installedPackageNameList.add(packageInfo.packageName)
            }
        }

        if (installedPackageNameList.contains("com.google.android.apps.nbu.paisa.user") ||
            installedPackageNameList.contains("com.phonepe.app") ||
            installedPackageNameList.contains("net.one97.paytm") ||
            installedPackageNameList.contains("com.paytmmall") ||
            installedPackageNameList.contains("in.org.npci.upiapp") ||
            installedPackageNameList.contains("in.amazon.mShop.android.shopping") ||
            installedPackageNameList.contains("com.csam.icici.bank.imobile") ||
            installedPackageNameList.contains("com.sbi.upi") ||
            installedPackageNameList.contains("com.myairtelapp") ||
            installedPackageNameList.contains("n.code.cashtime") ||
            installedPackageNameList.contains("com.icicibank.pockets")
        ) {
            com.nkomapp.rupeequiz.Utils.Utils.saveData(this, "spam", "true")
            com.nkomapp.rupeequiz.Utils.Utils.saveData(this, "time", "1")

            val Hander = Handler()
            val username = com.nkomapp.rupeequiz.Utils.Utils.getData(this, "username")
            if (username != null) {
                Hander.postDelayed(Runnable {
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )
                }, 2000)
            } else {
                Hander.postDelayed(Runnable {
                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )
                }, 2000)
            }


        } else {
            com.nkomapp.rupeequiz.Utils.Utils.saveData(this, "spam", "false")
            com.nkomapp.rupeequiz.Utils.Utils.saveData(this, "time", "1")

            val Hander = Handler()
            val username = com.nkomapp.rupeequiz.Utils.Utils.getData(this, "username")
            if (username != null) {
                Hander.postDelayed(Runnable {
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )
                }, 2000)
            } else {
                Hander.postDelayed(Runnable {
                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )
                }, 2000)
            }


        }

    }

}



