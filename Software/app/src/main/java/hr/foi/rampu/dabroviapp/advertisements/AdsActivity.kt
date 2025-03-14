package hr.foi.rampu.dabroviapp.advertisements

import Ad
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.ShowAdAdapter
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.repository.AdRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adsAdapter: ShowAdAdapter
    private lateinit var noAdsMessage: TextView
    private lateinit var locationSpinner: Spinner
    private lateinit var sortSpinner: Spinner

    private var selectedLocationId: Int? = null
    private var selectedCategoryId: Int? = null
    private var allCategoryAds: List<Ad> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)

        recyclerView = findViewById(R.id.recycler_view_ads)
        noAdsMessage = findViewById(R.id.no_ads_message)
        locationSpinner = findViewById(R.id.location_spinner)
        sortSpinner = findViewById(R.id.sort_spinner)

        selectedCategoryId = intent.getIntExtra("CATEGORY_ID", -1)
        Log.d("AdsActivity", "Selected Category ID: $selectedCategoryId")

        setupLocationSpinner()
        setupSortSpinner()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.top_bar_chat)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fetchAdsForCategory(selectedCategoryId ?: -1)
    }

    private fun setupLocationSpinner() {
        val locations = SessionManager.getLocations() ?: emptyList()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf(getString(R.string.all_locations)) + locations.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedLocationId = if (position == 0) null else locations[position - 1].id
                performFilteringAndSorting()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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

        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)
        sortSpinner.adapter = sortAdapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                performFilteringAndSorting()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchAdsForCategory(categoryId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ads = AdRepository.getAdsForTheCategory(categoryId)
                withContext(Dispatchers.Main) {
                    if (!ads.isNullOrEmpty()) {
                        allCategoryAds = ads
                        setupRecyclerView(allCategoryAds) // Prikaz svih oglasa iz kategorije
                        noAdsMessage.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        showNoAdsMessage()
                    }
                }
            } catch (e: Exception) {
                Log.e("AdsActivity", "Error fetching ads for category $categoryId", e)
                showNoAdsMessage()
            }
        }
    }

    private fun performFilteringAndSorting() {
        val filteredAds = allCategoryAds.filter { ad ->
            selectedLocationId == null || ad.locationId == selectedLocationId
        }

        val sortedAds = when (sortSpinner.selectedItemPosition) {
            1 -> filteredAds.sortedBy { it.price }
            2 -> filteredAds.sortedByDescending { it.price }
            3 -> filteredAds.sortedBy { it.dateOfPublication }
            4 -> filteredAds.sortedByDescending { it.dateOfPublication }
            5 -> filteredAds.sortedByDescending { it.views }
            else -> filteredAds
        }

        if (sortedAds.isNotEmpty()) {
            setupRecyclerView(sortedAds)
            noAdsMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            showNoAdsMessage()
        }
    }

    private fun showNoAdsMessage() {
        noAdsMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun setupRecyclerView(ads: List<Ad>) {
        adsAdapter = ShowAdAdapter(ads, showCancelButton = false) { ad -> }
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adsAdapter
    }
}
