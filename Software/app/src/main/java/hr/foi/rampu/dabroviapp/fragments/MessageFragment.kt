package hr.foi.rampu.dabroviapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.ChatPreviewAdapter
import hr.foi.rampu.dabroviapp.entities.ChatPreview
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.ws.WsChat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageFragment : Fragment() {

    private val ws = WsChat.chatService
    private lateinit var recyclerView: RecyclerView
    private val handler = Handler(Looper.getMainLooper())
    private var refreshRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message, container, false)

        recyclerView = view.findViewById(R.id.rv_previous_chats)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ChatPreviewAdapter(emptyList())

        val user = SessionManager.getUser()

        if (user != null) {
            ws.getPreviewChat(user.id).enqueue(object : Callback<List<ChatPreview>> {
                override fun onResponse(
                    call: Call<List<ChatPreview>>,
                    response: Response<List<ChatPreview>>
                ) {
                    if (response.isSuccessful) {
                        val chats = response.body()
                        if (!chats.isNullOrEmpty()) {
                            val adapter = ChatPreviewAdapter(chats)
                            recyclerView.adapter = adapter
                        } else {
                            Log.d("MessageFragment", "No chats found")
                        }
                    } else {
                        Log.d("MessageFragment", "Error: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), R.string.errorLoadingChats, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ChatPreview>>, t: Throwable) {
                    Log.e("MessageFragment", "API call failed", t)
                    Toast.makeText(requireContext(), R.string.failedToLoadChats, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.d("MessageFragment", "Error: User not found")
            Toast.makeText(requireContext(), R.string.userNotLoggedIn, Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onResume(){
        if(!SessionManager.isLoggedIn()){
            recyclerView.adapter = ChatPreviewAdapter(emptyList())
        }

        super.onResume()
        startPeriodicRefresh()
    }

    override fun onPause() {
        super.onPause()
        stopPeriodicRefresh()
    }

    private fun startPeriodicRefresh() {
        refreshRunnable = object : Runnable {
            override fun run() {
                val user = SessionManager.getUser()

                if(user != null){
                    ws.getPreviewChat(user.id).enqueue(object : Callback<List<ChatPreview>> {
                        override fun onResponse(
                            call: Call<List<ChatPreview>>,
                            response: Response<List<ChatPreview>>
                        ) {
                            if (response.isSuccessful) {
                                val chats = response.body()
                                if (!chats.isNullOrEmpty()) {
                                    val adapter = ChatPreviewAdapter(chats)
                                    recyclerView.adapter = adapter
                                } else {
                                    Log.d("MessageFragment", "No chats found")
                                }
                            } else {
                                Log.d("MessageFragment", "Error: ${response.errorBody()?.string()}")
                                Toast.makeText(requireContext(), "Error loading chats", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<ChatPreview>>, t: Throwable) {
                            Log.e("MessageFragment", "API call failed", t)
                            Toast.makeText(requireContext(), "Failed to load chats", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                handler.postDelayed(this, 60000)
            }
        }
        handler.post(refreshRunnable!!)
    }

    private fun stopPeriodicRefresh() {
        refreshRunnable?.let { handler.removeCallbacks(it) }
    }
}