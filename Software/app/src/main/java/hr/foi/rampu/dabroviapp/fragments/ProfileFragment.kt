package hr.foi.rampu.dabroviapp.fragments

import Location
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.authentication.LoginActivity
import hr.foi.rampu.dabroviapp.authentication.RegistrationActivity
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import hr.foi.rampu.dabroviapp.helpers.PhotoManager
import hr.foi.rampu.dabroviapp.reviews.ShowReviewsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import hr.foi.rampu.dabroviapp.statistics.StatisticsActivity

class ProfileFragment : Fragment() {
    private lateinit var layoutNotLoggedIn: ConstraintLayout
    private lateinit var layoutLoggedIn: ConstraintLayout

    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var contactEditText: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var locationSpinner: Spinner
    private lateinit var languageSpinner: Spinner

    private var selectedImageName: String = ""
    private var selectedLocation: String = ""

    private var locationList: List<Location> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        context?.let { SessionManager.initialize(it) }

        layoutNotLoggedIn = view.findViewById(R.id.layoutNotLoggedIn)
        layoutLoggedIn = view.findViewById(R.id.layoutLoggedIn)
        emailEditText = view.findViewById(R.id.txtEmail)
        usernameEditText = view.findViewById(R.id.txtUsername)
        contactEditText = view.findViewById(R.id.txtContact)
        profileImageView = view.findViewById(R.id.userPFP)
        saveButton = view.findViewById(R.id.saveButton)
        locationSpinner = view.findViewById(R.id.locationSpinner)
        languageSpinner = view.findViewById(R.id.languageSpinner)

        locationList = SessionManager.getLocations() ?: emptyList()
        if (locationList.isNotEmpty()) {
            Log.d("ProfileFragment", "Locations: $locationList")
            val locationNames = locationList.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locationNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        } else {
            Log.e("ProfileFragment", "Location list is empty.")
        }

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLocation = locationList[position].name
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedLocation = ""
            }
        }


        view.findViewById<Button>(R.id.btnLogin).setOnClickListener {
            startLoginActivity()
        }


        view.findViewById<Button>(R.id.btnRegistration).setOnClickListener {
            startRegistrationActivity()
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            SessionManager.logout()
            updateUIBasedOnLoginStatus()
        }

        val user = SessionManager.getUser()


        if(user != null){
            view.findViewById<Button>(R.id.btnShowReviews).setOnClickListener {
                val intent = Intent(requireContext(), ShowReviewsActivity::class.java)

                intent.putExtra("senderId", user.id)

                startActivity(intent)
            }
        }

        view.findViewById<Button>(R.id.btnStatistics).setOnClickListener {
            startStatisticsActivity()
        }

        profileImageView.setOnClickListener {
            PhotoManager.showImagePickerDialog(requireContext(), profileImageView) { imageName ->
                Log.d("ProfileFragment", "Image Name Selected: $imageName")
                selectedImageName = imageName
            }
        }

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLocation = locationList[position]
                Log.d("ProfileFragment", "Selected Location: ID=${selectedLocation.id}, Name=${selectedLocation.name}")
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedLocation = ""
                Log.d("ProfileFragment", "No location selected")
            }
        }


        saveButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val newUsername = usernameEditText.text.toString()
            val newContact = contactEditText.text.toString()

            val language = when (languageSpinner.selectedItemPosition) {
                0 -> "en"
                1 -> "hr"
                2 -> "de"
                else -> "en"
            }

            val selectedLocationIndex = locationSpinner.selectedItemPosition
            val selectedLocationId = if (selectedLocationIndex >= 0 && selectedLocationIndex < locationList.size) {
                locationList[selectedLocationIndex].id
            } else {
                null
            }

            val requiredFieldsFilled =
                email.isNotEmpty() && newUsername.isNotEmpty() && newContact.isNotEmpty()

            if (!requiredFieldsFilled || selectedLocationIndex < 0 || selectedImageName.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.pleaseFillAllFields,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val imageNameWithoutExtension = selectedImageName.substringBeforeLast(".", selectedImageName)

                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        SessionManager.updateUserProfile(
                            email,
                            newUsername,
                            newContact,
                            imageNameWithoutExtension,
                            language,
                            selectedLocationId
                        )

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                R.string.profileUpdateYes,
                                Toast.LENGTH_SHORT
                            ).show()
                            context?.let { applyLanguage(language) }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        if (savedInstanceState != null) {
            selectedImageName = savedInstanceState.getString("SELECTED_IMAGE_NAME", "")
        }

        updateUIBasedOnLoginStatus()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SELECTED_IMAGE_NAME", selectedImageName)
    }

    private fun applyLanguage(languageCode: String) {
        LocaleManager.applyLocale(requireContext(), languageCode)
        requireActivity().recreate()
    }

    private fun startRegistrationActivity() {
        val intent = Intent(context, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun startLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun startStatisticsActivity() {
        val intent = Intent(requireContext(), StatisticsActivity::class.java)
        startActivity(intent)
    }

    private fun updateUIBasedOnLoginStatus() {
        if (SessionManager.isLoggedIn()) {
            val user = SessionManager.getUser()

            layoutLoggedIn.visibility = View.VISIBLE
            layoutNotLoggedIn.visibility = View.GONE

            if (user != null) {
                usernameEditText.setText(user.username)
            }
            emailEditText.setText(user!!.email)
            contactEditText.setText(user.contact)

            val imageName = user.profileImg
            Log.d("ProfileFragment", "Loaded profile image: $imageName")

            selectedImageName = imageName ?: ""
            Log.d("ProfileFragment", "User loaded: $user")
            if (!imageName.isNullOrEmpty()) {
                PhotoManager.loadProfileImage(requireContext(), imageName, profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.ic_outline_boy_24)
            }

            val locationPosition = user.locationId
            if (locationPosition in locationList.indices) {
                if (locationPosition != null) {
                    locationSpinner.setSelection(locationPosition-1)
                }
            }

            val userLanguageCode = user.language
            val languagePosition = when (userLanguageCode) {
                "en" -> 0
                "hr" -> 1
                "de" -> 2
                else -> 0
            }

            languageSpinner.setSelection(languagePosition)
        } else {
            layoutLoggedIn.visibility = View.GONE
            layoutNotLoggedIn.visibility = View.VISIBLE
            profileImageView.setImageResource(R.drawable.ic_outline_boy_24)
            selectedImageName = ""
        }
    }
}
