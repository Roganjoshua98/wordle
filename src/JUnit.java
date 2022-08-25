import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JUnit {
    Model model;

    boolean invariant(Model model) {
        return model.getTurnCount() <= Model.NUMGUESSES;
    }

    @Test
    @DisplayName("Turn Count Updates Properly")
    void test1() throws IOException {
        model = new Model(1,0,0);
        assertTrue(invariant(model));
        assertEquals(0, model.getTurnCount());
        model.submitWord("aaaaa");
        model.submitGuess();
        model.submitWord("bbbbb");
        model.submitGuess();
        assertEquals(2, model.getTurnCount());
        assertTrue(invariant(model));
    }

    @Test
    @DisplayName("Letter State Lists Update when Guess Submitted")
    void test2() throws IOException {
        Model.FIXED_ANSWER = "tires";
        model = new Model(0,0,0);
        assertTrue(invariant(model));
        model.submitWord("tried");
        model.submitGuess();
        assertEquals(21, model.getUnusedLetters().size());
        assertEquals(2, model.getCorrectLetters().size());
        assertEquals(1, model.getWrongLetters().size());
        assertEquals(2, model.getPartialLetters().size());
        assertTrue(invariant(model));
    }

    @Test
    @DisplayName("Game is Set as Over With No Win After NUMGUESSES is Passed")
    void test3() throws IOException {
        model = new Model(1, 0, 0);
        assertTrue(invariant(model));
        for (int i = 0; i < Model.NUMGUESSES; i++) {
            model.submitWord("aaaaa");
            model.submitGuess();
        }
        assertFalse(model.getGameFlag());
        assertFalse(model.getWinFlag());
        assertTrue(invariant(model));
    }
}
