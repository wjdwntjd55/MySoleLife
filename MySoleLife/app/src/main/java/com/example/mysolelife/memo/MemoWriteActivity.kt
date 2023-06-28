package com.example.mysolelife.memo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef

class MemoWriteActivity : AppCompatActivity() {

    private val TAG = MemoWriteActivity::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_write)

        val writeBtn = findViewById<Button>(R.id.writeBtn)

        writeBtn.setOnClickListener {


            val title = findViewById<EditText>(R.id.titleArea).text.toString()
            val content = findViewById<EditText>(R.id.contentArea).text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            Log.d(TAG.toString(), title)
            Log.d(TAG.toString(), content)

            val key = FBRef.memoRef.push().key.toString()

            FBRef.memoRef
                //.child(FBAuth.getUid())
                .child(key)
                //.push()
                .setValue(MemoModel(title, content, uid, time))

            Toast.makeText(this,"메모완료", Toast.LENGTH_LONG).show()

            finish()

        }

    }
}