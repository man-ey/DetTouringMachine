/**
 * Enum of the possible Directions for the pointers of the Tapes.
 */
public enum Direction {

    /**
     * Enum for the left Direction of a pointer.
     */
    LEFT(-1),
    /**
     * Enum for pointer standing still.
     */
    STOP(0),
    /**
     * Enum for the right Direction of a pointer.
     */
    RIGHT(+1);
    private final int move;

    /**
     * Constructor.
     * @param number direction header is heading.
     */
    Direction(int number) {
        move = number;
    }

    /**
     *
     * @return the direction as an Integer.
     */
    public int getMoveInt() {
        return move;
    }

    /**
     *
     * @return the direction as a String to be printable.
     */
    public String getMoveStr() {
        //Ensuring the + to be added to the String
        if (move == 1) {
            return "+1";
        } else {
            return String.valueOf(move);
        }
    }
}