package es.uam.eps.dadm.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import es.uam.eps.dadm.cards.databinding.FragmentStudyBinding
import timber.log.Timber


class StudyFragment: Fragment() {

    // Reference to cards
    private var reference = FirebaseDatabase
        .getInstance()
        .getReference("cards")

    // ViewModel initialization
    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(this).get(StudyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("MainActivity onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentStudyBinding>(
            inflater,
            R.layout.fragment_study,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val listener = View.OnClickListener { v ->
            // Assign quality
            val quality = when (v?.id) {
                R.id.difficult_button -> 0
                R.id.doubt_button -> 3
                R.id.easy_button -> 5
                else -> throw Exception("Incorrect quality value")
            }

            // Update with the quality
            viewModel.update(quality)
            // Update the card in Firebase
            reference.child(viewModel.card!!.id).setValue(viewModel.card)

            if (viewModel.card == null) {
                Toast.makeText(requireActivity(), R.string.no_more_cards, Toast.LENGTH_LONG).show()
            }

            binding.invalidateAll()
        }

        // Observer
        viewModel.dueCard.observe(viewLifecycleOwner, Observer {
            viewModel.card = it
            binding.invalidateAll()
        })
        // Listeners
        binding.apply {
            answerButton.setOnClickListener {
                viewModel?.card?.answered = true
                binding.invalidateAll()
            }
            binding.easyButton.setOnClickListener(listener)
            binding.doubtButton.setOnClickListener(listener)
            binding.difficultButton.setOnClickListener(listener)
        }

        // Enable and disable the board view
        if(!SettingsActivity.getBoard(viewModel.context)) {
            binding.board?.visibility = View.GONE
        }

        return binding.root
    }
}