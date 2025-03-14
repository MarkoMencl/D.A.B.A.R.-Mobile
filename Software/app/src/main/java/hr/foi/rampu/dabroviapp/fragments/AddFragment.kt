package hr.foi.rampu.dabroviapp.fragments

import Image
import Location
import LocaleManager
import PostAd
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.repository.AdRepository
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.repository.ImgRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private var selectedImageBinary: ByteArray? = null
    private lateinit var imageViewAd: ImageView
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var locationSpinner: Spinner

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            imageViewAd.setImageURI(uri)

            selectedImageBinary = uriToBinary(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        val editTextTitle = view.findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextDescription)
        val editTextPrice = view.findViewById<EditText>(R.id.editTextPrice)
        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        locationSpinner = view.findViewById(R.id.spinnerLocation)
        imageViewAd = view.findViewById(R.id.imageViewAd)
        val buttonSaveAd = view.findViewById<Button>(R.id.buttonSaveAd)
        val buttonSelectImage = view.findViewById<Button>(R.id.buttonSelectImage)

        val categories = SessionManager.getCategories() ?: emptyList()

        if (categories.isNotEmpty()) {
            val categoryNames = categories.mapNotNull {
                val resId = context?.resources?.getIdentifier(it.localizationKey, "string", requireContext().packageName)

                if (resId != null && resId != 0) {
                    context?.getString(resId)
                } else {
                    null
                }
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        val locations = SessionManager.getLocations() ?: emptyList()
        if (locations.isNotEmpty()) {
            val locationNames = locations.map { it.name }
            val locationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locationNames)
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = locationAdapter
        }

        buttonSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        val buttonShowAds = view.findViewById<Button>(R.id.buttonMyAds)
        buttonShowAds.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, ShowAdsFragment())
                .addToBackStack(null)
                .commit()
        }

        buttonSaveAd.setOnClickListener {
            if (!SessionManager.isLoggedIn()) {
                Toast.makeText(requireContext(), "You need to log in to create ads!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val userId = SessionManager.getUser()?.id
            if (userId == null) {
                Toast.makeText(requireContext(), "Error: User ID not found!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val price = editTextPrice.text.toString().toDoubleOrNull()
            val status = spinnerStatus.selectedItemPosition
            val selectedCategoryPosition = spinnerCategory.selectedItemPosition
            val selectedCategory = categories[selectedCategoryPosition]
            val selectedLocationPosition = locationSpinner.selectedItemPosition
            val selectedLocation = locations[selectedLocationPosition]

            if (title.isNotEmpty() && description.isNotEmpty() && price != null && selectedImageBinary != null) {
                val image = Image(
                    bitmap = byteArrayToBase64(selectedImageBinary!!),
                    format = "image/jpeg",
                    size = selectedImageBinary!!.size
                )

                val ad = PostAd(
                    title = title,
                    description = description,
                    price = price,
                    userId = userId,
                    status = status,
                    category_id = selectedCategory.id,
                    location_id = selectedLocation.id
                )

                CoroutineScope(Dispatchers.Main).launch {
                    val isAdSaved = withContext(Dispatchers.IO) { AdRepository.addAd(ad, image) }

                    if (isAdSaved) {
                        val adId = withContext(Dispatchers.IO) { AdRepository.getLastInsertedAdId() }

                        if (adId != null) {
                            val imgId = withContext(Dispatchers.IO) { ImgRepository.getLastInsertedImgId() }
                            if (imgId != null) {
                                val isImageLinked = withContext(Dispatchers.IO) { AdRepository.linkImageToAd(adId, imgId) }
                                if (isImageLinked == true) {
                                    Toast.makeText(requireContext(), "Ad saved successfully!", Toast.LENGTH_SHORT).show()
                                    resetFields()
                                } else {
                                    Toast.makeText(requireContext(), "Failed to link image.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Failed to retrieve image ID.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to retrieve Ad ID.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to save ad.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields correctly!", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    fun byteArrayToBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun resetFields() {
        view?.findViewById<EditText>(R.id.editTextTitle)?.text?.clear()
        view?.findViewById<EditText>(R.id.editTextDescription)?.text?.clear()
        view?.findViewById<EditText>(R.id.editTextPrice)?.text?.clear()
        spinnerStatus.setSelection(0)
        imageViewAd.setImageResource(R.drawable.ic_placeholder)
        selectedImageBinary = null
    }

    private fun uriToBinary(uri: Uri): ByteArray? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
