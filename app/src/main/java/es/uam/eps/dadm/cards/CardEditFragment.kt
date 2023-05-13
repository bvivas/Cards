package es.uam.eps.dadm.cards

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.databinding.FragmentCardEditBinding
import java.util.concurrent.Executors


class CardEditFragment : Fragment() {

    lateinit var binding: FragmentCardEditBinding
    private val executor = Executors.newSingleThreadExecutor()

    lateinit var card: Card
    lateinit var question: String
    lateinit var answer: String

    var deckId: Long = 0L

    // Initialize ViewModel
    private val viewModel by lazy {
        ViewModelProvider(this).get(CardEditViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_card_edit,
            container,
            false
        )
        val args = CardEditFragmentArgs.fromBundle(requireArguments())

        deckId = args.deckId
        viewModel.loadCardId(args.cardId)
        viewModel.card.observe(viewLifecycleOwner) {
            card = it
            binding.card = card
            question = card.question
            answer = card.answer
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val questionTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.question = s.toString()
            }
        }

        val answerTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.answer = s.toString()
            }
        }

        binding.questionEditText.addTextChangedListener(questionTextWatcher)
        binding.answerEditText.addTextChangedListener(answerTextWatcher)

        // Accept changes
        binding.acceptButton.setOnClickListener {
            executor.execute {
                viewModel.getContext.updateCard(card)
            }
            // Go back to the list of cards
            it.findNavController()
                .navigate(CardEditFragmentDirections
                    .actionCardEditFragmentToCardListFragment(deckId))
        }

        // Discard changes
        binding.cancelButton.setOnClickListener {
            card.question = question
            card.answer = answer
            if(card.question == "" || card.answer == "")
                executor.execute {
                    viewModel.getContext.deleteCard(card)
                }
            // Go back to the list of cards
            it.findNavController()
                .navigate(CardEditFragmentDirections
                    .actionCardEditFragmentToCardListFragment(deckId))
        }
    }
}