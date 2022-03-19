import java.util.ArrayList;

/**
 * Tape class for WorkTapes.
 * "Endlessness" by adding up blanks if necessary.
 */
public class Tape {

    private ArrayList<Character> tapeContent;
    private int pointer;

    /**
     * Constructor method for WorkTapes.
     */
    Tape() {
        this.tapeContent = new ArrayList<>();
        this.tapeContent.add(DetTuringMachine.BLANK_CHAR);
        pointer = 0;
    }

    /**
     * Method moving the pointer down one position.
     */
    public void movePointerDown() {
        //"Endlessness" through adding blank char
        if (pointer == 0) {
            tapeContent.add(0, DetTuringMachine.BLANK_CHAR);
        } else {
            pointer--;
        }
    }

    /**
     * Method moving the pointer up one position.
     */
    public void movePointerUp() {
        //"Endlessness" through adding blank char
        if (pointer == tapeContent.size() - 1) {
            tapeContent.add(DetTuringMachine.BLANK_CHAR);
        }

        pointer++;
    }

    /**
     * Inserting a new character at the current position of the pointer.
     * @param symbol
     */
    public void addNewChar(char symbol) {
        //Overwriting the current character if necessary
        if (pointer != tapeContent.size() - 1) {
            tapeContent.remove(pointer);
        }
        tapeContent.add(pointer, symbol);
    }

    /**
     *
     * @return the tape printed as String with all characters/symbols.
     */
    public String getTapeAsString() {
        String toReturn = "";
        for (int i = 0; i < tapeContent.size(); i++) {
            toReturn = toReturn + tapeContent.get(i);
        }
        return toReturn;
    }

    /**
     *
     * @return the char currently pointed at by the pointer.
     */
    public char getCurrent() {
        return tapeContent.get(pointer);
    }
}