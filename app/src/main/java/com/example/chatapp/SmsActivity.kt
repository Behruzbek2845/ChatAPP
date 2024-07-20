package com.example.chatapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.SharedPraferance.MySharedPraferance
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.databinding.ActivitySmsBinding
import com.example.chatapp.databinding.ItemUserDialogBinding
import com.example.chatapp.models.Messege
import com.example.chatapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class SmsActivity : AppCompatActivity(), MessageAdapter.RvGo {
    lateinit var currentUser: String
    lateinit var user: User
    lateinit var messageAdapter: MessageAdapter
    lateinit var list: ArrayList<Messege>
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var reference2: StorageReference
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    lateinit var mySharedPreferences: MySharedPraferance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        currentUser = intent.getStringExtra("uid").toString()
        user = intent.getSerializableExtra("user") as User
        list = ArrayList()
        messageAdapter = MessageAdapter(this, list, currentUser)
        firebaseStorage = FirebaseStorage.getInstance()
        reference2 = firebaseStorage.getReference("myPhotos")
        binding.rv.adapter = messageAdapter
        binding.name.text = user.name
        Picasso.get().load(user.photoUrl).into(binding.image)
        mySharedPreferences = MySharedPraferance
        mySharedPreferences.init(this)


        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        binding.image.setOnLongClickListener {
            val intent = Intent(this@SmsActivity,ImageActivity::class.java)
            intent.putExtra("link", user.photoUrl)
            startActivity(intent)
            true
        }

        binding.name.setOnClickListener {
            val builder = AlertDialog.Builder(this@SmsActivity)
            val customAlertDialogBinding = ItemUserDialogBinding.inflate(layoutInflater)

            builder.setView(customAlertDialogBinding.root)

            val alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()

            val mail = intent.getStringExtra("mail")
            customAlertDialogBinding.apply {
                userName.text = user.name
                Picasso.get().load(user.photoUrl).into(imageUser)
                userMail.text = mail
                imageUser.setOnClickListener {
                    val intent = Intent(this@SmsActivity,ImageActivity::class.java)
                    intent.putExtra("link", user.photoUrl)
                    startActivity(intent)
                }

            }
        }


        binding.btnX.setOnClickListener {
            uri = null
            binding.progres.visibility = View.GONE
            binding.card.visibility = View.GONE
        }

        binding.btnSend.setOnClickListener {
            val text = binding.edtSend.text.toString()
            if (text.isNotBlank() && uri != null) {

                    val key = reference.push().key
                    val m = System.currentTimeMillis()
                    val task = reference2.child(m.toString()).putFile(uri!!)
                binding.progres.visibility = View.VISIBLE
                    task.addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            val downloadUrl = it.metadata?.reference?.downloadUrl
                            downloadUrl?.addOnSuccessListener { imageUri ->
                                imageUrl = imageUri.toString()
                                val message = Messege(text, currentUser, user.uid, getDate(), imageUrl)
                                reference.child(user.uid ?: "").child("messages").child(currentUser)
                                    .child(key ?: "").setValue(message)

                                reference.child(currentUser).child("messages").child(user.uid ?: "")
                                    .child(key ?: "").setValue(message)
                                binding.edtSend.text.clear()
                            }
//                            val list = MySharedPraferance.List
//                            if (list.contains(user.uid)==false){
//                                list.add(user.uid.toString())
//                            }
//                            MySharedPraferance.List = list
                            binding.progres.visibility = View.GONE
                            binding.card.visibility = View.GONE
                        }
                    task.addOnFailureListener {
                        Toast.makeText(this@SmsActivity, "Error", Toast.LENGTH_SHORT).show()
                    }

                }
                binding.edtSend.setText("")
            } else if (text.isBlank() && uri != null){
                val key = reference.push().key
                val m = System.currentTimeMillis()
                val task = reference2.child(m.toString()).putFile(uri!!)
                binding.progres.visibility = View.VISIBLE
                task.addOnSuccessListener {
                    if (it.task.isSuccessful) {
                        val downloadUrl = it.metadata?.reference?.downloadUrl
                        downloadUrl?.addOnSuccessListener { imageUri ->
                            imageUrl = imageUri.toString()
                            val message = Messege("", currentUser, user.uid, getDate(), imageUrl)
                            reference.child(user.uid ?: "").child("messages").child(currentUser)
                                .child(key ?: "").setValue(message)

                            reference.child(currentUser).child("messages").child(user.uid ?: "")
                                .child(key ?: "").setValue(message)

                        }
                        binding.progres.visibility = View.GONE
                        binding.card.visibility = View.GONE
                    }

                }
                task.addOnFailureListener {
                    Toast.makeText(this@SmsActivity, "Error", Toast.LENGTH_SHORT).show()
                }
//                val list = MySharedPraferance.List
//                if (list.contains(user.uid)==false){
//                    list.add(user.uid.toString())
//                }
//                MySharedPraferance.List = list
            } else if (text.isNotBlank() && uri == null){
                val key = reference.push().key
                val message = Messege(text, currentUser, user.uid, getDate())
                reference.child(user.uid ?: "").child("messages").child(currentUser)
                    .child(key ?: "").setValue(message)

                reference.child(currentUser).child("messages").child(user.uid ?: "")
                    .child(key ?: "").setValue(message)
                binding.edtSend.text.clear()

            }

            uri = null
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        reference.child(currentUser).child("messages").child(user.uid ?: "")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()

                    val children = snapshot.children
                    for (child in children) {
                        val message = child.getValue(Messege::class.java)
                        if (message != null) {
                            list.add(message)
                        }
                    }
                    if (list.isNotEmpty() && user.uid !in MySharedPraferance.sharedList) {
                        val sharedList = MySharedPraferance.sharedList
                        sharedList.add(user.uid!!)
                        MySharedPraferance.sharedList = sharedList
                    }
                    if (list.isEmpty()){
                        val sharedList = MySharedPraferance.sharedList
                        sharedList.remove(user.uid)
                        MySharedPraferance.sharedList = sharedList
                    }
                    binding.rv.scrollToPosition(list.size - 1)
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        binding.file.setOnClickListener {
            getImageContent.launch("image/*")
        }
    }

    fun getDate(): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return simpleDateFormat.format(date)
    }

    var imageUrl = ""
    var uri: Uri? = null
    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        uri = it
        binding.card.visibility = View.VISIBLE
        binding.imageView.setImageURI(uri)
    }

    override fun click2(message: Messege) {
        val intent = Intent(this,ImageActivity::class.java)
        intent.putExtra("link",message.imageLink)
        startActivity(intent)
    }

    override fun cliclLong(message: Messege) {

    }


}