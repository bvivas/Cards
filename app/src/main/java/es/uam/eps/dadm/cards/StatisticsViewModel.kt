package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase
import java.time.LocalDateTime


class StatisticsViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    var decks: LiveData<List<DeckWithCards>> = CardDatabase.getInstance(context).cardDao.getDecksWithCards(Firebase.auth.currentUser!!.uid)

    var decksNum: LiveData<List<Deck>> = CardDatabase.getInstance(context).cardDao.getDecks(Firebase.auth.currentUser!!.uid)

    var cardsNum: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao.getCards(Firebase.auth.currentUser!!.uid)

    // Get the list of cards
    val cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao
        .getCards(Firebase.auth.currentUser!!.uid)

    // Get the cards for the next days
    var notDueCards: LiveData<Int> = Transformations.map(cards) { c ->
        c.filter {
            !it.isDue(LocalDateTime.now())
        }.size
    }
}