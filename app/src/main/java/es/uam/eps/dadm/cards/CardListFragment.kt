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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.databinding.FragmentCardListBinding
import java.util.concurrent.Executors


class CardListFragment: Fragment() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var adapter: CardAdapter

    private lateinit var intent: Intent
    private lateinit var userInfo: UserInfo

    // Initialize ViewModel
    private val cardListViewModel by lazy {
        ViewModelProvider(this).get(CardListViewModel::class.java)
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
        val binding = DataBindingUtil.inflate<FragmentCardListBinding>(
            inflater,
            R.layout.fragment_card_list,
            container,
            false
        )

        SettingsActivity.setLoggedIn(requireContext(), true)

        val args = CardListFragmentArgs.fromBundle(requireArguments())

        adapter = CardAdapter()
        adapter.data = emptyList()
        adapter.deckData = mutableListOf(args.deckId)
        binding.cardListRecyclerView.adapter = adapter

        // Observer
        cardListViewModel.getContext.getCardByDeck(args.deckId, Firebase.auth.currentUser!!.uid).observe(
            viewLifecycleOwner,
            Observer {
                adapter.data = it
                adapter.notifyDataSetChanged()
            }
        )

        // Add new card
        binding.newCardFab.setOnClickListener {
            val card = Card("", "", args.deckId, userId = Firebase.auth.currentUser!!.uid)

            executor.execute {
                cardListViewModel.getContext.addCard(card)
            }

            // Go to edit card to finish the process
            it.findNavController()
                .navigate(CardListFragmentDirections
                    .actionCardListFragmentToCardEditFragment(card.id, args.deckId))
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