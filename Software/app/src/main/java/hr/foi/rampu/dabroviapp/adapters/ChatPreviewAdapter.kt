package hr.foi.rampu.dabroviapp.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.chat.ChatActivity
import hr.foi.rampu.dabroviapp.entities.ChatPreview
import hr.foi.rampu.dabroviapp.helpers.PhotoManager
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import java.text.SimpleDateFormat
import java.util.Locale

class ChatPreviewAdapter(
    private val chatPreviews: List<ChatPreview>
) : RecyclerView.Adapter<ChatPreviewAdapter.ChatPreviewViewHolder>() {

    class ChatPreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.txtChatPreviewUsername)
        val content: TextView = itemView.findViewById(R.id.txtChatPreviewContent)
        val date: TextView = itemView.findViewById(R.id.txtChatPreviewDate)
        val userImage: ImageView = itemView.findViewById(R.id.imgvUserImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatPreviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_preview, parent, false)
        return ChatPreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatPreviewViewHolder, position: Int) {
        val chat = chatPreviews[position]

        holder.username.text = chat.username
        holder.content.text = chat.content

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.date.text = dateFormat.format(chat.date)

        Log.d("Chat preview", "${chat.profileImage}")

        if(chat.profileImage != null){
            PhotoManager.loadProfileImage(holder.itemView.context, chat.profileImage, holder.userImage)
        }else{
            holder.userImage.setImageResource(R.drawable.ic_outline_boy_24)
        }

        holder.itemView.setOnClickListener{
            val context = holder.itemView.context
            val intent = Intent(context, ChatActivity::class.java)
            val user = SessionManager.getUser()

            if(user != null){
                Log.d("Message", "User id: ${user.id} + ${chat.id}")

                intent.putExtra("currentUserId", user.id.toString())
                intent.putExtra("otherUserId", chat.id.toString())

                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatPreviews.size
    }
}