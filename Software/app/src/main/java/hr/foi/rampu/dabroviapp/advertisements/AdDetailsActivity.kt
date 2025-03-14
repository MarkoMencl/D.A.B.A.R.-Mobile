package hr.foi.rampu.dabroviapp.advertisements

import Ad
import Image
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.adapters.ReviewAdapter
import hr.foi.rampu.dabroviapp.chat.ChatActivity
import hr.foi.rampu.dabroviapp.entities.Review
import hr.foi.rampu.dabroviapp.entities.User
import hr.foi.rampu.dabroviapp.helpers.PhotoManager
import hr.foi.rampu.dabroviapp.helpers.ReviewDialogHelper
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.repository.AdRepository
import hr.foi.rampu.dabroviapp.repository.ImgRepository
import hr.foi.rampu.dabroviapp.repository.LocationRepository
import hr.foi.rampu.dabroviapp.repository.UserRepository
import hr.foi.rampu.dabroviapp.ws.ReviewRequest
import hr.foi.rampu.dabroviapp.ws.WsReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdDetailsActivity : AppCompatActivity() {

    private lateinit var adImage: ImageView
    private lateinit var favoriteIcon: ImageView
    private lateinit var galleryLayout: LinearLayout
    private var isFavorite: Boolean = false
    private lateinit var addReviewButton: Button
    private lateinit var recyclerView: RecyclerView
    private var currentAd: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_details)

        val adId = intent.getIntExtra("adId", -1)
        Log.d("DEBUG", "Received adId: $adId")

        if (adId != -1) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    AdRepository.incrementAdViews(adId)
                }
            }
        }

        lifecycleScope.launch {
            try {
                val ad = withContext(Dispatchers.IO) {
                    AdRepository.getAdById(adId)
                }
                val images = withContext(Dispatchers.IO) {
                    ImgRepository.getImagesByAdId(adId)
                }

                Log.d("DEBUG", "API Response: $ad")

                if (ad != null) {
                    setupUI(ad, images)
                } else {
                    Log.e("DEBUG", "Ad is null, finishing activity.")
                    finish()
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "Error fetching ad", e)
            }
        }
    }

    private fun setupUI(ad: Ad, images: List<Image>?) {
        currentAd = ad
        updateFavoriteIconState(ad.id ?: -1)
        adImage = findViewById(R.id.ad_image)
        galleryLayout = findViewById(R.id.gallery_layout)
        favoriteIcon = findViewById(R.id.favorite_icon)
        addReviewButton = findViewById(R.id.btnAddReview)
        recyclerView = findViewById(R.id.reviews_recycler_view)
        val adTitle: TextView = findViewById(R.id.ad_title)
        val adViewCount: TextView = findViewById(R.id.ad_view_count)
        val adDescription: TextView = findViewById(R.id.ad_description)
        val adStatus: TextView = findViewById(R.id.ad_status)
        val adPrice: TextView = findViewById(R.id.ad_price)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.top_bar_chat)

        val uploaderName: TextView = findViewById(R.id.uploader_name)
        val uploaderLocation: TextView = findViewById(R.id.uploader_location)
        val uploaderImage: ImageView = findViewById(R.id.uploader_image)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val statusArray = resources.getStringArray(R.array.status_values)
        val statusText = if (ad.status in statusArray.indices) {
            statusArray[ad.status]
        } else {
            "Unknown Status"
        }

        favoriteIcon.setOnClickListener {
            toggleFavoriteStatus()
        }

        addReviewButton.setOnClickListener {
            val user = SessionManager.getUser()

            if(user != null && user.id != ad.userId){
                addReview(user, ad)
            }
        }

        recyclerView.adapter = ReviewAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this@AdDetailsActivity)
        fetchReviews(ad.userId)

        adTitle.text = ad.title
        adViewCount.text = "Views: ${ad.views}"
        adDescription.text = ad.description
        adStatus.text = statusText
        adPrice.text = "€${ad.price}"

        if (!images.isNullOrEmpty()) {
            val firstImage = images[0]
            val firstImageBase64 = firstImage.bitmap

            if (!firstImageBase64.isNullOrEmpty()) {
                val bitmap = decodeBase64ToBitmap(firstImageBase64)
                if (bitmap != null) {
                    adImage.setImageBitmap(bitmap)
                    adImage.tag = firstImageBase64
                } else {
                    adImage.setImageResource(R.drawable.ic_placeholder)
                }
            } else {
                adImage.setImageResource(R.drawable.ic_placeholder)
            }

            galleryLayout.removeAllViews()
            for (image in images) {
                val imageBase64 = image.bitmap
                val bitmap = decodeBase64ToBitmap(imageBase64)
                if (bitmap != null) {
                    val imageView = ImageView(this)

                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

                    val imageHeight = galleryLayout.height
                    val imageWidth = (imageHeight * aspectRatio).toInt()

                    val params = LinearLayout.LayoutParams(
                        imageWidth,
                        imageHeight
                    )

                    imageView.layoutParams = params
                    imageView.setImageBitmap(bitmap)
                    galleryLayout.addView(imageView)
                }
            }

            adImage.setOnClickListener {
                val currentBase64 = adImage.tag as? String
                if (!currentBase64.isNullOrEmpty()) {
                    showFullScreenImage(currentBase64)
                }
            }

        } else {
            adImage.setImageResource(R.drawable.ic_placeholder)
        }

        lifecycleScope.launch {
            val user = UserRepository.getUserById(ad.userId)

            if (user != null) {
                val locationName = LocationRepository.getLocationById(user.id)

                uploaderName.text = user.username

                if (locationName != null) {
                    uploaderLocation.text = locationName.name
                }
                val imageName = user.profileImg
                if (imageName != null) {
                    PhotoManager.loadProfileImage(this@AdDetailsActivity, imageName, uploaderImage)
                }
            }

            val currentUser = SessionManager.getUser()

            val contactButton: Button = findViewById(R.id.contact_seller_button)
            contactButton.setOnClickListener {
                handleContactClick(currentUser, ad)
            }
        }
    }

    private fun fetchReviews(userId: Int) {
        WsReview.reviewService.getReceiverReview(userId).enqueue(object: Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                if(response.isSuccessful){
                    val reviews = response.body()

                    if(reviews != null){
                        val reviewAdapter = ReviewAdapter(reviews)
                        recyclerView.adapter = reviewAdapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                Toast.makeText(this@AdDetailsActivity, R.string.failedToFetchMessages, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addReview(user: User, ad: Ad) {
        ReviewDialogHelper.showNewReviewDialog(this@AdDetailsActivity,
            user.id, ad.userId) { reviewRequest, dialog ->
            addNewReview(reviewRequest, dialog)
        }
    }

    private fun addNewReview(reviewRequest: ReviewRequest, dialog: DialogInterface) {
        WsReview.reviewService.postReview(reviewRequest).enqueue(object: Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if(response.isSuccessful && response.body() != null){
                    dialog.dismiss()
                }else{
                    Toast.makeText(this@AdDetailsActivity, R.string.reviewAddReviewFailed, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(this@AdDetailsActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }


    private fun handleContactClick(user: User?, ad: Ad) {
        if (user != null) {
            if (user.id != ad.userId) {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("currentUserId", user.id.toString())
                intent.putExtra("otherUserId", ad.userId.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, R.string.messageToMyself, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.needToBeLogged, Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFavoriteStatus() {
        val ad = currentAd ?: run {
            Toast.makeText(this, "Ad details not available", Toast.LENGTH_SHORT).show()
            return
        }
        val user = SessionManager.getUser() ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        val adId = ad.id ?: run {
            Toast.makeText(this, "Ad ID not available", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = user.id ?: run {
            Toast.makeText(this, "User ID not available", Toast.LENGTH_SHORT).show()
            return
        }

        if (isFavorite) {
            lifecycleScope.launch {
                val success = withContext(Dispatchers.IO) {
                    AdRepository.deleteFavoriteAd(adId, userId)
                }
                if (success) {
                    isFavorite = false
                    favoriteIcon.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                    Toast.makeText(this@AdDetailsActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AdDetailsActivity, "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            lifecycleScope.launch {
                val success = withContext(Dispatchers.IO) {
                    AdRepository.addAdToFavourites(adId, userId)
                }
                if (success) {
                    isFavorite = true
                    favoriteIcon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                    Toast.makeText(this@AdDetailsActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AdDetailsActivity, "Failed to add favorite", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateFavoriteIconState(adId: Int) {
        lifecycleScope.launch {
            val favorites = withContext(Dispatchers.IO) {
                AdRepository.getUserFavoriteAds() ?: emptyList()
            }
            isFavorite = favorites.any { it.id == adId }
            favoriteIcon.setImageResource(
                if (isFavorite) R.drawable.ic_baseline_check_circle_24
                else R.drawable.ic_baseline_check_circle_outline_24
            )
        }
    }



    private fun showFullScreenImage(base64Image: String?) {
        if (base64Image.isNullOrEmpty()) return

        val bitmap = decodeBase64ToBitmap(base64Image)
        if (bitmap == null) return

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val fullScreenImage: ImageView = dialog.findViewById(R.id.full_screen_image)
        fullScreenImage.setImageBitmap(bitmap)

        val matrix = Matrix()
        val scaleFactor = 4.0f
        matrix.postScale(scaleFactor, scaleFactor, fullScreenImage.width / 2f, fullScreenImage.height / 2f)
        fullScreenImage.imageMatrix = matrix
        fullScreenImage.scaleType = ImageView.ScaleType.MATRIX

        var lastTouchX = 0f
        var lastTouchY = 0f

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                dialog.dismiss()
                return true
            }
        })

        fullScreenImage.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY

                    matrix.postTranslate(dx, dy)
                    fullScreenImage.imageMatrix = matrix
                    fullScreenImage.invalidate()

                    lastTouchX = event.x
                    lastTouchY = event.y
                }
            }
            return@setOnTouchListener true
        }
        dialog.show()
    }
}
