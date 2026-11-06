package com.thiagosilva.navegabilidadeentreastelas.data.model

import android.os.Parcelable
import com.thiagosilva.navegabilidadeentreastelas.util.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: String = "",
    var description: String = "",
    var status: Status = Status.TODO
) : Parcelable {
    init {
        // Mantém o ID de uma tarefa recebida para edição.
        if (id.isEmpty()) {
            id = FirebaseHelper.getDatabase().push().key ?: ""
        }
    }
}
