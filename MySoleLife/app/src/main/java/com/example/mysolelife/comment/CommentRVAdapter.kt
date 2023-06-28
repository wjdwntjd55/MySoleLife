package com.example.mysolelife.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth

class CommentRVAdapter(val commentList: MutableList<CommentModel>) : RecyclerView.Adapter<CommentRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)

        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onBindViewHolder(holder: CommentRVAdapter.ViewHolder, position: Int) {

        if (itemClick != null) {
            holder.itemView.setOnClickListener{v->
                itemClick?.onClick(v, position)


            }
        }

        // 뷰바인딩해줌
        holder.bindItems(commentList[position])
    }

    // 전체 리사이클러뷰의 갯수
    override fun getItemCount(): Int {
        return commentList.size
    }

    // ViewHolder를 만들어 줘야함
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: CommentModel) {

            val title = itemView.findViewById<TextView>(R.id.titleArea)
            val time = itemView.findViewById<TextView>(R.id.timeArea)

            title.text= item.commentTitle
            time.text= item.commentCreatedTime

            val commentSettingBtn = itemView.findViewById<ImageView>(R.id.commentSettingBtn)
            commentSettingBtn.isVisible= commentList[position].uid.equals(FBAuth.getUid())

        }
    }

}