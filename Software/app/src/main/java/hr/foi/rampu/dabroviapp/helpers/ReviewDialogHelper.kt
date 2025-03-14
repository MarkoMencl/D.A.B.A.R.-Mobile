package hr.foi.rampu.dabroviapp.helpers

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.entities.Review
import hr.foi.rampu.dabroviapp.ws.ReviewRequest
import hr.foi.rampu.dabroviapp.ws.ReviewRequestUpdate

object ReviewDialogHelper {
    fun showEditReviewDialog(
        context: Context,
        review: Review,
        onSave: (reviewRequestUpdate: ReviewRequestUpdate, dialog: DialogInterface) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialogue_edit_review, null)

        val editComment: EditText = dialogView.findViewById(R.id.edit_review_comment)
        val gradeSpinner: Spinner = dialogView.findViewById(R.id.spinner_review_grade)
        val cancelButton: Button = dialogView.findViewById(R.id.btnCancel)
        val saveButton: Button = dialogView.findViewById(R.id.btnSave)
        val title: TextView = dialogView.findViewById(R.id.lblTitle)

        title.text = context.getString(R.string.edit)
        editComment.setText(review.comment)

        val gradeValues = arrayOf(1, 2, 3, 4, 5)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, gradeValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gradeSpinner.adapter = adapter

        val currentGradeIndex = gradeValues.indexOf(review.value.toInt())
        gradeSpinner.setSelection(currentGradeIndex)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        saveButton.setOnClickListener {
            val updatedComment = editComment.text.toString()
            val updatedGrade = gradeSpinner.selectedItem.toString()

            if (updatedComment.isNotEmpty() && updatedGrade.isNotEmpty()) {
                try {
                    val updatedGradeValue = updatedGrade.toInt()

                    if (updatedGradeValue in 1..5) {
                        val reviewRequestUpdate = ReviewRequestUpdate(
                            id = review.id,
                            value = updatedGrade,
                            comment = updatedComment
                        )
                        onSave(reviewRequestUpdate, alertDialog)
                    } else {
                        Toast.makeText(context, "Grade must be between 1 and 5", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Invalid grade value", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showNewReviewDialog(
        context: Context,
        reviewerId: Int,
        revieweeId: Int,
        onSave: (reviewRequest: ReviewRequest, dialog: DialogInterface) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialogue_edit_review, null)

        val editComment: EditText = dialogView.findViewById(R.id.edit_review_comment)
        val gradeSpinner: Spinner = dialogView.findViewById(R.id.spinner_review_grade)
        val cancelButton: Button = dialogView.findViewById(R.id.btnCancel)
        val saveButton: Button = dialogView.findViewById(R.id.btnSave)
        val title: TextView = dialogView.findViewById(R.id.lblTitle)

        title.text = context.getString(R.string.addReview)
        editComment.setText("")

        val gradeValues = arrayOf(1, 2, 3, 4, 5)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, gradeValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gradeSpinner.adapter = adapter

        val currentGradeIndex = gradeValues.indexOf(5)
        gradeSpinner.setSelection(currentGradeIndex)

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        saveButton.setOnClickListener {
            val updatedComment = editComment.text.toString()
            val updatedGrade = gradeSpinner.selectedItem.toString()

            if (updatedComment.isNotEmpty() && updatedGrade.isNotEmpty()) {
                try {
                    val updatedGradeValue = updatedGrade.toInt()

                    if (updatedGradeValue in 1..5) {
                        val reviewRequest = ReviewRequest(
                            reviewerId = reviewerId,
                            revieweeId = revieweeId,
                            value = updatedGrade,
                            comment = updatedComment
                        )
                        onSave(reviewRequest, alertDialog)
                    } else {
                        Toast.makeText(context, "Grade must be between 1 and 5", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Invalid grade value", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}