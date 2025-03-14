package hr.foi.rampu.dabroviapp.helpers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import hr.foi.rampu.dabroviapp.R
import java.io.IOException
import java.io.InputStream

object PhotoManager {

    fun loadProfileImage(context: Context, imageName: String, profileImageView: ImageView) {
        if (imageName.isNotEmpty()) {
            try {
                println("Trying to load profile image: $imageName")
                Log.d("ProfileUpdate", "Updated image name: $imageName")

                val inputStream: InputStream = context.assets.open("img/$imageName.png")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                profileImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                println("Failed to load image: $imageName")
                profileImageView.setImageResource(R.drawable.ic_outline_boy_24)
            }
        } else {
            profileImageView.setImageResource(R.drawable.ic_outline_boy_24)
        }
    }


    fun showImagePickerDialog(context: Context, profileImageView: ImageView, onImageSelected: (String) -> Unit) {
        val imageFiles = try {
            context.assets.list("img")?.toList() ?: emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }

        if (imageFiles.isNotEmpty()) {
            val scrollView = HorizontalScrollView(context)
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            scrollView.addView(linearLayout)

            for (imageName in imageFiles) {
                val imageView = ImageView(context)
                imageView.layoutParams = LinearLayout.LayoutParams(200, 200)
                imageView.setPadding(10, 10, 10, 10)

                try {
                    val inputStream: InputStream = context.assets.open("img/$imageName")
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageView.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                imageView.setOnClickListener {
                    onImageSelected(imageName)
                    setProfileImage(context, imageName, profileImageView)
                }
                linearLayout.addView(imageView)
            }

            val dialog = AlertDialog.Builder(context)
                .setTitle("Select Profile Image")
                .setView(scrollView)
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }
    }

    fun setProfileImage(context: Context, imageName: String, profileImageView: ImageView) {
        try {
            val inputStream: InputStream = context.assets.open("img/$imageName")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            profileImageView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
