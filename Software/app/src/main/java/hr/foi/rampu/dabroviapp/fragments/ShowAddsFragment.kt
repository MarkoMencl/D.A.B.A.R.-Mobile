package hr.foi.rampu.dabroviapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.AdAdapter
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.repository.AdRepository
import kotlinx.coroutines.launch

class ShowAdsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_ads, container, false)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.tabs)
        bottomNavigationView.visibility = View.GONE

        recyclerView = view.findViewById(R.id.recyclerViewAds)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val currentUser = SessionManager.getUser()
            val ads = if (currentUser != null) {
                AdRepository.getUserAds(currentUser.id) ?: emptyList()
            } else {
                emptyList()
            }
            recyclerView.adapter = AdAdapter(ads.toMutableList())
        }

        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_blue))
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.tabs)
        bottomNavigationView.visibility = View.VISIBLE
    }
}
