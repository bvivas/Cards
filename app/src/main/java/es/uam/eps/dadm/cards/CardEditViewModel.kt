package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import es.uam.eps.dadm.cards.database.CardDatabase


class CardEditViewModel(application: Application): AndroidViewModel(application) {

    // Application context
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    val getContext = CardDatabase.getInstance(context).cardDao

    private val cardId = MutableLiveData<String>()

    // Get the card from ID
    val card: LiveData<Card> = Transformations.switchMap(cardId) {
        CardDatabase.getInstance(context).cardDao.getCard(it)
    }

    fun loadCardId(id: String) {
        cardId.value = id
    }
}