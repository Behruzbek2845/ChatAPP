package com.example.chatapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.children
import com.example.chatapp.SharedPraferance.MySharedPraferance
import com.example.chatapp.adapters.RvAdapter
import com.example.chatapp.adapters.SecondAdapter
import com.example.chatapp.databinding.ActivityMain2Binding
import com.example.chatapp.databinding.ItemDialogImageBinding
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.ArrayList

class MainActivity2 : AppCompatActivity(), SecondAdapter.RvAction {
    private val binding by lazy { ActivityMain2Binding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    lateinit var list: ArrayList<User>
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var secondAdapter: SecondAdapter
    lateinit var mySharedPreferences: MySharedPraferance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        mySharedPreferences = MySharedPraferance
        mySharedPreferences.init(this)

        binding.addUser.setOnClickListener {
            val intent = Intent(this@MainActivity2, SearchActivity3::class.java)
            startActivity(intent)
        }


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()
                val shareList = MySharedPraferance.sharedList
                val children = snapshot.children
                for (child in children) {
                    val user = child.getValue(User::class.java)
                    for (share in shareList){
                            if (share == user?.uid) {
                                list.add(user!!)
                            }
                    }
                }
                secondAdapter = SecondAdapter(this@MainActivity2, list)
                binding.rv.adapter = secondAdapter

                val image = findViewById<ImageView>(R.id.image_header)
                Picasso.get().load(auth.currentUser?.photoUrl)
                    .into(image)
                binding.navView.findViewById<TextView>(R.id.header_name)
                    .text = auth.currentUser?.displayName
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity2, "${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.drawMenu.setOnClickListener {
            binding.main.openDrawer(GravityCompat.START)
        }




    }


    override fun itemClick(user: User) {
        val intent = Intent(this, SmsActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("uid", auth.uid)
        intent.putExtra("mail", auth.currentUser?.email)
        startActivity(intent)
    }


}