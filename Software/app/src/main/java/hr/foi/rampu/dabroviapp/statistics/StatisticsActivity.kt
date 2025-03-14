package hr.foi.rampu.dabroviapp.statistics

import Ad
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

class StatisticsActivity : AppCompatActivity() {

    private lateinit var showAdAdapter: ShowAdAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var averageViewsTextView: TextView
    private lateinit var categorySpinner: Spinner
    private lateinit var sortButton: Button
    private var userAds: List<Ad> = listOf()
    private var isAscending: Boolean = true
    private lateinit var currentCategoryFilter: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)

        currentCategoryFilter = getString(R.string.allCategories)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.top_bar_chat)
        recyclerView = findViewById(R.id.recyclerViewAds)
        averageViewsTextView = findViewById(R.id.averageViewsTextView)
        categorySpinner = findViewById(R.id.categorySpinner)
        sortButton = findViewById(R.id.sortButton)

        val categories = SessionManager.getCategories() ?: emptyList()
        val categoryNames = mutableListOf(getString(R.string.allCategories))

        categoryNames.addAll(categories.mapNotNull { category ->
            val resId = resources.getIdentifier(category.localizationKey, "string", packageName)
            if (resId != 0) resources.getString(resId) else null
        })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        showAdAdapter = ShowAdAdapter(listOf(), showCancelButton = false, onCancelClick = { })
        recyclerView.adapter = showAdAdapter

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        SessionManager.getUser()?.let { user ->
            loadUserAds(user.id)
        }

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position) as? String
                selectedCategory?.let {
                    currentCategoryFilter = it
                    applyFilterAndSort()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                currentCategoryFilter = getString(R.string.allCategories)
                applyFilterAndSort()
            }
        }

        sortButton.setOnClickListener {
            isAscending = !isAscending
            val orderResId = if (isAscending) R.string.sortByViewsA else R.string.sortByViewsD
            sortButton.text = getString(orderResId)
            applyFilterAndSort()
        }
    }

    override fun onResume() {
        super.onResume()
        SessionManager.getUser()?.let { user ->
            loadUserAds(user.id)
        }
    }

    private fun loadUserAds(userId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ads = withContext(Dispatchers.IO) {
                    AdRepository.getUserAds(userId)
                }
                if (!ads.isNullOrEmpty()) {
                    userAds = ads
                    applyFilterAndSort()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun applyFilterAndSort() {
        var adsToDisplay = userAds

        if (currentCategoryFilter != getString(R.string.allCategories)) {
            val categoryId = getCategoryIdFromName(currentCategoryFilter)
            adsToDisplay = adsToDisplay.filter { it.categoryId == categoryId }
        }

        adsToDisplay = if (isAscending) {
            adsToDisplay.sortedBy { it.views }
        } else {
            adsToDisplay.sortedByDescending { it.views }
        }

        updateAdsList(adsToDisplay)
    }

    private fun updateAdsList(ads: List<Ad>) {
        showAdAdapter = ShowAdAdapter(ads, showCancelButton = false, onCancelClick = { })
        recyclerView.adapter = showAdAdapter

        val averageViews = if (ads.isNotEmpty()) ads.map { it.views }.average() else 0.0
        averageViewsTextView.text = "Average Views: %.2f".format(averageViews)
    }

    private fun getCategoryIdFromName(categoryName: String): Int {
        return when (categoryName) {
            getString(R.string.literatureText) -> 1
            getString(R.string.sportText) -> 2
            getString(R.string.marketText) -> 3
            getString(R.string.furnitureText) -> 4
            getString(R.string.clothesText) -> 5
            else -> 0
        }
    }
}
