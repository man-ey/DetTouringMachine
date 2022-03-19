import java.util.Arrays;

/**
 * Command class implementing Comparable to compare multiple Commands.
 * Stores info about when to execute and how to.
 */
public class Command implements Comparable<Command> {

    private int origin;
    private int target;
    private char inputChar;
    private char[] tapeChars;
    private char[] newChars;
    private Direction[] pointerMoves;
    private Direction inputPointerMove;

    /**
     * Constructor for Commands.
     * @param source
     * @param target
     * @param input
     * @param tapeChars
     * @param newChar
     * @param pointerMoves
     * @param inputPointerMove
     */
    Command(int source, int target, char input, char[] tapeChars,
            char[] newChar, Direction[] pointerMoves,
            Direction inputPointerMove) {

        this.origin = source;
        this.target = target;
        this.inputChar = input;
        this.tapeChars = tapeChars;
        this.newChars = newChar;
        this.pointerMoves = pointerMoves;
        this.inputPointerMove = inputPointerMove;
    }

    /**
     * Executing the Command on the given Tapes
     * @param inputTape
     * @param workTapes
     * @return ID of State to be transitioned into.
     */
    public int execute(InputTape inputTape, Tape[] workTapes) {

        int inputPointerMoveInt = inputPointerMove.getMoveInt();
        int pointerMoveInt;

        //Move pointers of WorkTapes and write new symbols
        for (int i = 0; i < pointerMoves.length; i++) {
            pointerMoveInt = pointerMoves[i].getMoveInt();
            Tape current = workTapes[i];

            if (pointerMoveInt > 0) {
                current.addNewChar(newChars[i]);
                current.movePointerUp();
            } else if (pointerMoveInt < 0) {
                current.addNewChar(newChars[i]);
                current.movePointerDown();
            } else {
                current.addNewChar(newChars[i]);
            }
        }

        //Move pointer of InputTape
        if (inputPointerMoveInt > 0) {
            inputTape.movePointerUp();
        } else if (inputPointerMoveInt < 0) {
            inputTape.movePointerDown();
        }

        return target;
    }

    /**
     * Converting the Command into a String to be printable.
     * @return Command as a String.
     */
    public String commandToString() {
        String toReturn = "(" + String.valueOf(origin) + ", " + inputChar;

        //Iterating through the initial situation of the command
        for (int i = 0; i < tapeChars.length; i++) {
            toReturn = toReturn + ", " + tapeChars[i];
        }

        //Adding the transition to the String
        toReturn = toReturn + ") -> (" + String.valueOf(target) + ", "
                + inputPointerMove.getMoveStr();

        //Iterating through the second part of the command
        for (int i = 0; i < newChars.length; i++) {

            toReturn = toReturn + ", " + newChars[i] + ", "
                            + pointerMoves[i].getMoveStr();
        }

        return toReturn + ")";
    }

    /**
     * Method comparing two commands.
     * Comparing origin, input character and current WorkTape-characters.
     * @param other the Command to compare to.
     * @return 0 if these three are equal, 1 otherwise.
     */
    @Override
    public int compareTo(Command other) {
        if (this.origin != other.origin) {
            return 1;
        } else if (this.inputChar != other.inputChar) {
            return 1;
        } else if (!Arrays.equals(this.tapeChars, other.tapeChars)) {
            return 1;
        } else {
            return 0;
        }
    }
}