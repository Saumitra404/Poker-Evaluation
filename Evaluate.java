import java.io.*;
import java.util.*;


// NOTE: THIS PROGRAM ASSUMES THE USER HAS ALREADY RUN "Generate.java" AND HAS
//   OUPUT FILE, "allHands.txt", IN THE SAME FOLDER
public class ConvertEvalToRank {

    public static void main(String[] args) {
        String inputFile = "allHands.txt";
        String outputFile = "rankedHands.txt";

        List<HandLine> handLines = new ArrayList<>();

        // 1) Read and parse input
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip empty

                // Example input line: "[KH, 9S, 7D, 3S, 2S]		11309070302"
                String[] parts = line.split("\\t+");
                if (parts.length < 2) {
                    continue;
                }

                String bracketed = parts[0].trim();  // e.g. "[KH, 9S, 7D, 3S, 2S]"
                String evalStr   = parts[1].trim();  // e.g. "11309070302"

                long evalValue;
                try {
                    evalValue = Long.parseLong(evalStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                Hand hand = parseBracketedHand(bracketed);

                handLines.add(new HandLine(bracketed, evalValue, hand));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 2) Sort hands from weakest to strongest
        Collections.sort(handLines, (hl1, hl2) -> hl1.hand.compareTo(hl2.hand));

        // 3) Assign dense ranks
        long prevEval = 0;
        int rank = 1; // increment by 1 with new evals
        for (int i = 0; i < handLines.size(); i++) {
            long thisEval = handLines.get(i).hand.evaluate();
            if (thisEval != prevEval) {
                rank++;
                prevEval = thisEval;
            }
            handLines.get(i).assignedRank = rank;
        }

        // 4) Write them out to the output file
        // For example: "[JS, 9S, 9C, 6H, 5H]		1449"
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            for (HandLine hl : handLines) {
                pw.println(hl.bracketed + "\t\t" + hl.assignedRank);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done! Output in " + outputFile);
    }

    /*
     * Parse a bracketed string like "[KH, 9S, 7D, 3S, 2S]" into a Hand.
     * Adjust to match your Card/Hand constructor or format if needed.
     */
    private static Hand parseBracketedHand(String bracketed) {
        // Remove '[' and ']'
        String noBrackets = bracketed.replace("[", "").replace("]", "").trim();
        // e.g. "KH, 9S, 7D, 3S, 2S"
        String[] cardTokens = noBrackets.split(",");
        List<Card> cards = new ArrayList<>(5);

        for (String token : cardTokens) {
            token = token.trim(); // e.g. "KH"
            Card c = parseCard(token);
            cards.add(c);
        }
        return new Hand(cards);
    }

    /**
     * Convert "KH" -> King of Hearts, "9S" -> 9 of Spades, etc.
     * Adapt suit/rank as your Card constructor expects.
     */
    private static Card parseCard(String token) {
        int rank;
        char rankChar = token.charAt(0);
        switch (rankChar) {
            case 'T': rank = 10; break;
            case 'J': rank = 11; break;
            case 'Q': rank = 12; break;
            case 'K': rank = 13; break;
            case 'A': rank = 14; break;
            default:  rank = Character.getNumericValue(rankChar); 
        }

        char suitChar = token.charAt(token.length() - 1);
        int suit;
        // I'm not incredibly familiar with switch/case, but here is a useful application I beleive.
        switch (suitChar) {
            case 'H': suit = 2; break; // hearts
            case 'D': suit = 1; break; // diamonds
            case 'C': suit = 0; break; // clubs
            case 'S': suit = 3; break; // spades
            default:  suit = -1;
        }

        return new Card(rank, suit);
    }

    /*
     *  A simple wrapper class to hold data from each input line.
     */
    private static class HandLine {
        String bracketed;   // e.g. "[KH, 9S, 7D, 3S, 2S]"
        long oldEval;       // e.g. 11309070302
        Hand hand;          // parsed Hand
        int assignedRank;

        HandLine(String bracketed, long oldEval, Hand hand) {
            this.bracketed = bracketed;
            this.oldEval = oldEval;
            this.hand = hand;
        }
    }
}
