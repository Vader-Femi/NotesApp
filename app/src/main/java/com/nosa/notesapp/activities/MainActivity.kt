package com.nosa.notesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nosa.notesapp.databinding.ActivityMainBinding
import com.nosa.notesapp.db.NoteDatabase
import com.nosa.notesapp.fragments.NoteFragment
import com.nosa.notesapp.repository.NoteRepository
import com.nosa.notesapp.viewModel.NoteActivityViewModel
import com.nosa.notesapp.viewModel.NoteActivityViewModelFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)

        try {
            setContentView(binding.root)
            val noteRepository = NoteRepository(NoteDatabase(this))
            val noteActivityViewModelFactory = NoteActivityViewModelFactory(noteRepository)
            noteActivityViewModel =
                ViewModelProvider(
                    this,
                    noteActivityViewModelFactory
                )[NoteActivityViewModel::class.java]
        }catch (e: Exception){
            Log.d("TAG", "Error Instantiating database")
        }

    }
}