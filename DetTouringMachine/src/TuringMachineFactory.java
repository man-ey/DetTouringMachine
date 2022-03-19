import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory to create a Turing machine programmed by a given file.
 *
 * For a simplification of the exercise, we assume that format and content of
 * the passed file is correct. 
 */
public final class TuringMachineFactory {

    /**
     * Combines a text line of the file with its line number.
     *
     * Optional, as only necessary for location of syntax errors in Turing
     * machine definitions.
     */
    private static class Line {
        private String text;
        private int number;
    }

    private static final String DELIMITER = " ";

    /**
     * Utility class constructor preventing instantiation.
     */
    private TuringMachineFactory() {
        throw new UnsupportedOperationException(
                "Illegal call of utility class constructor.");
    }

    /**
     * Loads a Turing machine program from a given file and creates the
     * respective machine.
     *
     * @param file The input file.
     * @return The Turing machine using the given program.
     * @throws FileNotFoundException If the input file could not be found.
     * @throws IOException If an IO error occurs.
     * @throws ParseException If the file is not using the expected format.
     */
    public static TuringMachine loadFromFile(File file)
            throws FileNotFoundException, IOException, ParseException {
        TuringMachine turingMachine = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            // Read maximum number of states.
            Line line = readLine(reader, 0);
            int numberOfStates = parsePosInt(line.text, line.number);

            // Read number of tapes.
            line = readLine(reader, line.number);
            int numberOfTapes = parsePosInt(line.text, line.number);

            // Read id of starting state.
            line = readLine(reader, line.number);
            int startStateId
                    = parseStateId(line.text, numberOfStates, line.number);

            // Read ids of stopping states.
            line = readLine(reader, line.number);
            Set<Integer> stopStateIds
                    = parseStateIds(line.text, numberOfStates, line.number);

            // Read ids of accepting states.
            line = readLine(reader, line.number);
            Set<Integer> acceptStateIds
                    = parseStateIds(line.text, numberOfStates, line.number);
            if (!stopStateIds.containsAll(acceptStateIds)) {
                invalidFileError(line.number);
            }

            turingMachine = new DetTuringMachine(numberOfStates, numberOfTapes,
                    startStateId, stopStateIds, acceptStateIds);

            // Read Turing program.
            line = readLine(reader, line.number);
            while (line != null) {
                parseCmd(line.text, numberOfTapes, numberOfStates,
                        turingMachine, line.number);
                line = readLine(reader, line.number);
            }
        }

        return turingMachine;
    }

    private static Line readLine(BufferedReader reader, int lineNumber)
            throws IOException {
        Line line = new Line();
        line.number = lineNumber;
        do {
            ++line.number;
            line.text = reader.readLine();
            if (line.text == null) {
                return null;
            }
        } while (line.text.trim().startsWith("#"));  // Optional.
        return line;
    }

    private static void parseCmd(String line, int numberOfTapes,
                                 int numberOfStates,
                                 TuringMachine turingMachine, int lineNumber)
            throws ParseException {
        /*
         * (q, a, A_0, A_1, ..., A_k)
         *                         -> (q', d, B_0, d_0, B_1, d_1, ..., B_k, d_k)
         */

        String[] args = line.split(DELIMITER);

        int expectedNumberOfArgs = 3 + numberOfTapes + 4 + 2 * numberOfTapes;
        if (args.length != expectedNumberOfArgs) {
            invalidFileError(lineNumber);
        }

        // Parse source state.
        int sourceStateId = parseStateId(args[0], numberOfStates, lineNumber);

        // Parse input tape.
        char inputTapeChar = parseChar(args[1], lineNumber);

        // Parse working tapes - the output tape is the first working tape, but
        // not counted.
        char[] tapeChars = new char[numberOfTapes + 1];
        for (int i = 0; i < numberOfTapes + 1; ++i) {
            tapeChars[i] = parseChar(args[2 + i], lineNumber);
        }

        // Parse target state.
        int targetStateId = parseStateId(args[2 + numberOfTapes + 1],
                numberOfStates, lineNumber);

        // Parse movement on input tape.
        Direction inputTapeHeadMove
                = parseMovement(args[3 + numberOfTapes + 1], lineNumber);

        // Parse working tapes - the output tape is the first working tape, but
        // not counted.
        char[] newTapeChars = new char[numberOfTapes + 1];
        Direction[] tapeHeadMoves = new Direction[numberOfTapes + 1];
        for (int i = 0; i < newTapeChars.length; ++i) {
            int pos = 4 + numberOfTapes + 1 + i * 2;
            newTapeChars[i] = parseChar(args[pos], lineNumber);
            tapeHeadMoves[i] = parseMovement(args[pos + 1], lineNumber);
        }

        turingMachine.addCommand(sourceStateId, inputTapeChar, tapeChars,
                targetStateId, inputTapeHeadMove, newTapeChars, tapeHeadMoves);
    }

    private static Direction parseMovement(String arg, int lineNumber)
            throws ParseException {
        int value = parseInt(arg, lineNumber);
        if (value < -1 || value > 1) {
            invalidFileError(lineNumber);
        }
        return Direction.values()[value + 1];
    }

    private static char parseChar(String arg, int lineNumber)
            throws ParseException {
        char c = '\u0000';

        if (arg.length() == 1) {
            c = arg.charAt(0);
            if (!DetTuringMachine.isValidTapeChar(c)) {
                invalidFileError(lineNumber);
            }
        } else {
            invalidFileError(lineNumber);
        }

        return c;
    }

    private static int parseInt(String arg, int lineNumber)
            throws ParseException {
        int result = 0;
        try {
            result = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            invalidFileError(lineNumber);
        }
        return result;
    }

    private static int parsePosInt(String arg, int lineNumber)
            throws ParseException {
        int result = parseInt(arg, lineNumber);
        if (result < 0) {
            invalidFileError(lineNumber);
        }
        return result;
    }

    private static int parseStateId(String arg, int numberOfStates,
                                    int lineNumber)
            throws ParseException {
        int id = parseInt(arg, lineNumber);
        if (id < 0 || id >= numberOfStates) {
            invalidFileError(lineNumber);
        }
        return id;
    }

    private static Set<Integer> parseStateIds(String line, int numberOfStates,
                                              int lineNumber)
            throws ParseException {
        Set<Integer> stateIds = new HashSet<>();

        String[] args = line.split(DELIMITER);
        for (int i = 0; i < args.length; ++i) {
            // Is there a state to parse?
            if (!args[i].equals("")) {
                int state = parseStateId(args[i], numberOfStates, lineNumber);
                stateIds.add(state);
            }
        }

        return stateIds;
    }

    /**
     * Reports a parse error within the input file.
     *
     * @param lineNumber The line in the file which contains an error.
     * @throws ParseException If the file is not using the expected format.
     */
    private static void invalidFileError(int lineNumber)
            throws ParseException {
        throw new ParseException(
                "Malformed file at line: " + lineNumber + "!", lineNumber);
    }

}