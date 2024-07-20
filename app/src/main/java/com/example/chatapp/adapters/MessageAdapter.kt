package com.example.chatapp.adapters

import android.content.Intent
import android.net.Uri
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.FromItemBinding
import com.example.chatapp.databinding.ToItemBinding
import com.example.chatapp.models.Messege
import com.squareup.picasso.Picasso
import java.util.UUID

class MessageAdapter(val rvGo: RvGo ,val list: ArrayList<Messege>, val currentUserUid: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_FROM = 1
    val TYPE_TO = 0
    inner class ToVh(val itemRvBinding: ToItemBinding): RecyclerView.ViewHolder(itemRvBinding.root){
        fun onBind(message: Messege){
            if (message.imageLink != null && message.text != "") {
                itemRvBinding.toItemImage1.visibility = View.VISIBLE
                itemRvBinding.message.visibility = View.VISIBLE
                itemRvBinding.message.text = message.text
                Picasso.get().load(message.imageLink).into(itemRvBinding.toItemImage)
            }else if (message.imageLink != null && message.text!!.isBlank()){
                itemRvBinding.toItemImage1.visibility = View.VISIBLE
                itemRvBinding.message.visibility = View.GONE
                Picasso.get().load(message.imageLink).into(itemRvBinding.toItemImage)
            } else if (message.imageLink == null && message.text != ""){
                itemRvBinding.toItemImage1.visibility = View.GONE
                itemRvBinding.message.visibility = View.VISIBLE
                itemRvBinding.message.text = message.text
                Picasso.get().load(message.imageLink).into(itemRvBinding.toItemImage)
            }
            itemRvBinding.toItemImage1.setOnClickListener {
                rvGo.click2(message)
            }

        }
    }



    inner class FromVh(val itemRvBinding: FromItemBinding): RecyclerView.ViewHolder(itemRvBinding.root){
        fun onBind(message: Messege){
            if (message.imageLink != null && message.text != "") {
                itemRvBinding.fromItemImage1.visibility = View.VISIBLE
                itemRvBinding.message.visibility = View.VISIBLE
                itemRvBinding.message.text = message.text
                Picasso.get().load(message.imageLink).into(itemRvBinding.fromItemImage)
            }else if (message.imageLink != null && message.text!!.isBlank()){
                itemRvBinding.fromItemImage1.visibility = View.VISIBLE
                itemRvBinding.message.visibility = View.GONE
                Picasso.get().load(message.imageLink).into(itemRvBinding.fromItemImage)
            } else if (message.imageLink == null && message.text != ""){
                itemRvBinding.fromItemImage1.visibility = View.GONE
                itemRvBinding.message.visibility = View.VISIBLE
                itemRvBinding.message.text = message.text
                Picasso.get().load(message.imageLink).into(itemRvBinding.fromItemImage)
            }
            itemRvBinding.message.setOnLongClickListener {
                val popupMenu = PopupMenu(itemRvBinding.root.context, itemRvBinding.message)
                popupMenu.inflate(R.menu.item_menu_sms)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {

                    when (it.itemId){
                        R.id.btn_save_galerey -> {

                        }
                        R.id.btn_share_image -> {

                        }
                    }

                    return@OnMenuItemClickListener true
                })
                popupMenu.show()


                true
            }
            itemRvBinding.fromItemImage.setOnClickListener {
                rvGo.click2(message)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==0){
            return ToVh(ToItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return FromVh(FromItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ToVh){
            holder.onBind(list[position])
        }else if (holder is FromVh){
            holder.onBind(list[position])
        }
    }
    override fun getItemViewType(position: Int): Int {
        if (list[position].fromUserUid == currentUserUid) {
            return TYPE_FROM
        } else {
            return TYPE_TO
        }
    }
    interface RvGo{
        fun click2(message: Messege)
        fun cliclLong(message: Messege)
    }




}

