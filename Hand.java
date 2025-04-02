import java.util.*;

// This class represents a hand of cards, ie. 5 distinct cards from a deck, and can generate an evaluation
//    number for each hand which can be used to easily compare it to other hands.
public class Hand implements Comparable<Hand> {
    List<Card> hand = new ArrayList<>();

    Hand(List<Card> hand) {
        if (hand.size() != 5) {
            throw new IllegalArgumentException("Wrong number of cards");
        }
        this.hand = hand;
    }

    // Hand evaluation format: ABCDEFGHIJK as a long
    // A  -> Hand Value 1-9 (e.g., High Card = 1, Full House = 7, etc.)
    // BC -> Highest rank in combination (e.g., 14 for Ace, 09 for Nine, etc.)
    // DE -> Second highest in combination or kicker
    // FG -> Next highest kicker
    // HI -> Next highest kicker
    // JK -> Lowest kicker
    public long evaluate() {
        Map<Integer, Integer> rankCount = new TreeMap<>();
        Set<Integer> suits = new HashSet<>();

        for (Card card : hand) {
            int num = card.getNumber();
            if (!rankCount.containsKey(num)) {
                rankCount.put(card.getNumber(), 0);
            }
            rankCount.put(num, rankCount.get(num) + 1);
            suits.add(card.getSuite());
        }

        List<Integer> sortedRanks = new ArrayList<>(rankCount.keySet());
        Collections.sort(sortedRanks, Collections.reverseOrder());

        boolean isFlush = suits.size() == 1;
        boolean isStraight = sortedRanks.size() == 5 && (sortedRanks.get(0) - sortedRanks.get(4) == 4);
        // Check for the special A-2-3-4-5
        boolean isAceLowStraight = false;
        if (sortedRanks.equals(Arrays.asList(14, 5, 4, 3, 2))) {
            isAceLowStraight = true;
        }
        List<Integer> combos = new ArrayList<>();

        if (isFlush && isStraight) { // Straight Flush
            return 90000000000L + computeValue(combos, sortedRanks);
        } else if (isFlush && isAceLowStraight) {
            return 90000000000L;
        } else if (rankCount.containsValue(4)) { // Four of a Kind
            combos.add(getHighestKeyByValue(rankCount, 4));
            return 80000000000L + computeValue(combos, sortedRanks);
        } else if (rankCount.containsValue(3) && rankCount.containsValue(2)) { // Full House
            combos.add(getHighestKeyByValue(rankCount, 3));
            combos.add(getHighestKeyByValue(rankCount, 2));
            return 70000000000L + computeValue(combos, sortedRanks);
        } else if (isFlush) { // Flush
            return 60000000000L + computeValue(combos, sortedRanks);
        } else if (isStraight) { // Straight
            return 50000000000L + computeValue(combos, sortedRanks);
        } else if (isAceLowStraight) {
            return 50000000000L;
        } else if (rankCount.containsValue(3)) { // Three of a Kind
            combos.add(getHighestKeyByValue(rankCount, 3));
            return 40000000000L + computeValue(combos, sortedRanks);
        } else if (Collections.frequency(rankCount.values(), 2) == 2) { // Two Pair
            combos.add(getHighestKeyByValue(rankCount, 2));
            combos.add(getNextKeyByValue(rankCount, 2));
            return 30000000000L + computeValue(combos, sortedRanks);
        } else if (rankCount.containsValue(2)) { // One Pair
            combos.add(getHighestKeyByValue(rankCount, 2));
            return 20000000000L + computeValue(combos, sortedRanks);
        }
        return 10000000000L + computeValue(combos, sortedRanks); // High Card
    }

    // Helper method to calculate hand value based on rank and card values
    private long computeValue(List<Integer> combos, List<Integer> cards) {
        long output = 0;
        long multiplier = 100000000L;
        for (int card : combos) {
            output += card * multiplier;
            multiplier /= 100;
        }
        for (int card : cards) {
            if (!combos.contains(card)) {
                output += card * multiplier;
                multiplier /= 100;
            }
        }
        return output;
    }

    /*
    * Returns the second-highest-ranked key that has the given frequency
    * (i.e., skip the first one we see in descending order)
    * If there is not a second one, returns 0.
    */
    private int getNextKeyByValue(Map<Integer, Integer> map, int target) {
        TreeMap<Integer, Integer> tree = (TreeMap<Integer, Integer>) map;
        boolean foundFirst = false;

        for (int rank : tree.descendingKeySet()) {
            if (map.get(rank) == target) {
                if (foundFirst) {
                    return rank;
                }
                foundFirst = true;
            }
        }
        return 0;
    }

    /*
     * Returns the highest-ranked key that has the given frequency.
     * If none has that frequency, returns 0.
     */
    private int getHighestKeyByValue(Map<Integer, Integer> map, int target) {
        // Casting here is rather ugly, but it's the easiest way to get the descending order
        TreeMap<Integer, Integer> tree = (TreeMap<Integer, Integer>) map;

        for (int rank : tree.descendingKeySet()) {
            if (map.get(rank) == target) {
                return rank;
            }
        }
        return 0;
}

    // Compare hands by their evaluated values
    @Override
    public int compareTo(Hand other) {
        return Long.compare(this.evaluate(), other.evaluate());
    }
}
