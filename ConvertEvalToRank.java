import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ConvertEvalToRank {

    public static void main(String[] args) {
        // Adjust these filenames or paths as needed
        String inputFile = "allHands.txt";
        String outputFile = "rankedHands.txt";

        // Will hold each line's data: bracketed string, old eval, the parsed Hand
        List<HandLine> handLines = new ArrayList<>();

        // 1) Read and parse the input file
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip empty

                // Example input line: "[KH, 9S, 7D, 3S, 2S]		11309070302"
                // Split around tabs (or multiple spaces).
                // If your file uses a specific delimiter, adapt this.
                String[] parts = line.split("\\t+");
                if (parts.length < 2) {
                    // If we can’t find the bracketed portion and the eval
                    continue;
                }

                String bracketed = parts[0].trim();  // e.g. "[KH, 9S, 7D, 3S, 2S]"
                String evalStr   = parts[1].trim();  // e.g. "11309070302"

                long evalValue;
                try {
                    evalValue = Long.parseLong(evalStr);
                } catch (NumberFormatException e) {
                    // Not a valid number => skip or handle error
                    continue;
                }

                // Convert the bracketed portion into a Hand
                Hand hand = parseBracketedHand(bracketed);

                // Store
                handLines.add(new HandLine(bracketed, evalValue, hand));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 2) Sort all hands from weakest to strongest using Hand.compareTo(...)
        Collections.sort(handLines, (hl1, hl2) -> hl1.hand.compareTo(hl2.hand));

        // 3) Assign dense ranks
        //    - All hands that tie in .evaluate() share the same rank
        //    - The next distinct .evaluate() is rank+1
        long prevEval = 0;
        int rank = 1; // We'll increment to 1 at the first new evaluation
        for (int i = 0; i < handLines.size(); i++) {
            long thisEval = handLines.get(i).hand.evaluate();
            if (thisEval != prevEval) {
                // Found a new distinct evaluation => increment rank
                rank++;
                prevEval = thisEval;
            }
            // Assign that rank to the hand line
            handLines.get(i).assignedRank = rank;
        }

        // 4) Write them out to the output file
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
            for (HandLine hl : handLines) {
                // For example: "[JS, 9S, 9C, 6H, 5H]		1449"
                pw.println(hl.bracketed + "\t\t" + hl.assignedRank);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done! Output in " + outputFile);
    }

    /**
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
        // e.g. "KH" => rankChar='K' => 13, suitChar='H' => hearts=2
        // T->10, J->11, Q->12, K->13, A->14
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
        switch (suitChar) {
            case 'H': suit = 2; break; // hearts
            case 'D': suit = 1; break; // diamonds
            case 'C': suit = 0; break; // clubs
            case 'S': suit = 3; break; // spades
            default:  suit = -1;       // unknown
        }

        return new Card(rank, suit);
    }

    /**
     * Simple wrapper class to hold data from each input line.
     */
    private static class HandLine {
        String bracketed;   // e.g. "[KH, 9S, 7D, 3S, 2S]"
        long oldEval;       // e.g. 11309070302
        Hand hand;          // parsed Hand
        int assignedRank;   // the final rank we’ll compute

        HandLine(String bracketed, long oldEval, Hand hand) {
            this.bracketed = bracketed;
            this.oldEval = oldEval;
            this.hand = hand;
        }
    }
}