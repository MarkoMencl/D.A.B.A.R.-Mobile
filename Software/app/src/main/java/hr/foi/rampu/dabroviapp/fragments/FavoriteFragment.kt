package hr.foi.rampu.dabroviapp.fragments

import Ad
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.ShowAdAdapter
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.repository.AdRepository
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.widget.Toast

class FavoriteFragment : Fragment() {

    private lateinit var emptyPlaceholder: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShowAdAdapter
    private lateinit var sessionManager: SessionManager
    private var favoriteAds: MutableList<Ad?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        emptyPlaceholder = view.findViewById(R.id.emptyPlaceholder)
        recyclerView = view.findViewById(R.id.favoritesRecyclerView)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = ShowAdAdapter(favoriteAds, showCancelButton = true) { ad ->
            removeFromFavorites(ad)
        }
        recyclerView.adapter = adapter

        refreshFavorites()
        return view
    }

    override fun onResume() {
        super.onResume()
        refreshFavorites()
    }

    private fun refreshFavorites() {
        if (sessionManager.getUser() == null) {
            showLoginPrompt()
        } else {
            loadFavorites()
        }
    }

    private fun showLoginPrompt() {
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
        emptyPlaceholder.text = getString(R.string.not_logged_in_message)
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            val favorites = loadFavoriteAds()

            if (favorites.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyPlaceholder.visibility = View.VISIBLE
                emptyPlaceholder.text = getString(R.string.no_favorites_message)
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyPlaceholder.visibility = View.GONE
                favoriteAds.clear()
                favoriteAds.addAll(favorites)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun removeFromFavorites(ad: Ad) {
        val currentUser = SessionManager.getUser()
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val adId = ad.id ?: run {
            Toast.makeText(requireContext(), "Ad ID is not available", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                AdRepository.deleteFavoriteAd(adId, currentUser.id)
            }
            if (success) {
                Toast.makeText(requireContext(), "Favorite removed", Toast.LENGTH_SHORT).show()
                favoriteAds.remove(ad)
                adapter.notifyDataSetChanged()
                checkEmptyState()
            } else {
                Toast.makeText(requireContext(), "Failed to remove favorite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmptyState() {
        if (favoriteAds.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyPlaceholder.visibility = View.VISIBLE
            emptyPlaceholder.text = getString(R.string.no_favorites_message)
        }
    }

    private suspend fun loadFavoriteAds(): List<Ad?> {
        return AdRepository.getUserFavoriteAds() ?: emptyList()
    }
}
