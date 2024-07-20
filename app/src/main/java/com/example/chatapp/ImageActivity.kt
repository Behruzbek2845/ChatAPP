package com.example.chatapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.i
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import com.example.chatapp.databinding.ActivityImageBinding
import com.example.chatapp.utils.sdk29AdnUp
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ImageActivity : AppCompatActivity() {
    private var writePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private val binding by lazy { ActivityImageBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        binding.back.setOnClickListener {
            onBackPressed()
        }
        val img =  intent.getStringExtra("link")
        Picasso.get().load(img).into(binding.imageLong)


        binding.imgMore.setOnClickListener {
            val popupMenu = PopupMenu(this@ImageActivity, binding.imgMore)
            showIcons(popupMenu)
            popupMenu.inflate(R.menu.popup_more_menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {

                when (it.itemId){
                    R.id.btn_save_galerey -> {
                        updateOrRequestPermission()
                        if (writePermissionGranted) {
                            saveToExternalStorage(UUID.randomUUID().toString(), binding.imageLong.drawToBitmap())
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                    R.id.btn_share_image -> {

                    }
                }

                return@OnMenuItemClickListener true
            })
            popupMenu.show()

        }

    }

    fun showIcons(popupMenu: PopupMenu) {
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateOrRequestPermission(){
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        writePermissionGranted = hasWritePermission || minSdk29

        val permissionToRequest = mutableListOf<String>()
        if (!writePermissionGranted){
            permissionToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionToRequest.isNotEmpty()){
            permissionLauncher.launch(permissionToRequest.toTypedArray())
        }
    }

    private fun saveToExternalStorage(displayName: String, bmp: Bitmap): Boolean{
        val imageCollection = sdk29AdnUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image.jpeg")
        }
        return try {
            contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream!!)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch (e:IOException) {
            e.printStackTrace()
            false
        }
    }

//    private fun downloadAndShareImage(context: Context, imageUrl: String) {
//        Picasso.get().load(imageUrl).into(object : com.squareup.picasso.Target {
//            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                bitmap?.let {
//                    try {
//                        val file = File(context.cacheDir, "images")
//                        file.mkdirs() // Fayllar uchun katalog yaratish
//                        val imageFile = File(file, "shared_image.png")
//                        val fos = FileOutputStream(imageFile)
//                        it.compress(Bitmap.CompressFormat.PNG, 100, fos)
//                        fos.close()
//                        shareImage(context, imageFile)
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//
//            override fun onBitmapFailed(e: Exception?, errorDrawable: android.graphics.drawable.Drawable?) {
//                // Rasm yuklash xatosi
//                Toast.makeText(context, "Xato", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onPrepareLoad(placeHolderDrawable: android.graphics.drawable.Drawable?) {
//                // Rasm yuklashni tayyorlash
//                Toast.makeText(context, "tayyor", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun shareImage(context: Context, file: File) {
//        val imageUri: Uri = FileProvider.getUriForFile(
//            context,
//            "${context.packageName}.provider",
//            file
//        )
//        val intent = Intent(Intent.ACTION_SEND).apply {
//            type = "image/*"
//            putExtra(Intent.EXTRA_STREAM, imageUri)
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        context.startActivity(Intent.createChooser(intent, "Share image using"))
//    }




}