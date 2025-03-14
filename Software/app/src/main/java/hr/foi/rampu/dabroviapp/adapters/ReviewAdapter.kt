package hr.foi.rampu.dabroviapp.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.entities.Review
import hr.foi.rampu.dabroviapp.helpers.PhotoManager
import hr.foi.rampu.dabroviapp.helpers.ReviewDialogHelper
import hr.foi.rampu.dabroviapp.ws.ReviewRequestUpdate
import hr.foi.rampu.dabroviapp.ws.WsReview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewAdapter(
    private val reviews: List<Review>
): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.txt_review_username)
        val comment: TextView = itemView.findViewById(R.id.txt_review_comment)
        val grade: TextView = itemView.findViewById(R.id.txt_review_grade)
        val userImage: ImageView = itemView.findViewById(R.id.imgvUserImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_message, parent, false)

        return ReviewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        holder.username.text = review.reviewerUsername
        holder.comment.text = review.comment

        val valueText = review.value + "/5"
        holder.grade.text = valueText

        if(review.profileImage != null){
            PhotoManager.loadProfileImage(holder.itemView.context, review.profileImage, holder.userImage)
        }else{
            holder.userImage.setImageResource(R.drawable.ic_outline_boy_24)
        }

        if(reviews[position].userId != 0){
            holder.itemView.setOnClickListener{
                ReviewDialogHelper.showEditReviewDialog(holder.itemView.context, review) { reviewRequestUpdate, dialog ->
                    updateReview(reviewRequestUpdate, dialog, holder.itemView.context)
                }
            }
        }
    }

    private fun updateReview(reviewRequestUpdate: ReviewRequestUpdate, dialog: DialogInterface, context: Context) {
        WsReview.reviewService.putReview(reviewRequestUpdate).enqueue(object: Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Review updated successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Failed to update review", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}