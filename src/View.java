import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class View implements Observer {
    private final Model model;
    private final Controller controller;
    private JFrame frame;
    private List<JButton> keyboard;
    private List<List<JLabel>> guesses;
    private JLabel errorMessage;
    private JLabel answerMessage;
    private JButton newGameBtn;
    private int head;


    public View(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        List<Color> colours = controller.getColours();
        model.addObserver(this);
        this.frame = initialise();
        this.controller.initialise(this);
    }

    public JFrame initialise() { // General function to initialise the view for a new game
        this.head = 0;
        JFrame frame = new JFrame("Wordle Game");
        try{
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initLabels(frame);
        initButtons(frame);
        frame.setSize(500, controller.getNumGuesses()*50 + 250);
        frame.setLayout(null); //using no layout managers
        frame.setVisible(true);
        return frame;
    }

    private void initLabels(JFrame frame) { // Loads in the labels used to show user input letters
        this.guesses = new ArrayList<>();
        int y = 25;
        for (int i = 0; i < controller.getNumGuesses(); i++) {
            List<JLabel> row = new ArrayList<>();
            this.guesses.add(row);
            int x = 70;
            for (int j = 0; j < 5; j++) {
                JLabel label = new JLabel();
                label.setBounds(x,y,50,50);
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setBackground(Color.lightGray);
                frame.add(label);
                this.guesses.get(i).add(label);
                x += 60;
            }
            y += 60;
        }

        this.errorMessage = new JLabel("<html>"+ "Invalid Word!" +"</html>");
        this.answerMessage = new JLabel("<html>"+ "The answer was " + controller.getAnswerAsString() +"</html>");
        this.errorMessage.setForeground(Color.red);
        this.answerMessage.setForeground(Color.red);
        this.errorMessage.setBounds(420, 10, 70, 200);
        this.answerMessage.setBounds(420, 100, 70, 200);
        this.errorMessage.setVisible(false);
        this.answerMessage.setVisible(false);
        frame.add(this.errorMessage);
        frame.add(this.answerMessage);
    }

    private void initButtons(JFrame frame) { // Loads all the buttons
        // Load in the keyboard
        this.keyboard = new ArrayList<>();
        int keyboardHeightConst = controller.getNumGuesses()*50 + 100;
        int x = 20;
        int y = keyboardHeightConst;
        String[] qwerty = "qwertyuiopasdfghjklzxcvbnm".split("");
        for (int i = 0; i < qwerty.length; i++) {
            JButton btn = new JButton(qwerty[i]);
            btn.setBounds(x,y,30,30);
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.addActionListener((ActionEvent e) -> {keyboardBtnHandler(btn.getText());});
            btn.setFont(new Font("Arial", Font.PLAIN, 10));
            btn.setBackground(Color.lightGray);
            this.keyboard.add(btn);
            frame.add(this.keyboard.get(i));
            if (qwerty[i].equals("p") || qwerty[i].equals("l")) {
                if (qwerty[i].equals("p")) x = 35;
                else x = 60;
                y += 40;
            } else x += 40;
        }
        // Load in enter and delete buttons
        JButton enter = new JButton("ENT");
        JButton del = new JButton("DEL");
        enter.setBounds(425, keyboardHeightConst+ 65, 60, 40);
        del.setBounds(425, keyboardHeightConst+ 15, 60, 40);
        enter.setFont(new Font("Arial", Font.BOLD, 10));
        del.setFont(new Font("Arial", Font.BOLD, 10));
        enter.addActionListener((ActionEvent e) -> {enterHandler();});
        del.addActionListener((ActionEvent e) -> {delHandler();});
        frame.add(enter);
        frame.add(del);

        // Load in new game button
        this.newGameBtn = new JButton("New Game");
        this.newGameBtn.setBounds(385, keyboardHeightConst-70, 100, 40);
        this.newGameBtn.addActionListener((ActionEvent e) -> {newGameHandler();});
        this.newGameBtn.setVisible(false);
        frame.add(this.newGameBtn);
    }

    public void addLetter(Letter letter) { // Letter pressed by user is shown in corresponding label
        JLabel label = this.guesses.get(controller.getTurnCount()).get(head);
        label.setText(letter.getName());
        if (head < 5) head++;
        this.frame.repaint();
    }

    public void removeLetter() { // Make last updated label blank
        if (head > 0) head--;
        JLabel label = this.guesses.get(controller.getTurnCount()).get(head);
        label.setText(null);
        this.frame.repaint();
    }

    private void keyboardBtnHandler(String text) { // Event handler for when the letters of the keyboard are pressed
        if (controller.getGameFlag()) controller.submitLetter(text);
    }

    private void enterHandler() { // Event handler for when the enter key is pressed
        if (controller.getGameFlag()) {
            this.errorMessage.setVisible(!controller.isValidWord(controller.getCurrentGuessAsString())
                    && controller.getStrictFlag());
            controller.submitGuess();
        }
    }

    private void delHandler() { // Enter handle for when the delete key is pressed
        if (controller.getGameFlag()) controller.removeLetter();
    }

    private void newGameHandler() {
        controller.newGame();

        for (List<JLabel> row : this.guesses)
            for (JLabel label : row) {
                label.setText(null);
                label.setBackground(Color.lightGray);
                label.setForeground(Color.black);
            }

        for (JButton btn : this.keyboard) {
            btn.setBackground(Color.lightGray);
            btn.setForeground(Color.black);
        }

        this.newGameBtn.setVisible(false);
        this.answerMessage.setVisible(false);
    }

    @Override
    public void update(Observable o, Object arg) {
        // Update background of labels that have been used
        List<List<Letter>> submittedGuesses = controller.getSubmittedGuesses();
        List<Color> colours = controller.getColours();
        this.head = 0;
        List<Letter> guess = submittedGuesses.get(controller.getTurnCount()-1);
        for (int i = 0; i < guess.size(); i++) {
            JLabel label = this.guesses.get(controller.getTurnCount() - 1).get(i);
            Color colour = colours.get(guess.get(i).getRoundStates()[i]);
            label.setBackground(colour);
            if (guess.get(i).getRoundStates()[i] == Model.WRONG) label.setForeground(Color.white);
        }

        // Update backgrounds of buttons
        for (JButton btn : this.keyboard) {
            Letter letter = controller.getLetter(btn.getText());
            if (letter.getButtonState() != Model.UNASSIGNED) {
                Color colour = colours.get(letter.getButtonState());
                btn.setBackground(colour);
                if (letter.getButtonState() == Model.WRONG) btn.setForeground(Color.white);
            }
        }

        // Reveal new game button after first turn
        if (model.getTurnCount() == 1) this.newGameBtn.setVisible(true);
        // Reveal word if player runs out of turns
        if (!controller.getGameFlag() && !controller.getWinFlag()) answerMessage.setVisible(true);
    }


}
