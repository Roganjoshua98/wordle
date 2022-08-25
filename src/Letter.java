import java.util.ArrayList;
import java.util.List;

public class Letter {
    private final String name;
    private List<Integer> posList; // Positions that the letter appears in the answer
    private int buttonState;
    private int[] roundStates;
    private boolean isInGame;
    private boolean hasBeenUsed;

    public Letter(String name) {
        this.name = name;
        initialise();
    }

    public void initialise() {
        this.buttonState = Model.UNASSIGNED;
        this.hasBeenUsed = false;
        this.isInGame = false;
        this.posList = new ArrayList<>();
        this.roundStates = new int[]{Model.WRONG, Model.WRONG, Model.WRONG, Model.WRONG, Model.WRONG};
    }

    public void setInGame() {
        this.isInGame = true;
        this.roundStates = new int[]{Model.PARTIAL, Model.PARTIAL, Model.PARTIAL, Model.PARTIAL, Model.PARTIAL};
        for (int i : this.posList) this.roundStates[i] = Model.CORRECT;
    }

    public void addPos(int i) {
        this.posList.add(i);
    }

    public String getName() {
        return name;
    }

    public List<Integer> getPosList() {
        return posList;
    }

    public int getButtonState() {
        return buttonState;
    }

    public void setButtonState(int buttonState) {
        this.buttonState = buttonState;
    }

    public boolean hasBeenUsed() {
        return hasBeenUsed;
    }

    public void setHasBeenUsed(boolean hasBeenUsed) {
        this.hasBeenUsed = hasBeenUsed;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public int[] getRoundStates() { return roundStates; }
}
