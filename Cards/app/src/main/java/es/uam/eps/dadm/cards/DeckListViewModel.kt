package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase


class DeckListViewModel(application: Application) : AndroidViewModel(application) {

    // Application context
    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext
    val cardDao = CardDatabase.getInstance(context).cardDao

    val decks = cardDao.getDecks(Firebase.auth.currentUser!!.uid)

    val userCards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao
        .getCards(Firebase.auth.currentUser!!.uid)

    val userDecks: LiveData<List<Deck>> = CardDatabase.getInstance(context).cardDao
        .getDecks(Firebase.auth.currentUser!!.uid)

    val highestDeckId: LiveData<Long?> = CardDatabase.getInstance(context).cardDao.getHighestDeckId()
}