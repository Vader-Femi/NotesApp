package com.nosa.notesapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.nosa.notesapp.R
import com.nosa.notesapp.activities.MainActivity
import com.nosa.notesapp.databinding.BottomSheetLayoutBinding
import com.nosa.notesapp.databinding.FragmentSaveOrDeleteBinding
import com.nosa.notesapp.model.Note
import com.nosa.notesapp.utils.hideKeyboard
import com.nosa.notesapp.viewModel.NoteActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SaveOrDeleteFragment : Fragment(R.layout.fragment_save_or_delete) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveOrDeleteBinding
    private var note: Note? = null
    private var colour = -1
    private lateinit var result: String
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getDateInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrDeleteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementEnterTransition = animation


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding = FragmentSaveOrDeleteBinding.bind(view)

        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

        ViewCompat.setTransitionName(
            contentBinding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        contentBinding.noteContentFragmentParent.setBackgroundColor(Color.WHITE)

        contentBinding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        contentBinding.saveNote.setOnClickListener {
            saveNote()
        }

        try {
            contentBinding.etNoteContent.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    contentBinding.bottomBar.visibility = View.VISIBLE
                    contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                } else
                    contentBinding.bottomBar.visibility = View.GONE
            }
        } catch (e: Throwable) {
            Log.d("TAG", e.stackTraceToString())
        }

        contentBinding.fabColourPick.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

            val bottomSheetView: View = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }

            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colourPicker.apply {
                    setSelectedColor(colour)
                    setOnColorSelectedListener { value ->
                        colour = value
                        contentBinding.apply {
                            noteContentFragmentParent.setBackgroundColor(colour)
                            toolbarFragmentNoteContent.setBackgroundColor(colour)
                            bottomBar.setBackgroundColor(colour)
                            activity.window.statusBarColor = colour
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(colour)
                    }
                }
                bottomSheetParent.setCardBackgroundColor(colour)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        setUpNote()
    }

    private fun setUpNote() {
        val note = args.note
        val title = contentBinding.etTitle
        val content = contentBinding.etNoteContent
        val lastEdited = contentBinding.lastEdited

        if (note == null){
            contentBinding.lastEdited.text = getString(R.string.edited_on, currentDate)
        }
        if (note != null) {
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdited.text = getString(R.string.edited_on, note.date)
            colour = note.colour
            contentBinding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(colour)
                }
                toolbarFragmentNoteContent.setBackgroundColor(colour)
                bottomBar.setBackgroundColor(colour)
            }
            activity?.window?.statusBarColor = note.colour
        }
    }

    private fun saveNote() {
        if (contentBinding.etNoteContent.text.toString().isEmpty() ||
            contentBinding.etTitle.text.toString().isEmpty()
        ) {
            Toast.makeText(activity, "A Field is empty", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note
            when (note) {
                null -> {
                    noteActivityViewModel.saveNote(
                        Note(
                            0,
                            contentBinding.etTitle.text.toString(),
                            contentBinding.etNoteContent.getMD(),
                            currentDate,
                            colour
                        )
                    )

                    result = "Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )

                    navController.navigate(SaveOrDeleteFragmentDirections.actionSaveOrDeleteFragmentToNoteFragment())
                }
                else -> {
                    updateNote()
                    navController.popBackStack()
                }
            }
        }

    }

    private fun updateNote() {
        if (note != null) {
            noteActivityViewModel.updateNote(
                Note(
                    note!!.id,
                    contentBinding.etTitle.text.toString(),
                    contentBinding.etNoteContent.getMD(),
                    currentDate,
                    colour
                )
            )
        }
    }
}
