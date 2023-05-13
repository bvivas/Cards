package es.uam.eps.dadm.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*


class StatisticsFragment : Fragment() {

    // ViewModel initialization
    private val statisticsViewModel by lazy {
        ViewModelProvider(this).get(StatisticsViewModel::class.java)
    }

    private var numOfDecks: Int = 0
    private var numOfCards: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        // Get the buttons
        val decksButton: Button = view.findViewById(R.id.decks_button_stats)
        val cardsButton: Button = view.findViewById(R.id.cards_button_stats)
        // Get the charts
        val pieChart: PieChart = view.findViewById(R.id.pie_chart_decks)
        val barChart: BarChart = view.findViewById(R.id.bar_chart_cards)

        val numDecks = view.findViewById<TextView>(R.id.decks_size)
        val numCards = view.findViewById<TextView>(R.id.cards_size)
        val numNotDueCards: TextView = view.findViewById(R.id.num_not_due_cards)

        statisticsViewModel.decks.observe(viewLifecycleOwner,
            Observer {
                setPieChart(it, pieChart)
            })

        // Display the decks sizes
        decksButton.setOnClickListener { b ->
            statisticsViewModel.decks.observe(viewLifecycleOwner,
                Observer {
                    setPieChart(it, pieChart)
                })

            // Change button color
            b.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_blue))
            cardsButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_light_grey))

            // Set visibility
            pieChart.visibility = View.VISIBLE
            barChart.visibility = View.INVISIBLE
            numNotDueCards.visibility = View.INVISIBLE
        }

        // Display cards for the next days
        cardsButton.setOnClickListener { b ->
            statisticsViewModel.notDueCards.observe(viewLifecycleOwner,
                Observer {
                    setBarChart(it, barChart)

                    numNotDueCards.text = "${context?.resources?.getString(R.string.num_not_due_cards_text)} ${it.toString()}"
                    numNotDueCards.visibility = View.VISIBLE
                })

            // Change button color
            b.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_blue))
            decksButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_light_grey))

            // Set visibility
            pieChart.visibility = View.INVISIBLE
            barChart.visibility = View.VISIBLE
        }

        statisticsViewModel.decksNum.observe(viewLifecycleOwner,
        Observer {
            numOfDecks = it.size
            numDecks.text = "${context?.resources?.getString(R.string.number_of_decks_size)} ${numOfDecks.toString()}"
        })

        statisticsViewModel.cardsNum.observe(viewLifecycleOwner,
        Observer {
            numOfCards = it.size
            numCards.text = "${context?.resources?.getString(R.string.number_of_cards_size)} ${numOfCards.toString()}"
        })

        return view
    }

    private fun setPieChart(listDecks: List<DeckWithCards>, pieChart: PieChart) {

        val entries: ArrayList<PieEntry> = ArrayList()

        // Set the data
        for(d in listDecks) {
            var i = 1
            entries.add(PieEntry(d.cards.size.toFloat(), d.deck.name))
            i += 1
        }

        val dataSet = PieDataSet(entries, "Decks")
        dataSet.setColors(
            ContextCompat.getColor(requireContext(), R.color.pie_red),
            ContextCompat.getColor(requireContext(), R.color.pie_aqua),
            ContextCompat.getColor(requireContext(), R.color.pie_yellow),
            ContextCompat.getColor(requireContext(), R.color.pie_blue),
            ContextCompat.getColor(requireContext(), R.color.pie_grey),
            ContextCompat.getColor(requireContext(), R.color.pie_orange),
            ContextCompat.getColor(requireContext(), R.color.pie_pink))
        val data = PieData(dataSet)
        pieChart.data = data

        // Set some features of the chart
        pieChart.holeRadius = 0f
        pieChart.isDrawHoleEnabled = false
        dataSet.setDrawValues(false)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.animateY(1000, Easing.EaseInOutQuad)

        // Refresh chart
        pieChart.invalidate()
    }

    private fun setBarChart(notNumDueCards: Int, barChart: BarChart) {

        val barEntries: ArrayList<BarEntry> = ArrayList()

        // Set the data
        barEntries.add(BarEntry(0f, notNumDueCards.toFloat()))
        val barDataSet = BarDataSet(barEntries, "Next cards")
        barDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.pie_red))
        barDataSet.setDrawValues(false)
        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f
        barChart.data = barData

        // Set visual features
        val description = Description()
        description.isEnabled = false
        barChart.description = description
        barChart.legend.isEnabled = false
        barChart.animateY(300)
        barChart.animateX(300)
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawBorders(false)
        barChart.setTouchEnabled(false)

        // X axis
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.setDrawAxisLine(false)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Y axis
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisLeft.setDrawAxisLine(false)
        barChart.axisLeft.axisMinimum = 0f

        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.setDrawAxisLine(false)
        barChart.axisRight.axisMinimum = 0f

        // Refresh chart
        barChart.invalidate()
    }
}