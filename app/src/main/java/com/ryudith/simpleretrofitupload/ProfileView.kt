package com.ryudith.simpleretrofitupload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.ryudith.simpleretrofitupload.databinding.ActivityProfileViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import android.widget.RelativeLayout.LayoutParams

class ProfileView : AppCompatActivity() {
    private lateinit var activityRef : ProfileView
    private var binding : ActivityProfileViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityRef = this
        binding = ActivityProfileViewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val profile = intent.getStringExtra("profile")
        val photo = intent.getStringArrayListExtra("photo")

        binding!!.tvActivityProfileViewName.text = "Name : ${name}"
        binding!!.tvActivityProfileViewEmail.text = "Email : ${email}"

        lifecycleScope.launch(Dispatchers.IO) {
            val imgProfile = downloadImage(profile)

            val imgPhoto = mutableListOf<Bitmap>()
            for (photoUrl : String in photo) {
                imgPhoto.add(downloadImage(photoUrl)!!)
            }

            activityRef.runOnUiThread {
                binding!!.ivActivityProfileViewProfile.setImageBitmap(imgProfile)

                val lp = LayoutParams(250, 250)
                for (img : Bitmap in imgPhoto) {
                    val iv = ImageView(activityRef)
                    iv.setImageBitmap(img)
                    iv.layoutParams = lp

                    binding!!.llhActivityProfileViewPhoto.addView(iv)
                }
            }
        }
    }

    private fun downloadImage (url : String) : Bitmap? {
        val inputStream = URL(url).openStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}