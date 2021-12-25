package com.nosa.notesapp.viewModel

import android.app.DownloadManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nosa.notesapp.model.Note
import com.nosa.notesapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val repository: NoteRepository): ViewModel() {

    fun saveNote(newNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.addNote(newNote)
    }

    fun updateNote(existiongNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(existiongNote)
    }

    fun deleteNote(existiongNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(existiongNote)
    }

    fun searchNote(query: String): LiveData<List<Note>>{
        return repository.searchNote(query)
    }

    fun  getAllNotes(): LiveData<List<Note>> = repository.getNote()
}