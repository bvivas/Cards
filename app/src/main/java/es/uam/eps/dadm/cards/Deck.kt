package es.uam.eps.dadm.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "decks_table")
class Deck(
    @ColumnInfo(name = "deck_name")
    var name: String,
    @PrimaryKey
    var deckId: Long,
    @ColumnInfo(name = "deck_user_id")
    var userId: String
) {
    // Display the deck ID
    fun getDeckIdString(): String {
        return this.deckId.toString()
    }
}