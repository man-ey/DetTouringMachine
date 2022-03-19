/**
 * A small interface for Turing machines accepting formal languages.
 */
public interface TuringMachine {

    /**
     * The blank character, distinct to the alphabet.
     */
    char BLANK_CHAR = '~';

    /**
     * The first character of the alphabet. Must build a continuous range with
     * {@code LAST_CHAR}.
     */
    char FIRST_CHAR = 'a';

    /**
     * The last character of the alphabet. Must build a continuous range with
     * {@code FIRST_CHAR}.
     */
    char LAST_CHAR = 'z';

    /**
     * Adds a new command to the Turing program of the machine.
     *
     * @param sourceState The state of the source configuration.
     * @param inputTapeChar The current character under the head on the input
     *                      tape.
     * @param tapeChars The current characters under the heads of the output and
     *                  working tapes. As the output tape is seen as a (special)
     *                  working tape, the character under the head of the output
     *                  tape is the first in this array. The characters of the
     *                  working tapes follow according to the used order of the
     *                  working tapes.
     * @param targetState The (new) state after executing this command.
     * @param inputTapeHeadMove The move of the head of the input tape.
     * @param newTapeChars The new characters to write the output and working
     *                     tapes on the positions indicated by the respective
     *                     heads. As the output tape is seen as a (special)
     *                     working tape, the character for the output tape is
     *                     the first in this array. The characters for the
     *                     working tapes follow according to the used order of
     *                     the working tapes.
     * @param tapeHeadMoves The relative moves for the heads of the output and
     *                      working tapes. As the output tape is seen as a
     *                      (special) working tape, the move for the output tape
     *                      head is the first in this array. The moves for the
     *                      working tape heads follow according to the used
     *                      order of the working tapes.
     */
    void addCommand(int sourceState, char inputTapeChar, char[] tapeChars,
                    int targetState, Direction inputTapeHeadMove,
                    char[] newTapeChars, Direction[] tapeHeadMoves);

    /**
     * Simulates the machine, i.e., computes and returns the output string for a
     * given input word.
     *
     * @param input The input word.
     * @return The content of the output tape after the machine has run.
     */
    String simulate(String input);

    /**
     * Checks if the given input word is accepted by the machine.
     *
     * @param input The input word.
     * @return {@code true}, if and only if the input is accepted.
     */
    boolean check(String input);

    /**
     * Returns the Turing program as string. The commands are ordered
     * lexicographically.
     *
     * @return The Turing program.
     */
    @Override
    String toString();

}
