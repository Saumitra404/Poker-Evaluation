import java.io.*;
import java.util.*;

import Card.java
import Hand.java;
import Deck.java


// This class generates every possible hand and their evaluation numbers. Suprisingly not time intensive.
public class genHandsTXT {
    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("allHands.txt"))) {
            Deck deck = new Deck();
            List<Card> cards = deck.getList();
            for (int i = 0; i < cards.size() - 4; i++) {
                for (int j = i + 1; j < cards.size() - 3; j++) {
                    for (int k = j + 1; k < cards.size() - 2; k++) {
                        for (int l = k + 1; l < cards.size() - 1; l++) {
                            for (int m = l + 1; m < cards.size(); m++) {
                                List<Card> hand = Arrays.asList(cards.get(i), cards.get(j), cards.get(k), cards.get(l), cards.get(m));
                                Hand pokerHand = new Hand(hand);
                                writer.write(formatHand(hand) + "\t\t" + pokerHand.evaluate() + "\n");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatHand(List<Card> hand) {
        StringBuilder sb = new StringBuilder("[");
        for (Card card : hand) {
            sb.append(getRankSymbol(card.getNumber())).append(getSuitSymbol(card.getSuite())).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    private static String getRankSymbol(int num) {
        if (num == 14) { return "A";}
        else if (num == 13) {return "K";}
        else if (num == 12) {return "Q";}
        else if (num == 11) {return "J";}
        else if (num == 10) {return "T";}
        else {return String.valueOf(num);}
    }

    private static char getSuitSymbol(int suit) {
        char[] suits = {'S', 'C', 'D', 'H'};
        return suits[suit];
    }
}
