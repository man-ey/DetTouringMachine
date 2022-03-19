import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Implementation of a Deterministic Turing machine using the TuringMachine
 * interface.
 * Consists of States, one InputTape and WorkTapes.
 */
public class DetTuringMachine implements TuringMachine {

    private InputTape inputTape;
    private final int amountTapes;
    private final State[] states;
    //The current State of the TuringMachine
    private State machineState;
    private Tape[] tapes;
    private final int startID;

    /**
     * Constructor method.
     * @param states amount in this machine.
     * @param tapes amount of WorkTapes in this machine.
     * @param start ID of the start-State.
     * @param stops Collection of halt-States.
     * @param accepting Collection of accepting halt-States.
     */
    DetTuringMachine(int states, int tapes, int start, Set<Integer> stops,
                     Set<Integer> accepting) {
       this.states = new State[states];
       this.createStates(states, start, stops, accepting);
       this.startID = start;
       this.amountTapes = (tapes + 1);
       this.tapes = new Tape[amountTapes];
       this.createTapes(tapes);
   }

   /**
    * Side method called when instancing a new machine creating all States
    * and giving them the right attributes/parameters.
    * @param amount of States to be created.
    * @param startNumber ID of the start-State.
    * @param stopNumbers collection of States stopping the run/pass.
    * @param acceptNumbers collection of States being accepting Endstates.
    */
   private void createStates(int amount, int startNumber,
                             Set<Integer> stopNumbers,
                             Set<Integer> acceptNumbers) {
       boolean currentIsAcc;
       boolean currentIsStop;
       State current;

       for (int i = 0; i < amount; i++) {
           if (acceptNumbers.contains(i)) {
               currentIsAcc = true;
           } else {
               currentIsAcc = false;
           }

           if (stopNumbers.contains(i)) {
               currentIsStop = true;
           } else {
               currentIsStop = false;
           }

           if (i == startNumber) {
               current = new State(i, currentIsAcc, currentIsStop, true);
           } else {
               current = new State(i, currentIsAcc, currentIsStop, false);
           }

           states[i] = current;
       }

       machineState = states[startID];
   }

   /**
     * Side method cleansing the current worktapes -if existing- and creating
     * new empty worktapes.
     * @param amount
     */
   private void createTapes(int amount) {
       Tape current;
       for (int i = 0; i <= amount; i++) {
           current = new Tape();
           tapes[i] = current;
       }
   }

   /**
    * Side method gathering all Symbols which are currently pointed at on
    * all WorkTapes.
    * @return all signs gathered in a character array.
    */
   private char[] getCurrentSigns() {
       char[] toReturn = new char[tapes.length];
       for (int i = 0; i < tapes.length; i++) {
           toReturn[i] = tapes[i].getCurrent();
       }
       return toReturn;
   }

    /**
     * Side method filling the InputTape with a given word/key.
     * @param input
     */
   private void loadInputTape(String input) {
       inputTape = new InputTape(input);
    }

   /**
    * Method checking, if and which command has to be executed based on
    * @param currentTapeChars array of all pointed-to symbols on WorkTapes
    * @param inputChar the symbol read on the InputTape.
    * @return the fitting command if existing/possible, null otherwise.
    */
   private Command getFittingCmd(char[] currentTapeChars,
                                 char inputChar) {
       Command toReturn = null;
       //All commands available for the current State gathered
       ArrayList<Command> stateCmds = machineState.getCommands();

       //Setting up a dummy Command to compare and find out the right one.
       Direction[] dummyDirArray = new Direction[0];
       Direction dummyDir = null;
       int targetDummy = 0;
       char[] dummyNewChars = new char[0];
       Command compareDummy = new Command(machineState.getId(), targetDummy,
               inputChar, currentTapeChars, dummyNewChars,
               dummyDirArray, dummyDir);

       //Loop running through all
       Iterator<Command> command = stateCmds.iterator();
       while  (command.hasNext()) {
           Command i = command.next();
           //Comparison = 0 if Requirements are met, 1 otherwise
           int test = i.compareTo(compareDummy);
           if (test == 0) {
               toReturn = i;
               break;
           }
       }

       return toReturn;
   }

   /**
    * Method called after running a word through the machine resetting all
    * WorkTapes and the current State.
    */
   private void resetMachine() {
       createTapes(amountTapes - 1);
       machineState = states[startID];
       return;
   }

   /**
    * Method running the machine until either no more command is available
    * or a stop-State is reached.
    */
   private void runCheck() {
       //Getting the current WorkTape symbols and the command to execute
       char[] newTapeChars = getCurrentSigns();
       Command currentCmd = getFittingCmd(newTapeChars,
               inputTape.getCurrent());

       //Iterating through the machine until no Command available or stop-State
       while ((currentCmd != null) && (machineState.getStopClass() == 0)) {
           //Returning the State-ID reached after executing the Command
           int executeCmd = currentCmd.execute(inputTape, tapes);
           machineState = states[executeCmd];

           //Exiting if stop-State reached
           if (machineState.getStopClass() == 1
                   || machineState.getStopClass() == 2) {
               return;
           }

           //Setting up for next loop
           newTapeChars = getCurrentSigns();
           currentCmd = getFittingCmd(newTapeChars,
                   inputTape.getCurrent());
       }
   }

   /**
    * Side method removing all leading and trailing blank-symbols on the
    * given String.
    * @param toTrim
    * @return trimmed String
    */
   private String cleanupBlanks(String toTrim) {
       char blank = TuringMachine.BLANK_CHAR;
       char[] trimming = toTrim.toCharArray();

       //Nothing to trim
       if (trimming.length <= 1) {
           return toTrim;
       }

       //How many leading Blank signs
       int counterFront = 0;
       while (counterFront < trimming.length
               && trimming[counterFront] == blank) {
           counterFront++;
       }

       //How many trailing blank signs
       int counterBack = trimming.length - 1;
       while (counterBack >= 0 && trimming[counterBack] == blank) {
           counterBack--;
       }

       //Building the String without the blanks
       String toReturn = "";
       for (int i = counterFront; i <= counterBack; i++) {
           toReturn = toReturn + trimming[i];
       }

       return toReturn;
   }

   /**
    * Method validating if the given symbol/characer matches the pattern set
    * up in the TuringMachine Interface.
    * @param ch
    * @return yes if matching, false otherwise.
    */
   public static boolean isValidTapeChar(char ch) {
       char first = TuringMachine.FIRST_CHAR;
       char last = TuringMachine.LAST_CHAR;
       char blank = TuringMachine.BLANK_CHAR;
       String charsRex = "[" + first + "-" + last + "]";
       Pattern pattern = Pattern.compile(charsRex);
       return (pattern.matcher(String.valueOf(ch)).matches() || ch == blank);
   }

   /**
    * Implementing the method by delegating it to the responsible State and
    * adding it to its Command-List.
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
    */
   @Override
   public void addCommand(int sourceState, char inputTapeChar,
                          char[] tapeChars, int targetState,
                          Direction inputTapeHeadMove, char[] newTapeChars,
                          Direction[] tapeHeadMoves) {
       Command toAdd = new Command(sourceState, targetState, inputTapeChar,
               tapeChars, newTapeChars, tapeHeadMoves, inputTapeHeadMove);
       State current = states[sourceState];
       current.addCommand(toAdd);
   }

   @Override
   public String simulate(String input) {
       //No input?
       if (input == "") {
           return  "";
       }

       loadInputTape(input);
       char[] tapeChars = getCurrentSigns();
       Command currentCmd = getFittingCmd(tapeChars, inputTape.getCurrent());

       //Run machine until Stop or no Cmd
       while ((machineState.getStopClass() == 0) && (currentCmd != null)) {
           int executeCmd = currentCmd.execute(inputTape, tapes);
           machineState = states[executeCmd];
           tapeChars = getCurrentSigns();
           currentCmd = getFittingCmd(tapeChars, inputTape.getCurrent());
       }

       String toReturn = cleanupBlanks(tapes[0].getTapeAsString());
       resetMachine();
       return toReturn;
   }

   @Override
   public boolean check(String input) {
       loadInputTape(input);

       //Start state = AcceptanceState?
       if (machineState.getStopClass() == 2) {
           return true;
       } else {
           runCheck();
       }

       //Reached State AcceptanceState?
       if (machineState.getStopClass() == 2) {
           resetMachine();
           return true;
       } else {
           resetMachine();
           return false;
       }
   }

   @Override
   public String toString() {
       String toReturn = "";
       for (int i = 0; i < states.length; i++) {
           State current = states[i];
           toReturn = toReturn + current.printCommands();
       }
       return toReturn;
   }
}