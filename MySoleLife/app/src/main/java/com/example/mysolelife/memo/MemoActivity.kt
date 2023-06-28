package com.example.mysolelife.memo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysolelife.R
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MemoActivity : AppCompatActivity() {

    private val TAG = MemoActivity::class.java.simpleName

    private val memoDataList = mutableListOf<MemoModel>()

    // 메모들의 키리스트를 저장함
    private val memoKeyList = ArrayList<String>()

    lateinit var memoRVAdapter : MemoRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        val memo_Btn = findViewById<Button>(R.id.memo_Btn)

        // 메모하기 버튼 클릭하면 MemoWriteActivity으로 이동
        memo_Btn.setOnClickListener {
            val intent = Intent(this, MemoWriteActivity::class.java)
            startActivity(intent)
        }

        val memolv = findViewById<RecyclerView>(R.id.memo_lv)

        memoRVAdapter = MemoRVAdapter(baseContext, memoDataList, memoKeyList)
        memolv.adapter = memoRVAdapter
        memolv.layoutManager = GridLayoutManager(this, 2)

        getFBMemoData()

    }


    // 파이어베이스에서 데이터를 가져오기위해서 선언
    private fun getFBMemoData() {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                memoDataList.clear()

                val memolv = findViewById<RecyclerView>(R.id.memo_lv)

                for (dataModel in dataSnapshot.children) {

                    Log.d(TAG, dataModel.toString())
                    // 메모의 키값을 가져온다
                    Log.d(TAG, dataModel.key.toString())

                    val item = dataModel.getValue(MemoModel::class.java)

                    // 여기서 부터
                    // 내가쓴 메모만 보이게 하기 위해
                    // 사용자의 uid
                    val userUid = FBAuth.getUid()
                    val memoWriterUid = item!!.uid

                    Log.d(TAG, userUid)
                    Log.d(TAG, memoWriterUid)

                    if(userUid.equals(memoWriterUid)) {
                        Log.d(TAG, "내가 쓴 글")
                        memolv.isVisible = true
                        //Toast.makeText(baseContext, "내가 글쓴이임", Toast.LENGTH_LONG).show()
                        memoDataList.add(item!!)
                        // memoKeyList가 메모들의 키값을 가지게됨
                        memoKeyList.add(dataModel.key.toString())
                    } else {
                        Log.d(TAG, "내가 쓴 글 아님")
                        //Toast.makeText(baseContext, "내가 글쓴이 아님", Toast.LENGTH_LONG).show()
                    }
                    // 여기 까지


                }

                // 최신 메모를 보기위해서 뒤집어준 것임
                memoDataList.reverse()
                memoKeyList.reverse()
                // 화면에 보이기 하기 위해, 동기화를 시켜줘야함
                memoRVAdapter.notifyDataSetChanged()

                Log.d(TAG, memoDataList.toString())



            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.memoRef.addValueEventListener(postListener)
    }


}