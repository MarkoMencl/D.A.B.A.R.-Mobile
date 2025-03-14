import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.advertisements.AdsActivity
import hr.foi.rampu.dabroviapp.entities.Category
import org.w3c.dom.Text

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryImage: ImageView = view.findViewById(R.id.category_image)
        val categoryName: TextView = view.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val translatedName = if (!category.localizationKey.isNullOrEmpty()) {
            holder.itemView.context.getString(
                holder.itemView.context.resources.getIdentifier(
                    category.localizationKey,
                    "string",
                    holder.itemView.context.packageName
                )
            )
        } else {
            category.name
        }

        holder.categoryName.text = translatedName

        holder.categoryName.text = translatedName


        val context = holder.itemView.context
        val resourceName = category.img
        val resourceId = context.resources.getIdentifier(
            resourceName,
            "drawable",
            context.packageName
        )

        if (resourceId != 0) {
            holder.categoryImage.setImageResource(resourceId)
        } else {
            holder.categoryImage.setImageResource(R.drawable.ic_placeholder)
        }

        holder.itemView.setOnClickListener {

            onCategoryClick(category)
        }
    }
    override fun getItemCount(): Int = categories.size
}
