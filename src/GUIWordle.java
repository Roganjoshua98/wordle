import java.io.IOException;
import java.util.Scanner;

public class GUIWordle {

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
        if (model.isSpoilerFlag()) System.out.println("The answer is \"" + model.letterListToString(model.getAnswer()) + "\"");
        Controller controller = new Controller(model);
        View view = new View(model, controller);
    }
}
