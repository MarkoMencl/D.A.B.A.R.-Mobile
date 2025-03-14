package hr.foi.rampu.dabroviapp.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import java.io.IOException
import java.io.InputStream

class ImageGridAdapter(private val context: Context, private val imageFiles: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return imageFiles.size
    }

    override fun getItem(position: Int): Any {
        return imageFiles[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(200, 200)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView = convertView as ImageView
        }

        val imageName = imageFiles[position]
        try {
            val inputStream: InputStream = context.assets.open("img/$imageName")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageView
    }
}
