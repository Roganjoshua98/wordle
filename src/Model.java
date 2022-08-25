import java.io.*;
import java.util.*;

public class Model extends Observable {
    // Constants
    private static final String ANSWER_FILE = "src/assets/common.txt";
    private static final String WORDS_FILE = "src/assets/words.txt";
    public static final int UNASSIGNED = 0;
    public static final int WRONG = 1;
    public static final int PARTIAL = 2;
    public static final int CORRECT = 3;
    public static final int NUMGUESSES = 6;
    public static String FIXED_ANSWER = "undid";
    // Flags
    private final boolean spoilerFlag; // Sets whether the game will reveal the answer to the user
    private final boolean randomFlag; // Sets whether the game generates an answer or is provided an answer by user
    private final boolean strictFlag; // Sets whether the user can input any five-letter words or not
    private boolean gameFlag; // When true, game is still in play
    private boolean winFlag; // When true, player has won the game
    // Game attributes
    private final List<String> answers; // List of valid answers
    private final List<String> words; // List of valid guesses
    private HashMap<String, Letter> alphabet; // List of Letter objects in the game
    private int turnCount; // Which turn the game is on
    private List<Letter> answer; // The answer the user needs to guess to win
    private List<Letter> currentGuess; // Most recent guess submitted by user
    private List<List<Letter>> submittedGuesses; // List of all submitted guesses
    public List<Letter> unusedLetters; // List of unused letters
    public List<Letter> wrongLetters; // List of incorrectly guessed letters
    public List<Letter> partialLetters; // List of letters that are in the answer but wrong position
    public List<Letter> correctLetters; // List of correctly guessed letters

    public Model(int randomMode, int spoilerMode, int strictMode) throws IOException {
        // Initialise answer list
        BufferedReader ar = new BufferedReader(new FileReader(ANSWER_FILE));
        this.answers = new ArrayList<>();
        String line;
        while((line = ar.readLine()) != null) { this.answers.add(line); }
        ar.close();
        // Initialise words list
        BufferedReader wr = new BufferedReader(new FileReader(WORDS_FILE));
        this.words = new ArrayList<>();
        while((line = wr.readLine()) != null) { this.words.add(line); }
        this.words.addAll(this.answers);
        wr.close();
        // Set attributes
        this.randomFlag = randomMode == 1;
        this.spoilerFlag = spoilerMode == 1;
        this.strictFlag = strictMode == 1;
        initialise();
    }

    /**
     * Initialises the game attributes
     * @pre. Words and Answers have been successfully initialised
     * @post. this.answer is a five-letter Letter list
     */
    public void initialise() {
        assert !this.words.isEmpty() && !this.answers.isEmpty();
        this.winFlag = false;
        this.gameFlag = true;
        if (this.turnCount > 0) resetAlphabet();
        else this.alphabet = createAlphabet();
        this.unusedLetters = new ArrayList<>(this.alphabet.values());
        this.submittedGuesses = new ArrayList<>();
        this.wrongLetters = new ArrayList<>();
        this.partialLetters = new ArrayList<>();
        this.correctLetters = new ArrayList<>();
        this.currentGuess = new ArrayList<>();
        this.turnCount = 0;
        this.answer = generateAnswer();
        int i = 0;
        for (Letter l : this.answer) { // Update the letters in alphabet
            assert l != null;
            l.addPos(i);
            l.setInGame();
            i++;
        }
        assert this.answer.size() == 5;
    }


    private HashMap<String, Letter> createAlphabet() {
        String[] letters = "abcdefghijklmnopqrstuvwxyz".split("");
        HashMap<String, Letter> alphabet = new HashMap<>();
        for (String c : letters) alphabet.put(c, new Letter(c));
        return alphabet;
    }

    private void resetAlphabet() {
        String[] letters = "abcdefghijklmnopqrstuvwxyz".split("");
        for (String c : letters) {
            Letter l = getLetter(c);
            l.initialise();
        }
    }

    private List<Letter> generateAnswer() {
        String a;
        List<Letter> answer = new ArrayList<>();

        // If random mode is on, generate the answer by picking a random word from answer list
        if (this.randomFlag) {
            Random rand = new Random();
            a = this.answers.get(rand.nextInt(this.answers.size()));
            String[] sa = a.split("");
            for (String s : sa) answer.add(this.alphabet.get(s));
        }
        // Else, set the fixed word, check it's valid, and then set that as the answer
        else {
            a = FIXED_ANSWER;
            String[] sa = a.split("");
            for (String s : sa) answer.add(this.alphabet.get(s));
        }
        assert answer.size() == 5; // Answer was initialised with a 5-letter word
        return answer;
    }

    /**
     * Get letter from alphabet
     * @pre. l is a single alphabetic letter
     * @post. None
     */
    public Letter getLetter(String l) {
        l = l.toLowerCase();
        assert l.length() == 1; // Key is length of 1
        assert l.matches("[a-z]"); // Key is an alphabetic value
        return this.alphabet.get(l);
    }

    /**
     * Returns a boolean corresponding to if currentGuess is valid
     * @pre. None
     * @post. None
     */
    public boolean isCurrentGuessValid() {
        return isValidWord(String.join("", letterListToStringList(this.currentGuess)));
    }

    /**
     * Checks input guess to see if it is valid
     * @pre. this.words contains all words from WORDS_FILE, this.strictFlag is set
     * @post. None
     */
    public boolean isValidWord(String guess) {
        if (!guess.matches("[a-z]+")) {
            System.out.println("Word is not alphabetic!");
            return false;
        }
        if (guess.length() != 5) {
            System.out.println("Word is not five characters!");
            return false;
        }
        if (this.strictFlag) if (!this.words.contains(guess)) { // If strictFlag is false, no need to check guess list
            System.out.println("Word is not in the guess list");
            return false;
        }
        return true;
    }

    private boolean isCorrectGuess() {
        for (int i = 0; i < 5; i++) {
            if (this.currentGuess.get(i) != this.answer.get(i))
                return false;
        }
        return true;
    }

    /**
     * Appends an input Letter to currentGuess
     * @pre. letter is in the alphabet, this.currentGuess is less than size 5
     * @post. this.currentGuess contains at least one instance of letter
     */
    public void submitLetter(Letter letter) {
        assert alphabet.containsValue(letter);
        assert this.currentGuess.size() < 5;
        this.currentGuess.add(letter);
        assert this.currentGuess.contains(letter);
    }

    /**
     * Removes the last Letter from currentGuess
     * @pre. this.currentGuess is not size 0
     * @post. this.currentGuess won't be a complete word
     */
    public void removeLastLetter() {
        assert this.currentGuess.size() > 0;
        this.currentGuess.remove(this.currentGuess.size() - 1);
        assert this.currentGuess.size() < 5;
    }

    /**
     * Submits a five-letter word guess one letter at a time
     * @pre. word is a five-letter string
     * @post. this.currentGuess is a list containing five Letter elements
     */
    public void submitWord(String word) {
        assert word.length() == 5;
        clearGuess();
        String[] w = word.split("");
        for (String l : w) submitLetter(this.alphabet.get(l));
        assert this.currentGuess.size() == 5;
    }

    /**
     * Submits currentGuess as final guess for the turn
     * @pre. this.currentGuess is a valid five-letter guess,
     * @post. this.submittedGuesses has at least one element
     */
    public void submitGuess() {
        assert isCurrentGuessValid();
        this.submittedGuesses.add(this.currentGuess);
        updateAlphabet();
        this.turnCount++;
        this.winFlag = isCorrectGuess();
        if (this.turnCount >= NUMGUESSES) this.gameFlag = false;
        else if (this.winFlag) this.gameFlag = false;
        setChanged();
        notifyObservers();
        clearGuess();
        assert this.submittedGuesses.size() > 0;
    }

    private void updateAlphabet() {
        List<Letter> word = this.currentGuess;
        int i = 0;
        for (Letter l : word) {
            this.unusedLetters.remove(l);
            setButtonState(l,i);
            switch (l.getButtonState()) {
                case WRONG:
                    if (!this.wrongLetters.contains(l)) this.wrongLetters.add(l);
                    break;
                case PARTIAL:
                    if (!this.partialLetters.contains(l)) this.partialLetters.add(l);
                    break;
                case CORRECT:
                    if (!this.correctLetters.contains(l)) {
                        this.partialLetters.remove(l);
                        this.correctLetters.add(l);
                    } break;
                default:
                    this.unusedLetters.add(l);
                    break;
            }
            l.setHasBeenUsed(true);
            i++;
        }
    }

    private void setButtonState(Letter letter, int i) {
        if (!letter.hasBeenUsed()) {
            if (letter.isInGame()) {
                if (letter.getPosList().contains(i)) letter.setButtonState(CORRECT);
                else letter.setButtonState(PARTIAL);
            }
            else letter.setButtonState(WRONG);
        }
        else if (letter.getButtonState() == PARTIAL) {
            if (letter.getPosList().contains(i)) letter.setButtonState(CORRECT);
        }
    }

    /**
     * Create a string indicating whether each guess letter is correct, partial, or wrong
     * @pre. this.currentGuess is a valid five-letter word
     * @post. indicators is the same length as this.currentGuess
     */
    public String getIndicators() {
        assert isCurrentGuessValid();
        StringBuilder indicators = new StringBuilder();
        for (int i = 0; i < this.currentGuess.size(); i++) {
            Letter l = this.currentGuess.get(i);
            if (l.getRoundStates()[i] == CORRECT) indicators.append("o");
            else if (l.getRoundStates()[i] == PARTIAL) indicators.append("~");
            else if (l.getRoundStates()[i] == WRONG) indicators.append("x");
        }
        assert indicators.length() == this.currentGuess.size();
        return indicators.toString();
    }

    /**
     * Converts a List<Letter> to a List<String>
     * @pre. None
     * @post. None
     */
    public List<String> letterListToStringList(List<Letter> letters) {
        List<String> stringList = new ArrayList<>();
        for (Letter l : letters) stringList.add(l.getName());
        return stringList;
    }

    /**
     * Converts a List<Letter> to a String
     * @pre. None
     * @post. None
     */
    public String letterListToString(List<Letter> letters) {
        return String.join("",letterListToStringList(letters));
    }

    /**
     * Converts a String to a List<Letter>
     * @pre. words contains only characters in the alphabet
     * @post. None
     */
    public List<Letter> stringToLetterList(String word) {
        assert word.matches("[a-zA-Z]+");
        word = word.toLowerCase();
        List<Letter> letters = new ArrayList<>();
        for (char c : word.toCharArray()) letters.add(this.alphabet.get(String.valueOf(c)));
        return letters;
    }

    // Getters and Setters
    public List<String> getAnswers() {
        return answers;
    }
    public List<String> getWords() {
        return words;
    }
    public List<Letter> getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        if (isValidWord(answer))
            this.answer = stringToLetterList(answer);
    }
    public void clearGuess() {
        this.currentGuess = new ArrayList<>();
    }
    public int getTurnCount() {
        return turnCount;
    }
    public boolean isSpoilerFlag() {
        return spoilerFlag;
    }
    public boolean isRandomFlag() {
        return randomFlag;
    }
    public List<Letter> getCurrentGuess() {
        return currentGuess;
    }
    public List<Letter> getUnusedLetters() {
        return unusedLetters;
    }
    public List<Letter> getWrongLetters() {
        return wrongLetters;
    }
    public List<Letter> getPartialLetters() {
        return partialLetters;
    }
    public List<Letter> getCorrectLetters() {
        return correctLetters;
    }
    public boolean getWinFlag() { return winFlag; }
    public boolean getGameFlag() { return gameFlag; }
    public boolean getStrictFlag() { return strictFlag; }
    public List<List<Letter>> getSubmittedGuesses() { return submittedGuesses; }
}
