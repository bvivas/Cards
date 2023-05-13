package es.uam.eps.dadm.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Double.max
import java.time.*
import java.util.*
import kotlin.math.*


@Entity(tableName = "cards_table")
open class Card(
    @ColumnInfo(name = "card_question")
    var question: String,
    @ColumnInfo(name = "card_answer")
    var answer: String,
    var deckId: Long = 0,
    @ColumnInfo(name = "card_date")
    var date: String = LocalDateTime.now().toString(),
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id")
    var userId: String
) {
    var quality: Int = -1
    var repetitions: Int = 0
    var interval: Long = 1L
    var nextPracticeDate = date
    var easiness: Double = 2.5

    var answered = false

    constructor() : this(
        "question",
        "answer",
        0,
        LocalDateTime.now().toString(),
        UUID.randomUUID().toString(),
        Firebase.auth.currentUser!!.uid
    )


    fun update(currentDate: LocalDateTime) {
        // Update easiness
        easiness = max(1.3, easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))

        // Update repetitions
        if(quality < 3)
            repetitions = 0
        else
            repetitions += 1

        // Update interval
        interval = if(repetitions <= 1)
            1
        else if(repetitions == 2)
            6
        else
            (interval * easiness).roundToLong()

        // Update date
        nextPracticeDate = currentDate.plusDays(interval).toString()
    }


    fun isDue(date: LocalDateTime): Boolean {
        val current = date.dayOfYear
        val next = LocalDateTime.parse(nextPracticeDate).dayOfYear
        return current >= next
    }

    override fun toString() = "card | $question | $answer | $date | $id | $easiness | $repetitions | $interval | $nextPracticeDate"


    // Display relevant information about a card
    fun displayEasiness(): String {
        return "Easiness: ${"%.2f".format(this.easiness)}"
    }

    fun displayRepetitions(): String {
        return "Repetitions: ${this.repetitions}"
    }

    fun displayInterval(): String {
        return "Interval: ${this.interval}"
    }

    fun displayNextDate(): String {
        return "Next date: ${this.nextPracticeDate.substring(0, 10)}"
    }
}