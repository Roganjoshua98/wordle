import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final Model model;
    private View view;

    public Controller(Model model) {
        this.model = model;
    }

    public void initialise(View view) {
        this.view = view;
    }

    public int getNumGuesses() {
        return Model.NUMGUESSES;
    }

    public void newGame() {
        model.initialise();
        if (model.isSpoilerFlag()) System.out.println("The answer is \"" + model.letterListToString(model.getAnswer()) + "\"");
    }

    public List<Color> getColours() { // Get list of colours with indicies matching letter state values
        List<Color> colours = new ArrayList<>();
        colours.add(Color.lightGray); // Model.UNASSIGNED
        colours.add(Color.darkGray); // Model.WRONG
        colours.add(Color.yellow); //  Model.PARTIAL
        colours.add(Color.green); // Model.CORRECT
        return colours;
    }

    public void submitLetter(String text) { // Submit letter to model and call view to update label
        if (model.getCurrentGuess().size() < 5) {
            Letter letter = model.getLetter(text);
            model.submitLetter(letter);
            view.addLetter(letter);
        }
    }

    public void removeLetter() { // Remove letter in model and clear corresponding label
        if (model.getCurrentGuess().size() > 0) {
            model.removeLastLetter();
            view.removeLetter();
        }
    }

    public String getCurrentGuessAsString() {
        return model.letterListToString(model.getCurrentGuess());
    }

    public String getAnswerAsString() {
        return model.letterListToString(model.getAnswer());
    }

    public boolean isValidWord(String word) {
        return model.isValidWord(word);
    }

    public void submitGuess() {
        if (model.getCurrentGuess().size() == 5 && model.isCurrentGuessValid()) {
            model.submitGuess();
        }
    }

    public List<List<Letter>> getSubmittedGuesses() {
        return model.getSubmittedGuesses();
    }

    public int getTurnCount() {
        return model.getTurnCount();
    }

    public boolean getWinFlag() {
        return model.getWinFlag();
    }

    public boolean getGameFlag() {
        return model.getGameFlag();
    }

    public boolean getStrictFlag() { return model.getStrictFlag(); }

    public Letter getLetter(String l) {
        return model.getLetter(l);
    }
}
