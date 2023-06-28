package com.example.mysolelife.comment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CommentInsideActivity : AppCompatActivity() {

    private val TAG = CommentInsideActivity::class.java.simpleName

    // 게시글 키
    private lateinit var key : String

    // 댓글 키
    private lateinit var commentKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_inside)

        // 게시판 프래그먼트에서 게시글의 키 값을 받아옴
        key =intent.getStringExtra("key").toString()

        // 글읽기 액티비티에서 댓글의 키 값을 받아옴
        commentKey =intent.getStringExtra("commentKey").toString()

        Log.d(TAG, key)
        Log.d(TAG, commentKey)

        // 댓글 키 값을 바탕으로 댓글 하나의 정보를 가져옴
        getCommentData(key, commentKey)

        // 댓글 수정, 삭제하는 버튼
        val commentSettingIcon = findViewById<ImageView>(R.id.commentSettingIcon)
        commentSettingIcon.setOnClickListener {
            commentDialog()
        }

    }

    // 댓글 하나의 정보를 가져옴
    private fun getCommentData(key : String, commentKey : String) {

        // 데이터베이스에서 컨텐츠의 세부정보를 검색
        val postListener = object : ValueEventListener {

            // 데이터 스냅샷
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {

                    val dataModel = dataSnapshot.getValue(CommentModel::class.java)

                    val commentMainArea = findViewById<TextView>(R.id.commentMainArea)
                    val comment_timeArea = findViewById<TextView>(R.id.comment_timeArea)

                    commentMainArea.text = dataModel?.commentTitle
                    comment_timeArea.text = dataModel?.commentCreatedTime

                    Log.d(TAG, dataModel.toString())
                } catch (e : Exception) {

                }


            }
            // 오류 나면
            override fun onCancelled(databaseError: DatabaseError) {

                // 로그
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())

            }

        }

        // 파이어베이스 내 데이터의 변화(추가)를 알려줌
        FBRef.commentRef.child(key).child(commentKey).addValueEventListener(postListener)
    }

    // 다이얼로그 띄우기 기능
    private fun commentDialog() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.comment_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("댓글 수정/삭제")

        val alertDialog = mBuilder.show()

        // 수정버튼 클릭시 일어나는 기능
        alertDialog.findViewById<Button>(R.id.comment_editBtn)?.setOnClickListener{

            Toast.makeText(this, "수정 버튼을 눌렀습니다", Toast.LENGTH_LONG).show()

            val intent = Intent(this, CommentEditActivity::class.java)
            intent.putExtra("key", key)
            intent.putExtra("commentKey", commentKey)

            startActivity(intent)

        }

        // 삭제제버튼 클릭시 어나는 기능
        alertDialog.findViewById<Button>(R.id.comment_removeBtn)?.setOnClickListener{

            FBRef.commentRef
                .child(key)
                .child(commentKey)
                .removeValue()

            Toast.makeText(this, "삭제완료", Toast.LENGTH_LONG).show()
            finish()
        }

    }

}