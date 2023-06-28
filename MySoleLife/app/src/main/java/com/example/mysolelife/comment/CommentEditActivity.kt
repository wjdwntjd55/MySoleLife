package com.example.mysolelife.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CommentEditActivity : AppCompatActivity() {

    private val TAG = CommentEditActivity::class.java.simpleName

    private lateinit var key : String

    private lateinit var commentKey : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_edit)

        // 게시판 프래그먼트에서 게시글의 키 값을 받아옴
        key =intent.getStringExtra("key").toString()

        // 글읽기 액티비티에서 댓글의 키 값을 받아옴
        commentKey =intent.getStringExtra("commentKey").toString()

        getData(key, commentKey)

        Log.d(TAG, key)
        Log.d(TAG, commentKey)

        val commentEditBtn2 = findViewById<Button>(R.id.commentEditBtn2)
        commentEditBtn2.setOnClickListener{
            editCommentData(key, commentKey)
        }

    }

    private fun getData(key : String, commentKey : String) {

        // 데이터베이스에서 컨텐츠의 세부정보를 검색
        val postListener = object : ValueEventListener {

            // 데이터 스냅샷
            override fun onDataChange(dataSnapshot: DataSnapshot){

                val dataModel = dataSnapshot.getValue(CommentModel::class.java)

                val commentMainArea2 = findViewById<EditText>(R.id.commentMainArea2)

                commentMainArea2.setText(dataModel!!.commentTitle)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 로그
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }

        // 파이어베이스 내 데이터의 변화(추가)를 알려줌
        FBRef.commentRef.child(key).child(commentKey).addValueEventListener(postListener)

    }

    private fun editCommentData(key : String, commentKey : String) {

        val commentTitle = findViewById<EditText>(R.id.commentMainArea2).text.toString()
        val commentCreatedTime = FBAuth.getTime()
        val uid = FBAuth.getUid()

        FBRef.commentRef
            .child(key)
            .child(commentKey)
            .setValue(CommentModel(commentTitle, commentCreatedTime, uid))

        Toast.makeText(this, "수정 완료", Toast.LENGTH_LONG).show()

        finish()
    }


}