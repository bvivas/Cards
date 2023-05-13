package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase


class CardListViewModel(application: Application) : AndroidViewModel(application) {

    // Application context
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    val getContext = CardDatabase.getInstance(context).cardDao

    // Get the list of cards
    val cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao
        .getCards(Firebase.auth.currentUser!!.uid)
}