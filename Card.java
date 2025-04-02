/*
 * Simple class to represent a standard playing card, jokers not included.
 */
public class Card {
   private int rank;
   private int suite;

  /*
   * Takes a rank and suite integer
   *   - rank from 2 -> 14 (Two, Three... King, Ace)
   *   - suite from 0 -> 3 (Spades, Clovers, Diamonds, Hearts)
   */
   public Card(int rank, int suite) {
      if (rank >= 2 && rank < 14) {
         if (suite >= 0 && suite <= 3) {
            this.rank = rank;
            this.suite = suite;

         } else {
            throw new IllegalArgumentException("Suit number out of bounds 0 => 3");
         }
      } else {
         throw new IllegalArgumentException("Rank number out of bounds 2 => 14");
      }
   }

  // Fetches rank
   public int getRank() {
      return this.rank;
   }

  // Fetches Suite
   public int getSuite() {
      return this.suite;
   }

  // Boolean value (Diamonds & Hearts -> true, Spades & Clovers -> false
   public boolean isRed() {
      return suite > 1;
   }

  // Boolean value (if the card is a face card, ie. Jack, Queen, King, Ace)
   public boolean isFace() {
      return rank > 10;
   }
}
