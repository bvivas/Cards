package es.uam.eps.dadm.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.ListItemDeckBinding
import java.util.concurrent.Executors


class DeckAdapter : RecyclerView.Adapter<DeckAdapter.DeckHolder>() {

    lateinit var binding: ListItemDeckBinding
    private val executor = Executors.newSingleThreadExecutor()

    var data = listOf<Deck>()

    inner class DeckHolder(view: View): RecyclerView.ViewHolder(view) {
        private var local = binding

        fun bind(deck: Deck) {
            local.deck = deck
            binding.deck = deck

           // See the cards of a deck
            binding.deckToCardsButton.setOnClickListener {
                it.findNavController()
                    .navigate(DeckListFragmentDirections
                        .actionDeckListFragmentToCardListFragment(deck.deckId))
            }

            // Edit deck
            binding.itemView.setOnClickListener {
                it.findNavController()
                    .navigate(DeckListFragmentDirections
                        .actionDeckListFragmentToDeckEditFragment(deck.deckId))
            }

            // Delete deck
            binding.deleteDeckButton.setOnClickListener {
                executor.execute {
                    val cardDatabase = CardDatabase.getInstance(context = CardsApplication())
                    cardDatabase.cardDao.deleteDeckByName(deck.name, Firebase.auth.currentUser!!.uid)
                    // Delete the cards of the deleted deck
                    cardDatabase.cardDao.deleteAllCardsByDeck(deck.deckId, Firebase.auth.currentUser!!.uid)
                }
                notifyItemRemoved(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ListItemDeckBinding.inflate(layoutInflater, parent, false)
        return DeckHolder(binding.root)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: DeckHolder, position: Int) {
        holder.bind(data[position])
    }
}