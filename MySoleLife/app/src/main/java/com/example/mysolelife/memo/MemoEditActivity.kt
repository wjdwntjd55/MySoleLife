package com.example.mysolelife.memo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MemoEditActivity : AppCompatActivity() {

    private val TAG = MemoEditActivity::class.java.simpleName

    private lateinit var key : String

    // 글 쓴이에 대한 uid
    //private lateinit var writerUid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_edit)

        key = intent.getStringExtra("key").toString()

        getMemoData(key)

        val memo_editBtn = findViewById<Button>(R.id.memo_editBtn2)
        memo_editBtn.setOnClickListener {
            editMemoData(key)
        }

    }

    // 수정버튼 클릭하면 메모장의 제목과 내용 받아오기 기능
    private fun getMemoData(key : String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val  dataModel = dataSnapshot.getValue(MemoModel::class.java)

                val memo_titleArea2 = findViewById<TextView>(R.id.memo_titleArea2)
                val memo_contentArea2  = findViewById<TextView>(R.id.memo_contentArea2)

                memo_titleArea2.setText(dataModel!!.title )
                memo_contentArea2.setText(dataModel.content)
                //writerUid = dataModel.uid


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.memoRef.child(key).addValueEventListener(postListener)

    }

    // 수정한 데이터를 파이어베이스에 저장하기
    private fun editMemoData(key: String) {

        val title = findViewById<EditText>(R.id.memo_titleArea2).text.toString()
        val content = findViewById<EditText>(R.id.memo_contentArea2).text.toString()
        val uid = FBAuth.getUid()
        val time = FBAuth.getTime()

        FBRef.memoRef
            //.child(FBAuth.getUid())
            .child(key)
            //.push()
            .setValue(MemoModel(title, content, uid, time))

        Toast.makeText(this, "수정완료", Toast.LENGTH_LONG).show()

        finish()

    }

}