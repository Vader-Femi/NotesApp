package com.nosa.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note (

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val colour: Int = -1

):Serializable //To be able to pass the whole fragment as an object instead of using Intent