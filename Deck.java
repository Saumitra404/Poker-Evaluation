import java.util.*;

// This class represents a deck of 52 cards, using Card objects (see card.java)
public class Deck {
   private List<Card> deck = new ArrayList();

   // Initializes a new deck with all 52 cards
   Deck() {
      this.initializeDeck();
   }

   private void initializeDeck() {
      for(int i = 0; i < 4; i++) {
         for(int j = 2; j <= 14; j++) {
            this.deck.add(new Card(j, i));
         }
      }

      this.sortDeck();
   }
  
    // Sorts the deck in descending order by rank (Ace to 2), suit (Spade to Heart)
    private void sortDeck() {
        deck.sort((a, b) -> {
            if (a.getRank() != b.getRank()) {
                return Integer.compare(b.getRank(), a.getRank());
            }
            return Integer.compare(a.getSuite(), b.getSuite());
        });
    }

   public void shuffle() {
      Collections.shuffle(this.deck, new Random());
   }

   public Card draw() {
      return (Card)this.deck.remove(0);
   }

   public Card peek() {
      return (Card)this.deck.get(0);
   }

   public int size() {
      return this.deck.size();
   }

   public List<Card> getList() {
      return new ArrayList(this.deck);
   }
}
