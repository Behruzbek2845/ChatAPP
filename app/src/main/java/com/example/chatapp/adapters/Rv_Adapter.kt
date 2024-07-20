package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemRvBinding
import com.example.chatapp.models.User
import com.squareup.picasso.Picasso

class RvAdapter(var rvAction: RvAction, val list: ArrayList<User>): RecyclerView.Adapter<RvAdapter.VH>() {
    inner class VH(var itemRvBinding: ItemRvBinding):RecyclerView.ViewHolder(itemRvBinding.root){
        fun OnBind(users: User){
            itemRvBinding.tv1.text = users.name
            Picasso.get().load(users.photoUrl).into(itemRvBinding.img)
            itemRvBinding.root.setOnClickListener {
                rvAction.itemClick(users)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.OnBind(list[position])
    }

    interface RvAction{
        fun itemClick(user: User)
    }


}