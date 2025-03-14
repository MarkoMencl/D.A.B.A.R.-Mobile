package hr.foi.rampu.dabroviapp.chat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.ChatAdapter
import hr.foi.rampu.dabroviapp.entities.Chat
import hr.foi.rampu.dabroviapp.ws.ChatRequest
import hr.foi.rampu.dabroviapp.ws.WsChat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val sendButton: Button = findViewById(R.id.send_button)
        val messageText: EditText = findViewById(R.id.message_input)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.top_bar_chat)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        recyclerView = findViewById(R.id.rv_chat_messages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val emptyChatList: List<Chat> = emptyList()
        val chatAdapter = ChatAdapter(emptyChatList)
        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = chatAdapter

        val currentUserId = intent.getStringExtra("currentUserId")?.toIntOrNull()
        val otherUserId = intent.getStringExtra("otherUserId")?.toIntOrNull()

        if (currentUserId != null && otherUserId != null) {
            fetchChatMessages(currentUserId, otherUserId)
            recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

            sendButton.setOnClickListener {
                addNewChatMessage(currentUserId, otherUserId, messageText.text.toString()){
                    fetchChatMessages(currentUserId, otherUserId)
                    messageText.text.clear()
                }
            }
        } else {
            Toast.makeText(this, R.string.invalidUserData, Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addNewChatMessage(currentUserId: Int, otherUserId: Int, messageContent: String, onSuccess: () -> Unit) {
        if(messageContent != ""){
            val currDate = Date()
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            val currFormatDate = format.format(currDate)

            val chat = ChatRequest(currentUserId, otherUserId, messageContent, currFormatDate)

            WsChat.chatService.postChat(chat).enqueue(object : Callback<Any> {
                override fun onResponse(p0: Call<Any>, p1: Response<Any>) {
                    if (p1.isSuccessful) {
                        Log.d("ChatActivity", "Message sent successfully")
                        onSuccess()
                    } else {
                        Log.e("ChatActivity", "Failed to send message: ${p1.message()}")
                        Toast.makeText(this@ChatActivity, R.string.failedToSaveData, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(p0: Call<Any>, t: Throwable) {
                    Log.e("ChatActivity", "Error: ${t.message}")
                    Toast.makeText(this@ChatActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this, R.string.messageNeedsText, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchChatMessages(senderId: Int, receiverId: Int) {
        val ws = WsChat.chatService.getChat(senderId, receiverId)

        ws.enqueue(object : Callback<List<Chat>> {
            override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
                if (response.isSuccessful) {
                    val chatList = response.body() ?: emptyList()

                    if (chatList.isNotEmpty()) {
                        val chatAdapter = ChatAdapter(chatList)
                        recyclerView.adapter = chatAdapter
                    } else {
                        Toast.makeText(this@ChatActivity, R.string.noMessages, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ChatActivity, R.string.failedToFetchMessages, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}