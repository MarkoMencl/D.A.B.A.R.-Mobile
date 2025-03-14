import Ad
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.ShowAdAdapter
import hr.foi.rampu.dabroviapp.advertisements.AdsActivity
import hr.foi.rampu.dabroviapp.entities.Category
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.repository.AdRepository
import hr.foi.rampu.dabroviapp.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerViewAds: RecyclerView
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var adsAdapter: ShowAdAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var adList: List<Ad>
    private lateinit var categoryList: List<Category>
    private lateinit var sortSpinner: Spinner
    private var selectedSortOption: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewAds = view.findViewById(R.id.recycler_view_ads)
        recyclerViewCategories = view.findViewById(R.id.recycler_view_categories)
        sortSpinner = view.findViewById(R.id.sort_spinner)
        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.search_view)

        setupSortSpinner()

        lifecycleScope.launch {
            categoryList = fetchCategories() ?: emptyList()
            setupCategoriesRecyclerView()

            adList = AdRepository.getAllAds() ?: emptyList()
            setupAdsRecyclerView()
        }

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }
        })

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSortOption = parent?.getItemAtPosition(position) as? String
                performSearch(searchView.query.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSortSpinner() {
        val sortOptions = arrayOf(
            getString(R.string.sort_by_default),
            getString(R.string.sort_by_price_asc),
            getString(R.string.sort_by_price_desc),
            getString(R.string.sort_by_date_asc),
            getString(R.string.sort_by_date_desc),
            getString(R.string.sort_by_popularity)
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sortOptions)
        sortSpinner.adapter = adapter
    }

    private fun performSearch(query: String?) {
        lifecycleScope.launch {
            val filteredAds = AdRepository.searchAds(query ?: "", selectedSortOption)
            filteredAds?.let { ads ->
                updateRecyclerView(ads)
            }
        }
    }

    private suspend fun fetchCategories(): List<Category>? {
        return withContext(Dispatchers.IO) {
            CategoryRepository.getAllCategories()
        }
    }

    private fun setupCategoriesRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategories.layoutManager = linearLayoutManager
        categoryAdapter = CategoryAdapter(categoryList) { category ->
            val intent = Intent(requireContext(), AdsActivity::class.java)
            intent.putExtra("CATEGORY_ID", category.id)
            context?.startActivity(intent)
        }
        recyclerViewCategories.adapter = categoryAdapter
    }

    private fun setupAdsRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        recyclerViewAds.layoutManager = gridLayoutManager
        adsAdapter = ShowAdAdapter(adList, showCancelButton = false) { ad -> }
        recyclerViewAds.adapter = adsAdapter
    }

    private fun updateRecyclerView(filteredAds: List<Ad>) {
        adsAdapter = ShowAdAdapter(filteredAds, showCancelButton = false) { ad -> }
        recyclerViewAds.adapter = adsAdapter
    }
}
