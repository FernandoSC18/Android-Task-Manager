package com.fscsoftware.managetasks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fscsoftware.managetasks.databinding.ActivityAboutBinding
import com.fscsoftware.managetasks.databinding.DialogAlertThanksAddBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    //Define ad Id from AdMob platform

    /** The below AD ID IS TO TEST - NEED CHANGE TO REAL VALUE*/
    private val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    private val DEBUG_TAG = AboutActivity::javaClass.name

    //ADS
    private var mRewardedAd: RewardedAd? = null
    val adTryTimes = 4
    var adCurrentTry = 0
    private var adIsLoading = false
    private var adWasEarnedReward = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnBack.setCallbackOnTouch { onBackPressed() }
        binding.btnPaypal.setOnClickListener { actionPaypal() }
        binding.btnAds.setOnClickListener { actionAds() }
        binding.btnGoGit.setOnClickListener { actionGoGit() }
        binding.btnGoPlayStore.setOnClickListener { actionGoPlayStore() }

    }

    private fun actionPaypal (){
        startActivity(
            Intent( Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/donate/?hosted_button_id=JUFBTTPHQL69L") )
        )
    }

    private fun actionGoGit (){
        startActivity(
            Intent( Intent.ACTION_VIEW, Uri.parse("https://github.com/FernandoSC18/Android-Task-Manager.git") )
        )
    }

    private fun actionGoPlayStore (){
        val idPackage = this.packageName
        startActivity(
            Intent( Intent.ACTION_VIEW, Uri.parse("market://details?id=$idPackage") )
        )
    }

    private fun actionAds (){

        try {
            binding.btnAds.isEnabled = false
            adWasEarnedReward = false
            binding.btnAds.text = getString(R.string.donate_add_wait)
            MobileAds.initialize(this) {}

            loadRewardedAd()
            lifecycleScope.launch(Dispatchers.Default) {
                tryShowRewardedVideo()

                withContext(Dispatchers.Main){
                    binding.btnAds.isEnabled = true
                    binding.btnAds.text = getString(R.string.donate_add_btn)
                    adWasEarnedReward = false
                    adCurrentTry = 0;
                }
            }

        }catch (e : Exception){
            binding.btnAds.isEnabled = true
            binding.btnAds.text = getString(R.string.donate_add_btn)
            adWasEarnedReward = false
            adCurrentTry = 0;
            Toast.makeText(this, getString(R.string.add_error_1), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    suspend fun tryShowRewardedVideo ( ){

        if (mRewardedAd != null) {
            adCurrentTry = adTryTimes;
            withContext(Dispatchers.Main){
                showRewardedVideo()
            }
        }else{

            if (adCurrentTry < adTryTimes){
                withContext(Dispatchers.Main){
                    loadRewardedAd()
                }
                delay(2000)
                tryShowRewardedVideo()
            }
            Log.d(DEBUG_TAG, "The rewarded ad wasn't ready yet.")
        }
        adCurrentTry += 1;
    }

    private fun loadRewardedAd(){
        if (mRewardedAd == null) {
            adIsLoading = true
            var adRequest = AdRequest.Builder().build()

            RewardedAd.load( this, AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(DEBUG_TAG, "ERROR MESSAGE::" + adError.message)
                    adIsLoading = false
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(DEBUG_TAG, "Ad was loaded.")
                    mRewardedAd = rewardedAd
                    adIsLoading = false
                }
            }
            )
        }
    }

    private fun showRewardedVideo()  {
        //binding.btnAds.visibility = View.INVISIBLE

            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mRewardedAd = null
                    loadRewardedAd()

                    if (adWasEarnedReward){
                        val bindingDialogAlert = DialogAlertThanksAddBinding.inflate(
                            LayoutInflater.from(this@AboutActivity), binding.root, false
                        )

                        val alert = AlertDialog.Builder( ContextThemeWrapper( this@AboutActivity, R.style.AlertDialogTheme ) )
                            .setPositiveButton(R.string.ok, null)
                            .setView(bindingDialogAlert.root)

                        val alertDialog = alert.create();
                        alertDialog.show()
                    }

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d(DEBUG_TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mRewardedAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(DEBUG_TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }

            mRewardedAd?.show(
                this){ rewardItem ->
                        adWasEarnedReward = true
                        var rewardAmount = rewardItem.amount
                        var rewardType = rewardItem.type
                        Log.d(DEBUG_TAG, "User earned the reward.")
                        Log.d("rewardAmount", "$rewardAmount")
                        Log.d("rewardType", "$rewardType")
                }

    }

}