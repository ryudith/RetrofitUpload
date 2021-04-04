package com.ryudith.simpleretrofitupload

import android.app.Activity
import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.ContentResolverCompat.query
import androidx.lifecycle.lifecycleScope
import com.ryudith.simpleretrofitupload.databinding.ActivityMainBinding
import com.ryudith.simpleretrofitupload.util.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private val REQUEST_PROFILE_CODE : Int = 100
    private val REQUEST_PHOTO_CODE : Int = 200

    private var binding : ActivityMainBinding? = null
    private lateinit var activityRef : MainActivity

    private var selectedProfile : Uri? = null
    private var selectedPhoto : MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityRef = this
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.ivActivityMainProfile.setOnClickListener {
            openProfileChooser()
        }

        binding!!.ivActivityMainPhoto.setOnClickListener {
            openPhotoChooser()
        }

        val uploadRef = RetrofitHelper.getProfileRetrofit()
        binding!!.btnActivityMainSave.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val name = binding!!.etActivityMainName.text.toString()
                val email = binding!!.etActivityMainEmail.text.toString()

                val profile = getUriFile(selectedProfile!!)
                val requestProfileFile = profile.asRequestBody("image/*".toMediaType())
                val profileFileName = profile.name
                val postProfileFile = MultipartBody.Part.createFormData("profile", profileFileName, requestProfileFile)

                val photos : MutableList<MultipartBody.Part> = mutableListOf()
                for (f : Uri in selectedPhoto) {
                    val photo = getUriFile(f)
                    val requestPhotoFile = photo.asRequestBody("image/*".toMediaType())

                    photos.add(MultipartBody.Part.createFormData("photo[]", photo.name, requestPhotoFile))
                }

                val resp = uploadRef.updateProfile(name, email, postProfileFile, photos)

                val intent = Intent(activityRef, ProfileView::class.java)
                intent.putExtra("name", resp.name)
                intent.putExtra("email", resp.email)
                intent.putExtra("profile", resp.profile)
                intent.putExtra("photo", ArrayList<String>(resp.photo))
                startActivity(intent)
            }
        }
    }

    private fun openProfileChooser () {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        startActivityForResult(intent, REQUEST_PROFILE_CODE)
    }

    private fun openPhotoChooser () {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(intent, REQUEST_PHOTO_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PROFILE_CODE -> {
                    selectedProfile = data?.data!!
                    binding!!.ivActivityMainProfile.setImageURI(selectedProfile)
                }

                REQUEST_PHOTO_CODE -> {
                    selectedPhoto = mutableListOf()

                    val clipData : ClipData? = data?.clipData
                    if (clipData != null) {
                        for (i : Int in 0 until clipData.itemCount) {
                            selectedPhoto.add(clipData.getItemAt(i).uri)
                        }
                    } else if (data != null) {
                        selectedPhoto.add(data?.data!!)
                    }
                }
            }
        }
    }

    private fun getUriFileName (contentResolver: ContentResolver, uri: Uri) : String {
        var fileName = ""

        val cursor = query(contentResolver, uri, null, null, null, null, null)
        cursor.use {
            it!!.moveToFirst()
            fileName = cursor!!.getString(it!!.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }

        return fileName
    }

    private fun getUriFile (uri: Uri) : File {
        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r", null)
        val file = File(cacheDir, getUriFileName(contentResolver, uri))
        val fileInputStream = FileInputStream(fileDescriptor!!.fileDescriptor)
        val fileOutputStream = FileOutputStream(file)
        fileInputStream.copyTo(fileOutputStream)

        return file
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}