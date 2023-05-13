package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase
import java.time.LocalDateTime
import java.util.concurrent.Executors


class StudyViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext

    private val executor = Executors.newSingleThreadExecutor()

    var card: Card? = null
    val cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao.getCards(Firebase.auth.currentUser!!.uid)

    // Maximum number of cards allowed to study
    private var maxNumCards = 20

    var nDueCards: LiveData<Int> = Transformations.map(cards) { c ->
        c.filter {
            it.isDue(LocalDateTime.now())
        }.size
    }

    var dueCard: LiveData<Card?> = Transformations.map(cards) { c ->
        try {
            if(maxNumCards > 0) {
                maxNumCards -= 1
                c.filter {
                    it.isDue(LocalDateTime.now())
                }.random()
            } else {
                null
            }
        } catch(e: Exception) {
            null
        }
    }

    init {
        // Get the maximum number of cards from the preferences
        val maxCards = SettingsActivity.getMaximumNumberOfCards(context)
        maxNumCards = maxCards!!.toInt()
    }

    fun update(quality: Int) {
        card?.quality = quality
        card?.update(LocalDateTime.now())

        executor.execute {
            CardDatabase.getInstance(context).cardDao.updateCard(card!!)
        }
    }
}