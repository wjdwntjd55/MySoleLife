package com.example.mysolelife.memo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth

class MemoRVAdapter(val context : Context,
                    val memoList : MutableList<MemoModel>,
                    val memoKeyList: ArrayList<String>)
    : RecyclerView.Adapter<MemoRVAdapter.Viewholder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MemoRVAdapter.Viewholder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_item, parent, false)

        return Viewholder(view)

    }

    override fun onBindViewHolder(holder: MemoRVAdapter.Viewholder, position: Int) {
        holder.bindItems(memoList[position], memoKeyList[position])
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(memoList : MemoModel, key : String) {

            // 아이템 클릭시 이동하기
            itemView.setOnClickListener{
                val intent = Intent(context, MemoInsideActivity::class.java)
                // 방법 1: MemoInsideActivity에 데이터 넘겨주기
                //intent.putExtra("title", memoList.title)
                //intent.putExtra("content", memoList.content)
                //intent.putExtra("time",memoList.time)

                // 방법 2 : MemoInsideActivity에 데이터 넘겨주기
                intent.putExtra("key", memoKeyList[position])

                itemView.context.startActivity(intent)

                Log.d("MemoRVAdapter", FBAuth.getUid())
                //Toast.makeText(context, key, Toast.LENGTH_LONG).show()


            }

            // 메모장에 있는 내용 연결
            val memo_textArea = itemView.findViewById<TextView>(R.id.memo_textArea)
            memo_textArea.text = memoList.content

            // 메모장 제목 연결
            val memo_title = itemView.findViewById<TextView>(R.id.memo_title)
            memo_title.text = memoList.title


        }

    }


}