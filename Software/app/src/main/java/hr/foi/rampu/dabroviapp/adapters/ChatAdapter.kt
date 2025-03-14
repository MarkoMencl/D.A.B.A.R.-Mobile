package hr.foi.rampu.dabroviapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.entities.Chat
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(
    private val chats: List<Chat>
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.txtChatUsername)
        val content: TextView = itemView.findViewById(R.id.txtChatContent)
        val date: TextView = itemView.findViewById(R.id.txtChatDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        holder.username.text = chat.senderUsername
        holder.content.text = chat.content

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.date.text = dateFormat.format(chat.date)
    }

    override fun getItemCount(): Int {
        return chats.size
    }
}