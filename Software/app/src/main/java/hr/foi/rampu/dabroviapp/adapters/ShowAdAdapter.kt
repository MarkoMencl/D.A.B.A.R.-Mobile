package hr.foi.rampu.dabroviapp.adapters

import Ad
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import hr.foi.rampu.dabroviapp.advertisements.AdDetailsActivity
import hr.foi.rampu.dabroviapp.repository.ImgRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShowAdAdapter(
    private val ads: List<Ad?>,
    private val showCancelButton: Boolean,
    private val onCancelClick: (Ad) -> Unit
) : RecyclerView.Adapter<ShowAdAdapter.AdViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_ad_item, parent, false)
        return AdViewHolder(view, showCancelButton, onCancelClick)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = ads[position]
        if (ad != null) {
            holder.bind(ad)
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdDetailsActivity::class.java)
            if (ad != null) {
                intent.putExtra("adId", ad.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = ads.size

    class AdViewHolder(
        itemView: View,
        private val showCancelButton: Boolean,
        private val onCancelClick: (Ad) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val adTitle: TextView = itemView.findViewById(R.id.ad_title)
        private val adPrice: TextView = itemView.findViewById(R.id.ad_price)
        private val adImage: ImageView = itemView.findViewById(R.id.ad_image)
        private val cancelIcon: ImageView = itemView.findViewById(R.id.cancel_icon)

        fun bind(ad: Ad) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val image = ad.id?.let { ImgRepository.getTheImageByAdId(it) }
                    if (image != null && image.bitmap.isNotEmpty()) {
                        val bitmap = decodeBase64ToBitmap(image.bitmap)
                        adImage.setImageBitmap(bitmap)
                    } else {
                        adImage.setImageResource(R.drawable.ic_placeholder)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    adImage.setImageResource(R.drawable.ic_placeholder)
                }
            }

            adTitle.text = ad.title
            adPrice.text = "€${ad.price}"

            cancelIcon.visibility = if (showCancelButton) View.VISIBLE else View.GONE
            cancelIcon.setOnClickListener {
                onCancelClick(ad)
            }
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

        /*private fun loadImageFromURL(imageView: ImageView, url: String) {
            Thread {
                try {
                    val inputStream = java.net.URL(url).openStream()
                    val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, "src")
                    imageView.post {
                        imageView.setImageDrawable(drawable)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }*/
    }
}

