package hr.foi.rampu.dabroviapp

import HomeFragment
import Location
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.rampu.dabroviapp.adapters.MainPagerAdapter
import hr.foi.rampu.dabroviapp.entities.Category
import hr.foi.rampu.dabroviapp.fragments.AddFragment
import hr.foi.rampu.dabroviapp.fragments.FavoriteFragment
import hr.foi.rampu.dabroviapp.fragments.MessageFragment
import hr.foi.rampu.dabroviapp.fragments.ProfileFragment
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.repository.CategoryRepository
import hr.foi.rampu.dabroviapp.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var viewPager2: ViewPager2
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SessionManager.initialize(applicationContext)

        viewPager2 = findViewById(R.id.viewpager)
        bottomNavigationView = findViewById(R.id.tabs)

        lifecycleScope.launch {
            val locations = fetchLocations()
            val categories = fetchCategories();

            SessionManager.saveLocations(locations)
            SessionManager.saveCategories(categories)

            val profileFragment = ProfileFragment()


            val mainPagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle)

            mainPagerAdapter.addFragment(
                MainPagerAdapter.FragmentItem(
                    R.string.home,
                    R.drawable.ic_baseline_home_24,
                    HomeFragment::class
                )
            )

            mainPagerAdapter.addFragment(
                MainPagerAdapter.FragmentItem(
                    R.string.fav,
                    R.drawable.ic_rounded_bookmark_heart_24,
                    FavoriteFragment::class
                )
            )

            mainPagerAdapter.addFragment(
                MainPagerAdapter.FragmentItem(
                    R.string.add,
                    R.drawable.ic_baseline_add_circle_24,
                    AddFragment::class
                )
            )

            mainPagerAdapter.addFragment(
                MainPagerAdapter.FragmentItem(
                    R.string.message,
                    R.drawable.ic_outline_business_messages_24,
                    MessageFragment::class
                )
            )

            mainPagerAdapter.addFragment(
                MainPagerAdapter.FragmentItem(
                    R.string.profile,
                    R.drawable.ic_baseline_accessibility_new_24,
                    profileFragment::class
                )
            )

            viewPager2.adapter = mainPagerAdapter
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> viewPager2.setCurrentItem(0, true)
                R.id.nav_fav -> viewPager2.setCurrentItem(1, true)
                R.id.nav_add -> viewPager2.setCurrentItem(2, true)
                R.id.nav_message -> viewPager2.setCurrentItem(3, true)
                R.id.nav_profile -> viewPager2.setCurrentItem(4, true)
            }
            true
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private suspend fun fetchCategories(): List<Category> {
        val categories = withContext(Dispatchers.IO) {
            CategoryRepository.getAllCategories()
        }
        Log.d("BRAT", "$categories")
        return categories ?: emptyList()
    }

    private suspend fun fetchLocations(): List<Location> {
        val locations = withContext(Dispatchers.IO) {
            LocationRepository.getAllLocations()
        }
        Log.d("BRAT", "$locations")
        return locations ?: emptyList()
    }
}
