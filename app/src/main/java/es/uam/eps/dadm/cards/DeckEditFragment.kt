package es.uam.eps.dadm.cards

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.databinding.FragmentDeckEditBinding
import java.util.concurrent.Executors


class DeckEditFragment: Fragment() {

    private lateinit var binding: FragmentDeckEditBinding
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var deck: Deck
    lateinit var name: String
    var deckId: Long = 0L

    // ViewModel initialization
    private val viewModel by lazy {
        ViewModelProvider(this).get(DeckEditViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deck_edit,
            container,
            false
        )

        val args = DeckEditFragmentArgs.fromBundle(requireArguments())

        viewModel.loadCardDeck(args.deckId)
        // Observer
        viewModel.deck.observe(viewLifecycleOwner) {
            deck = it
            binding.deck = deck
            name = deck.name
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val deckNameTextWatcher = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                name = s.toString()
            }

        }

        binding.deckNameEdit.addTextChangedListener(deckNameTextWatcher)

        // Accept changes
        binding.acceptDeckEditButton.setOnClickListener {
            deck.name = name
            executor.execute {
                viewModel.getContext.updateDeck(deck)
            }
            // Go back to the deck list
            it.findNavController()
                .navigate(DeckEditFragmentDirections
                    .actionDeckEditFragmentToDeckListFragment())
        }

        // Discard changes
        binding.cancelDeckEditButton.setOnClickListener {
            if(deck.name == "") {
                executor.execute {
                    viewModel.getContext.deleteDeck(deck)
                }
            }
            // Go back to the deck list
            it.findNavController()
                .navigate(DeckEditFragmentDirections
                    .actionDeckEditFragmentToDeckListFragment())
        }
    }
}