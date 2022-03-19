import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

/**
 * Shell class handling the input and executing methods on the trie.
 */
public final class Shell {

    //Prompt of the UserInterface
    private static final String PROMPT = "dtm> ";

    /**
     * Main method starting up the Shell input
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        BufferedReader stdin
                = new BufferedReader(new InputStreamReader(System.in));

        shellExecute(stdin);
    }

    /**
     *Shell method monitoring input and executing methods based on it
     * @param stdin
     * @throws IOException
     */
    private static void shellExecute(BufferedReader stdin) throws IOException {
        TuringMachine machine = null;
        //Param deciding if to quit the input-loop
        boolean quit = false;

        //Input loop handling the input and responses
        while (!quit) {
            //Standard shell line
            System.out.print(PROMPT);
            //Setting up command line inputs
            String input = stdin.readLine();
            //Input in split up in array
            String[] parts = input.trim().split("\\s+");

            //checking input
            if (parts.length < 1) {
                error("Empty command");
            } else {
                //Switch handling inputs
                switch (parts[0].toLowerCase().charAt(0)) {

                    //Closing the user interface
                    case 'q' :
                        quit = true;
                        break;

                    //Input of a new file
                    case 'i' :
                        machine = commandNew(parts, machine);
                        if (machine == null) {
                            error("Couldn't initialize machine!");
                        }
                        break;

                    //Run the inputstring and print the result
                    case 'r' :
                        commandRun(parts, machine);
                        break;

                    //Checking acceptance of input in current machine
                    case 'c' :
                        commandCheck(parts, machine);
                        break;

                    //Printing all commands in order
                    case 'p' :
                        if (machine == null) {
                            System.out.println("");
                            break;
                        }
                        commandPrint(machine);
                        break;

                    //Display of help
                    case 'h' :
                        commandHelp();
                        break;

                    default :
                        error("Unknown command.");
                }
            }
        }
    }

    /**
     * Side method executed every time an error statement needs to be shown.
     * Prints the statement with a set prefix on the console.
     * @param errorMsg the errormessage to be shown on the console.
     */
    private static void error(String errorMsg) {
        System.out.println("Error! " + errorMsg);
    }

    /**
     * Side method validating that the input string has the least necessary
     * amount of parameters/keys.
     * @param inputs
     * @param min necessary amount of keys.
     * @return true if at least min amount, false otherwise.
     */
    private static boolean validAmount(String[] inputs, int min) {
        boolean toReturn;
        //Checking if length is correct
        if (inputs.length < min) {
            toReturn = false;
        } else {
            toReturn = true;
        }

        return toReturn;
    }

    /**
     * Side method called when a new TuringMachine has to be set up.
     * Calls the TuringMachineFactory and returns its result.
     * @param parts the name of the file which contains the instructions.
     * @param toSet
     * @return the built TuringMachine if successful, null otherwise.
     */
    private static TuringMachine commandNew(String[] parts,
                                            TuringMachine toSet) {
        //(Trying to) create machine
        if (validAmount(parts, 2)) {
            try {
                String fileName = parts[1];
                File machineFile = new File(fileName);
                toSet = TuringMachineFactory.loadFromFile(machineFile);
                return toSet;
            } catch (FileNotFoundException e) {
                error("No file found!");
                return null;
            } catch (ParseException e) {
                error("Parsing not possible!");
                return null;
            } catch (IOException e) {
                error("Fault at IO!");
                return null;
            }
        } else {
            error("Wrong amount of input!");
            return null;
        }
    }

    /**
     * Side method advising the Machine to run the inputstring and printing
     * the result.
     * @param inputs is the word to be inserted.
     * @param turingMachine
     */
    private static void commandRun(String[] inputs,
                                   TuringMachine turingMachine) {
        //Right amount of words in input?
        if (validAmount(inputs, 2)) {
            String key = inputs[1];
            //Key matching input pattern?
            if (isValidKey(key, turingMachine)) {
                String output = turingMachine.simulate(key);
                System.out.println(output);
            } else {
                error("Not matching the alphabet!");
            }
        } else {
            String key = "";
            String output = turingMachine.simulate(key);
            System.out.println(output);
        }
    }

    /**
     *Side method handing the current TuringMachine a word to check if it is
     * accepted and printing the result on the terminal.
     * @param inputs
     * @param turingMachine
     */
    private static void commandCheck(String[] inputs,
                                     TuringMachine turingMachine) {
        //Right amount of words in input?
        if (validAmount(inputs, 2)) {
            String key = inputs[1];
            //Key matching input pattern?
            if (isValidKey(key, turingMachine)) {
                //Checking if containing
                if (turingMachine.check(key)) {
                    System.out.println("accept");
                } else {
                    System.out.println("reject");
                }
            } else {
                error("Not matching the alphabet!");
            }
        } else {
            System.out.println("accept");
        }
    }

    /**
     * Side method gathering the String from the TuringMachine and printing
     * the result.
     * @param turingMachine
     */
    private static void commandPrint(TuringMachine turingMachine) {
        String allCmds = turingMachine.toString();
        String[] parts = allCmds.split("[)]");

        //Commands divided in pt1:Current state & pt2:Execution commands
        for (int i = 0; i < (parts.length - 1); i = i + 2) {
            String partOne = parts[i] + ")";
            String partTwo = parts[i + 1] + ")";
            System.out.println(partOne.trim() + partTwo);
        }
    }

    /**
     * Method printing out a list of Commands available.
     */
    private static void commandHelp() {
        System.out.println("Available commands:");
        System.out.println("Load a new machine: insert (TuringMachineFile)");
        System.out.println("Run a word to get the result: run (word to run)");
        System.out.println("Check if the machine accepts a certain word: "
                + "check (word to be checked");
        System.out.println("Printing out all commands: print");
        System.out.println("Exiting the TuringMachine and Input: quit");
    }

    /**
     * Method validating if the given Input for the machine matches the
     * alphabet of the machine.
     * @param toVal String to validate.
     * @param machine the current machine.
     * @return true if matching, false otherwise.
     */
    public static boolean isValidKey(String toVal, TuringMachine machine) {
        char first = TuringMachine.FIRST_CHAR;
        char last = TuringMachine.LAST_CHAR;
        String charsRex = ".*[" + first + "-" + last + "].*";
        return toVal.matches(charsRex);
    }

    /**
     * Utility class constructor preventing instancation.
     */
    private Shell() {
        throw new UnsupportedOperationException("Illegal calling of "
                + "constructor!");
    }
}