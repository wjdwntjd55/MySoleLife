package com.example.mysolelife.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysolelife.R
import com.example.mysolelife.comment.CommentInsideActivity
import com.example.mysolelife.comment.CommentLVAdapter
import com.example.mysolelife.comment.CommentModel
import com.example.mysolelife.comment.CommentRVAdapter
import com.example.mysolelife.databinding.ActivityBoardInsideBinding
import com.example.mysolelife.utils.FBAuth
import com.example.mysolelife.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class BoardInsideActivity : AppCompatActivity() {

    // 태그
    private  val TAG = BoardInsideActivity::class.java.simpleName

    private lateinit var binding : ActivityBoardInsideBinding

    // 게시글 키
    private lateinit var key : String

    // 댓글(= 본문+uid+시간) 목록
    private val commentDataList =mutableListOf<CommentModel>()

    // 댓글의 키 목록
    private val commentKeyList =mutableListOf<String>()

    // 리스클뷰 어댑터 선언
    private lateinit var commentRVAdapter: CommentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside)

        binding.boardSettingIcon.setOnClickListener{
            showDialog()
        }


// 첫 번째 방법
        /* val title = intent.getStringExtra("title").toString()
         val content = intent.getStringExtra("content").toString()
         val time = intent.getStringExtra("time").toString()

         binding.titleArea.text = title
         binding.textArea.text = content
         binding.timeArea.text = time*/

        // 두 번째 방법
        // 게시판 프래그먼트에서 게시글의 키 값을 받아옴
        key =intent.getStringExtra("key").toString()

        getBoardData(key)
        getImageData(key)

        binding.commentBtn.setOnClickListener{
            insertComment(key)
        }

/*commentAdapter = CommentLVAdapter(commentDataList)
    val cLV : ListView = binding.commentLV
    binding.commentLV.adapter = commentAdapter*/

        commentRVAdapter = CommentRVAdapter(commentDataList)
        val cLV : RecyclerView = binding.commentLV
        binding.commentLV.adapter= commentRVAdapter

        cLV.layoutManager= LinearLayoutManager(this)

        // 77 ~ 89 보고 작성함
        // 댓글 목록(리스트뷰)
        /*cLV.setOnTouchListener(object : View.OnTouchListener {

            // 리스트뷰를 터치했을 때
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

                // 스크롤뷰(화면 전체)의 터치 이벤트를 막으면 -> 리스트뷰(댓글 영역)의 스크롤뷰가 작동함
                binding.scrollViewTest.requestDisallowInterceptTouchEvent(true)
                return false

            }

        })*/

        commentRVAdapter.itemClick = object : CommentRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                // 댓글 작성자의 uid와 현재 사용자의 uid가 동일하면
                if(commentDataList[position].uid == FBAuth.getUid()) {

                    // 명시적 인텐트 -> 다른 액티비티 호출
                    val intent = Intent(baseContext, CommentInsideActivity::class.java)

                    // 댓글수정 액티비티로 키 값 전달
                    intent.putExtra("key", key)

                    // 댓글수정 액티비티로 댓글의 키 값 전달
                    intent.putExtra("commentKey", commentKeyList[position])

                    // 댓글수정 액티비티 시작
                    startActivity(intent)
                }

            }

        }


        getCommentData(key)

    }

    // 게시글 하나의 정보를 가져옴
    private fun getBoardData(key : String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {

                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(TAG, dataModel!!.title)

                    binding.titleArea.text= dataModel!!.title
                    binding.textArea.text= dataModel!!.content
                    binding.timeArea.text= dataModel!!.time

                    val myUid = FBAuth.getUid()
                    val writerUid = dataModel.uid

                    if(myUid.equals(writerUid)) {
                        binding.boardSettingIcon.isVisible= true
                    } else {

                    }

                } catch (e : Exception) {

                    Log.d(TAG, "삭제완료")

                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)

    }

    // 게시글에 첨부된 이미지 정보를 가져옴
    private fun getImageData(key : String) {

        // 이미지 파일 경로
        val storageReference = Firebase.storage.reference.child(key + ".png")

        // 이미지 넣을 곳
        val imageViewFromFB = binding.getImageArea

        // 글라이드로 이미지 다운로드
        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener{task->

// 이미지 첨부
            if (task.isSuccessful) {

                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
                // 첨부 이미지 없으면 imageArea 안 보이게 처리
            } else {
                binding.getImageArea.isVisible= false
            }

        })

    }

    private fun showDialog() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")

        val alertDialog = mBuilder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener{
            Toast.makeText(this, "수정 버튼을 눌렀습니다", Toast.LENGTH_LONG).show()

            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener{

            FBRef.boardRef.child(key).removeValue()
            Toast.makeText(this, "삭제완료", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    // 작성한 댓글을 등록
    fun insertComment(key : String){
        val uid = FBAuth.getUid()
        // 파이어베이스 구조
        // comment
        //  - BoardKey
        //      - CommentKey
        //          - CommentData
        //          - CommentData
        //          - CommentData
        FBRef.commentRef
            .child(key)
            .push()
            .setValue(
                CommentModel(
                    binding.commentArea.text.toString(),
                    FBAuth.getTime(),
                    uid
                )
            )

        Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_SHORT).show()
        binding.commentArea.setText("")


    }

    // 댓글 목록 정보를 가져옴
    fun getCommentData(key: String){

        // 데이터베이스에서 컨텐츠의 세부정보를 검색
        val postListener = object : ValueEventListener {

            // 데이터 스냅샷
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // 댓글 목록 비움
                // -> 저장/삭제 마다 데이터 누적돼 게시글 중복으로 저장되는 것을 방지해줌
                commentDataList.clear()

                for (dataModel in dataSnapshot.children) {

                    // 아이템(=댓글)
                    val item = dataModel.getValue(CommentModel::class.java)

                    // 댓글 목록에 아이템 넣음
                    commentDataList.add(item!!)

                    // 댓글 키 목록에 문자열 형식으로 변환한 키 넣음
                    commentKeyList.add(dataModel.key.toString())

                }

                // 댓글 키 목록을 출력
                commentKeyList

                // 댓글 목록도 출력
                commentDataList

                // 동기화(새로고침) -> 리스트 크기 및 아이템 변화를 어댑터에 알림
                commentRVAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }


}