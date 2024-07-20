package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.adapters.RvAdapter
import com.example.chatapp.databinding.ActivitySearch3Binding
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity3 : AppCompatActivity(), RvAdapter.RvAction {
    private lateinit var auth: FirebaseAuth
    lateinit var list: ArrayList<User>
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    private val binding by lazy { ActivitySearch3Binding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        list = ArrayList()

        binding.searchUser.isIconified = false
        binding.searchUser.requestFocus()
        binding.searchUser.setQuery("", false)


        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()
                val children = snapshot.children
                for (child in children) {
                    val user = child.getValue(User::class.java)
                    if (user?.uid != auth.uid) {
                        list.add(user!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity3, "${error.message}", Toast.LENGTH_SHORT).show()
            }
        })



        binding.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                binding.searchRv.adapter = RvAdapter(this@SearchActivity3, list)
                val l = ArrayList<User>()
                for (user in list) {
                    if (user.name?.lowercase()!!.contains(newText!!.lowercase()) && newText.isNotBlank()) {
                        l.add(user)
                    }
                }
                binding.searchRv.adapter = RvAdapter(this@SearchActivity3, l)
                return true
            }
        })
    }

    override fun itemClick(user: User) {
        val intent = Intent(this, SmsActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("uid", auth.uid)
        intent.putExtra("mail", auth.currentUser?.email)
        startActivity(intent)
    }

//    private fun setSearchViewQuery(query: String) {
//        // SearchView ni kengaytirish
//        searchView.isIconified = false
//        // SearchView ga query matnini o'rnatish
//        searchView.setQuery(query, false)
//        // Kerak bo'lsa SearchView ga fokus berish
//        searchView.requestFocus()
//    }
}