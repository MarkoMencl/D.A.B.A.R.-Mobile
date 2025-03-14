package hr.foi.rampu.dabroviapp.reviews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.ReviewAdapter
import hr.foi.rampu.dabroviapp.entities.Review
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.ws.WsReview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewReceivedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_received, container, false)

        recyclerView = view.findViewById(R.id.rv_review_received)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ReviewAdapter(emptyList())

        val user = SessionManager.getUser()

        if(user != null){
            fetchReceivedMessages(user.id)
        }

        return view
    }

    private fun fetchReceivedMessages(senderId: Int){
        val ws = WsReview.reviewService
        ws.getReceiverReview(senderId).enqueue(object : Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                if(response.isSuccessful){
                    val reviews = response.body() ?: emptyList()

                    val reviewAdapter = ReviewAdapter(reviews)
                    recyclerView.adapter = reviewAdapter
                } else{
                    Toast.makeText(requireContext(), R.string.failedToFetchMessages, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Review>>, response: Throwable) {
                Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}