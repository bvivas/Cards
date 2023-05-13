package es.uam.eps.dadm.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.ListItemCardBinding
import java.util.concurrent.Executors


class CardAdapter : RecyclerView.Adapter<CardAdapter.CardHolder>() {

    private lateinit var binding: ListItemCardBinding
    private val executor = Executors.newSingleThreadExecutor()

    var data = listOf<Card>()
    var deckData = listOf<Long>()

    inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var local = binding
        private var deckId: Long = 0L

        fun bind(card: Card, deckId: Long) {
            local.card = card
            this.deckId = deckId
            itemView.setOnClickListener {
                it.findNavController()
                    .navigate(CardListFragmentDirections
                        .actionCardListFragmentToCardEditFragment(card.id, deckId))
            }
            // Listener for the delete card button
            binding.deleteCardButton.setOnClickListener {
                executor.execute {
                    Firebase.auth.currentUser!!.uid
                    val cardDatabase = CardDatabase.getInstance(context = CardsApplication())
                    cardDatabase.cardDao.deleteCardByDeck(card.question, deckId, Firebase.auth.currentUser!!.uid)
                }
                notifyItemRemoved(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ListItemCardBinding.inflate(layoutInflater, parent, false)
        binding.apply {
            // Display more info button
            listItemShowMoreButton.setOnClickListener {
                listItemQuestion.visibility = View.INVISIBLE
                listItemAnswer.visibility = View.INVISIBLE
                listItemDate.visibility = View.INVISIBLE
                listItemEasiness.visibility = View.VISIBLE
                listItemRepetitions.visibility = View.VISIBLE
                listItemInterval.visibility = View.VISIBLE
                listItemNextDate.visibility = View.VISIBLE

                listItemShowLessButton.visibility = View.VISIBLE
                listItemShowMoreButton.visibility = View.INVISIBLE
            }

            // Hide info button
            listItemShowLessButton.setOnClickListener {
                listItemQuestion.visibility = View.VISIBLE
                listItemAnswer.visibility = View.VISIBLE
                listItemDate.visibility = View.VISIBLE
                listItemEasiness.visibility = View.INVISIBLE
                listItemRepetitions.visibility = View.INVISIBLE
                listItemInterval.visibility = View.INVISIBLE
                listItemNextDate.visibility = View.INVISIBLE

                listItemShowMoreButton.visibility = View.VISIBLE
                listItemShowLessButton.visibility = View.INVISIBLE
            }
        }

        return CardHolder(binding.root)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(
        holder: CardHolder,
        position: Int
    ) {
        holder.bind(data[position], deckData[0])
    }
}