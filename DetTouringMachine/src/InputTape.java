import java.util.ArrayList;

/**
 * InputTape class extending the normal WorkTape-Tape class, by augmented
 * methods for moving the pointer.
 * "Endlessness" of Tape simulated by adding Blanks if necessary.
 */
public class InputTape extends Tape {

    private ArrayList<Character> tapeContent;
    private int pointer;

    /**
     * Constructor method for InputTapes.
     * @param input the key entered.
     */
    InputTape(String input) {
        char[] symbols = input.toCharArray();
        tapeContent = new ArrayList<Character>();

        //Fill Tape
        for (int i = 0; i < symbols.length; i++) {
            tapeContent.add(i, symbols[i]);
        }

        //Empty String handed?
        if (tapeContent.get(0) == null) {
            tapeContent.add(TuringMachine.BLANK_CHAR);
        }

        //Setup pointer
        pointer = 0;
    }

    /**
     * Method moving the pointer up one position.
     */
    public void movePointerUp() {
        //"Endlessness" through adding blank char
        if (pointer == tapeContent.size() - 1) {
            tapeContent.add(TuringMachine.BLANK_CHAR);
        }

        pointer++;
    }

    /**
     * Method moving the pointer down one position.
     */
    public void movePointerDown() {
        //"Endlessness" through adding blank char
        if (pointer == 0) {
            tapeContent.add(0, TuringMachine.BLANK_CHAR);
        } else {
            pointer--;
        }
    }

    /**
     *
     * @return the char currently pointed at by the pointer.
     */
    public char getCurrent() {
        return tapeContent.get(pointer);
    }
}