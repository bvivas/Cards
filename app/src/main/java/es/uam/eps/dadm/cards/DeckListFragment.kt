package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.databinding.FragmentDeckListBinding
import java.util.concurrent.Executors


class DeckListFragment : Fragment() {

    private lateinit var binding: FragmentDeckListBinding
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var adapter: DeckAdapter
    private lateinit var intent: Intent
    private lateinit var userInfo: UserInfo
    private var deckId: Long = 0L

    // Reference to cards
    private var reference = FirebaseDatabase
        .getInstance()
        .getReference("cards")

    // Reference to decks
    private var deckReference = FirebaseDatabase
        .getInstance()
        .getReference("decks")


    // ViewModel initialization
    private val deckListViewModel by lazy {
        ViewModelProvider(this).get(DeckListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        intent = Intent(context, EmailPasswordActivity::class.java)
        userInfo = UserInfo(Firebase.auth.currentUser!!.email!!, "")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deck_list,
            container,
            false)

        adapter = DeckAdapter()
        adapter.data = emptyList()
        // Decks observer
        deckListViewModel.decks.observe(viewLifecycleOwner,
            Observer {
                adapter.data = it
                adapter.notifyDataSetChanged()
            }
        )
        binding.cardRecyclerViewDeck.adapter = adapter

        deckListViewModel.userCards.observe(viewLifecycleOwner,
            Observer { binding.invalidateAll() }
        )

        deckListViewModel.userDecks.observe(viewLifecycleOwner,
            Observer { binding.invalidateAll() }
        )

        // Get the highest ID of the decks to assign it to the next new deck
        deckListViewModel.highestDeckId.observe(viewLifecycleOwner,
            Observer {
            deckId = it ?: 0
        })

        // Add new deck
        binding.newDeckFab.setOnClickListener {
            val deck = Deck("", deckId, userId = Firebase.auth.currentUser!!.uid)
            executor.execute {
                deckListViewModel.cardDao.addDeck(deck)
            }
            // Edit deck to finish the process
            it.findNavController()
                .navigate(DeckListFragmentDirections
                    .actionDeckListFragmentToDeckEditFragment(deck.deckId))
        }

        // Upload cards and decks to Firebase
        binding.uploadFirebaseFab.setOnClickListener {
            // Upload cards
            for(card in deckListViewModel.userCards.value!!) {
                reference.child(card.id).setValue(card)
            }
            // Upload decks
            for(deck in deckListViewModel.userDecks.value!!) {
                deckReference.child(deck.deckId.toString()).setValue(deck)
            }
            Snackbar.make(binding.root, R.string.decks_cards_firebase, Snackbar.LENGTH_LONG)
                .show()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_card_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
            R.id.log_out -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(intent)
            }
        }
        return true
    }
}