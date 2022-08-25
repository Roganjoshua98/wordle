import java.io.IOException;
import java.util.Scanner;

public class CLIWordle {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int randomMode, spoilerMode, strictMode;

        // Choose various game modes to play
        do {
            System.out.println("Press 1 for a random word or 2 for a fixed word");
            randomMode = scanner.nextInt();
        } while (randomMode != 1 && randomMode != 2);
        do {
            System.out.println("Press 1 for spoiler mode or 2 for secret mode");
            spoilerMode = scanner.nextInt();
        } while (spoilerMode != 1 && spoilerMode != 2);
        do {
            System.out.println("Press 1 for strict mode or 2 for loose mode");
            strictMode = scanner.nextInt();
        } while (strictMode != 1 && strictMode != 2);
        Model model = new Model(randomMode, spoilerMode, strictMode);

        gameLoop(model, scanner);
    }

    public static void gameLoop(Model model, Scanner scanner) {
        String guess;
        int i;
        if (model.isSpoilerFlag()) System.out.println("The answer is " + model.letterListToString(model.getAnswer()));
        while (model.getGameFlag()) {
            // Enter guess
            System.out.println("Enter guess number " + (model.getTurnCount()+1));
            guess = scanner.next();
            while (!model.isValidWord(guess)) {
                System.out.println("Not a valid guess. Try again!");
                guess = scanner.next();
            }

            model.submitWord(guess);
            String indicators = model.getIndicators();
            model.submitGuess();
            System.out.println("");

            // Print out results
            if (model.getWinFlag()) {
                System.out.println("Correct! You won!");
                return;
            }
            else {
                System.out.println(indicators);
                System.out.println(guess);
                System.out.println("");
                System.out.println("Correct letters: " + model.letterListToStringList(model.getCorrectLetters()));
                System.out.println("Partial letters: " + model.letterListToStringList(model.getPartialLetters()));
                System.out.println("Wrong letters: " + model.letterListToStringList(model.getWrongLetters()));
                System.out.println("Unused letters: " + model.letterListToStringList(model.getUnusedLetters()));
                System.out.println("");
            }
        }
        System.out.println("No more guesses allowed. Better luck next time!");
    }
}
