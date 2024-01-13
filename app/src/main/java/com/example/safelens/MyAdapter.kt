package com.example.safelens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val userList : ArrayList<camera>, private val onCameraClick: (camera, action: String) -> Unit) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.camera_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.cameraID.text = currentItem.cameraID
        holder.address1.text = currentItem.address1
        holder.address2.text = currentItem.address2

        holder.viewDetails.setOnClickListener {
            onCameraClick(currentItem, "details")
        }

        holder.deleteCamera.setOnClickListener {
            onCameraClick(currentItem, "delete")
        }

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cameraID : TextView = itemView.findViewById(R.id.tvCameraId)
        val address1 : TextView = itemView.findViewById(R.id.tvStreetInfo)
        val address2 : TextView = itemView.findViewById(R.id.tvDistrictInfo)
        val viewDetails: ImageView = itemView.findViewById(R.id.viewDetails)
        val deleteCamera: ImageView = itemView.findViewById(R.id.deleteCamera)
    }

}