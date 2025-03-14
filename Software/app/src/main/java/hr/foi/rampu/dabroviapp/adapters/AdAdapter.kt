package hr.foi.rampu.dabroviapp.adapters

import Ad
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.app.AlertDialog
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import android.util.Log
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.repository.AdRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdAdapter(
    private val ads: MutableList<Ad>
) : RecyclerView.Adapter<AdAdapter.AdViewHolder>() {

    inner class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.adDescription)
        val priceTextView: TextView = itemView.findViewById(R.id.adPrice)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ad, parent, false)
        return AdViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = ads[position]
        holder.descriptionTextView.text = ad.description
        holder.priceTextView.text = "Price: ${ad.price}"

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Potvrda brisanja")
                .setMessage("Jeste li sigurni da želite obrisati ovaj oglas?")
                .setPositiveButton("Da") { _, _ ->
                    Log.d("DeleteAd", "Positive button clicked")
                    ad.id?.let { id ->
                        Log.d("DeleteAd", "Ad id is not null: $id")
                        CoroutineScope(Dispatchers.IO).launch {
                            AdRepository.deleteAd(id)
                            CoroutineScope(Dispatchers.Main).launch {
                                ads.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, ads.size)
                            }
                        }
                    } ?: run {
                        Log.e("DeleteAd", "ad.id is null!")
                    }
                }
                .setNegativeButton("Ne") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_ad, null)
            val editTitle = dialogView.findViewById<EditText>(R.id.editAdTitle)
            val editDescription = dialogView.findViewById<EditText>(R.id.editAdDescription)
            val editPrice = dialogView.findViewById<EditText>(R.id.editAdPrice)

            editTitle.setText(ad.title)
            editDescription.setText(ad.description)
            editPrice.setText(ad.price.toString())

            AlertDialog.Builder(context)
                .setTitle("Edit Ad")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val newTitle = editTitle.text.toString()
                    val newDescription = editDescription.text.toString()
                    val newPrice = editPrice.text.toString().toDoubleOrNull()

                    if (!newTitle.isNullOrEmpty() && !newDescription.isNullOrEmpty() && newPrice != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            CoroutineScope(Dispatchers.Main).launch {
                                val success = AdRepository.updateAd(ad.id!!, newTitle, newDescription, newPrice.toDouble())
                                if (success) {
                                    ads[position] = ad.copy(
                                        title = newTitle,
                                        description = newDescription,
                                        price = newPrice.toDouble(),
                                        dateOfPublication = ad.dateOfPublication ?: "Unknown"
                                    )

                                    notifyItemChanged(position)
                                    Toast.makeText(context, "Ad updated successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()
        }
    }

    override fun getItemCount(): Int = ads.size
}
