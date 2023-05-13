package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase


class DeckEditViewModel(application: Application): AndroidViewModel(application) {

    // Application context
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    val getContext = CardDatabase.getInstance(context).cardDao

    private val deckId = MutableLiveData<Long>()

    // Get deck from ID
    val deck: LiveData<Deck> = Transformations.switchMap(deckId) {
        CardDatabase.getInstance(context).cardDao.getDeck(it, userId = Firebase.auth.currentUser!!.uid)
    }

    fun loadCardDeck(id: Long) {
        deckId.value = id
    }
}