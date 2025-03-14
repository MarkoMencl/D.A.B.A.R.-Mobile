package hr.foi.rampu.dabroviapp.reviews

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.MainPagerAdapter
import hr.foi.rampu.dabroviapp.fragments.AddFragment
import hr.foi.rampu.dabroviapp.fragments.FavoriteFragment
import hr.foi.rampu.dabroviapp.fragments.MessageFragment
import hr.foi.rampu.dabroviapp.fragments.ProfileFragment
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.reviews.fragments.ReviewReceivedFragment
import hr.foi.rampu.dabroviapp.reviews.fragments.ReviewSentFragment

class ShowReviewsActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_reviews)

        viewPager2 = findViewById(R.id.vp_reviews)
        bottomNavigationView = findViewById(R.id.bnv_review)

        val mainPagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.setCurrentItem(0, false)

        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.navReviewSent,
                R.drawable.ic_baseline_call_sent_24,
                ReviewSentFragment::class
            )
        )

        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.navReviewReceived,
                R.drawable.ic_baseline_call_received_24,
                ReviewReceivedFragment::class
            )
        )

        viewPager2.adapter = mainPagerAdapter

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_review_sent -> viewPager2.setCurrentItem(0, true)
                R.id.nav_review_received -> viewPager2.setCurrentItem(1, true)
            }
            true
        }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_review)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}