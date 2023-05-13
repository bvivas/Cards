package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.*
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.Deck
import es.uam.eps.dadm.cards.DeckWithCards


@Dao
interface CardDao {

    @Insert
    fun addCard(card: Card)

    @Update
    fun updateCard(card: Card)

    @Delete
    fun deleteCard(card: Card)

    @Query("SELECT * FROM cards_table WHERE user_id = :userId")
    fun getCards(userId: String): LiveData<List<Card>>

    @Query("SELECT * FROM cards_table WHERE id = :id")
    fun getCard(id: String): LiveData<Card?>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDeck(deck: Deck)

    @Update
    fun updateDeck(deck: Deck)

    @Delete
    fun deleteDeck(deck: Deck)

    @Query("DELETE FROM decks_table WHERE deck_name = :name and deck_user_id = :userId")
    fun deleteDeckByName(name: String, userId: String)

    @Query("SELECT * FROM decks_table WHERE deck_user_id = :userId")
    fun getDecks(userId: String): LiveData<List<Deck>>

    @Query("SELECT * FROM decks_table WHERE deckId = :id and deck_user_id = :userId")
    fun getDeck(id: Long, userId: String): LiveData<Deck?>

    @Query("SELECT deckId + 1 FROM decks_table ORDER BY deckId DESC LIMIT 1")
    fun getHighestDeckId(): LiveData<Long?>


    @Transaction
    @Query("SELECT * FROM decks_table WHERE deck_user_id = :userId")
    fun getDecksWithCards(userId: String): LiveData<List<DeckWithCards>>

    @Query("SELECT * FROM cards_table WHERE deckId = :id and user_id = :userId")
    fun getCardByDeck(id: Long, userId: String): LiveData<List<Card>>

    @Query("DELETE FROM cards_table WHERE deckId = :deckId and user_id = :userId")
    fun deleteAllCardsByDeck(deckId: Long, userId: String)

    @Query("DELETE FROM cards_table WHERE cards_table.card_question = :name and cards_table.deckId = :idDeck and user_id = :userId")
    fun deleteCardByDeck(name: String, idDeck: Long, userId: String)
}