package com.example.mysolelife.memo

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
import java.lang.Exception

class MemoInsideActivity : AppCompatActivity() {

    private val TAG = MemoInsideActivity::class.java.simpleName

    private lateinit var key : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_inside)

        // 방법 1:  MemoRVAdapter에서 준 데이터 받기
        /*val title = intent.getStringExtra("title").toString()
        val content = intent.getStringExtra("content").toString()
        val time = intent.getStringExtra("time").toString()

        val memo_titleArea = findViewById<TextView>(R.id.memo_titleArea)
        val memo_timeArea  = findViewById<TextView>(R.id.memo_timeArea)
        val memo_textArea  = findViewById<TextView>(R.id.memo_textArea)

        memo_titleArea.text = title
        memo_timeArea.text = time
        memo_textArea.text = content*/

        // 방법 2 : MemoRVAdapter에서 준 데이터 받기
        key = intent.getStringExtra("key").toString()
        //Toast.makeText(this, key, Toast.LENGTH_LONG).show()

        getMemoData(key)

        // 메모내용 수정 삭제하는 버튼
        val memoSettingIcon = findViewById<ImageView>(R.id.memoSettingIcon)
        memoSettingIcon.setOnClickListener {
            showDialog()
        }

    }

    private fun getMemoData(key : String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {

                    val  dataModel = dataSnapshot.getValue(MemoModel::class.java)
                    Log.d(TAG, dataModel!!.title)

                    val memo_titleArea = findViewById<TextView>(R.id.memo_titleArea)
                    val memo_timeArea  = findViewById<TextView>(R.id.memo_timeArea)
                    val memo_textArea  = findViewById<TextView>(R.id.memo_textArea)

                    memo_titleArea.text = dataModel!!.title
                    memo_timeArea.text = dataModel!!.time
                    memo_textArea.text = dataModel!!.content

                } catch (e: Exception) {
                    Log.d(TAG, "삭제완료")
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.memoRef.child(key).addValueEventListener(postListener)

    }

    // 다이얼로그 띄우기 기능
    private fun showDialog(){

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.memo_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("메모 수정/삭제")

        val alertDialog = mBuilder.show()

        // 수정버튼 클릭시 일어나는 기능
        alertDialog.findViewById<Button>(R.id.memo_editBtn)?.setOnClickListener {

            Toast.makeText(this, "수정 버튼을 눌렀습니다", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MemoEditActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)

        }

        // 삭제제버튼 클릭시 일어나는 기능
        alertDialog.findViewById<Button>(R.id.memo_removeBtn)?.setOnClickListener {

            FBRef.memoRef.child(key).removeValue()
            Toast.makeText(this, "삭제완료", Toast.LENGTH_LONG).show()
            finish()
        }

    }


}