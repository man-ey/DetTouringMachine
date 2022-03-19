import java.util.ArrayList;
import java.util.Arrays;

/**
 * State class.
 * State contains/knows its ID, stopClass and Commands executable from it.
 */
public class State {

    private final int id;
    //0 if none, 1 if only stopState, 2 if stop- and acceptingState
    private final int stopClass;
    private ArrayList<Command> commands;

    /**
     * Constructor method for States
     * @param givenId
     * @param acceptance
     * @param halting
     * @param starting
     */
    State(int givenId, boolean acceptance, boolean halting, boolean starting) {
        this.id = givenId;
        this.stopClass = assignStopClass(acceptance, halting);
        this.commands =  new ArrayList<>();
    }

    /**
     * Assigning a stopclass to this State when it is constructed.
     *
     * @param acceptance
     * @param halting
     * @return 0 if neither, 1 if only stop, 2 if stop and accepting.
     */
    private int assignStopClass(boolean acceptance, boolean halting) {
        if (acceptance) {
            return 2;
        } else if (halting) {
            return  1;
        } else {
            return  0;
        }
    }

    /**
     * @return the stopClass of this State.
     */
    public int getStopClass() {
        return stopClass;
    }

    /**
     * @return all Commands with this State as origin.
     */
    public ArrayList<Command> getCommands() {
        return commands;
    }

    /**
     * @return the number/ID of the State.
     */
    public int getId() {
        return id;
    }

    /**
     * Adding a created Command to the list of available Commands.
     * @param cmd
     */
    public void addCommand(Command cmd) {
        commands.add(cmd);
    }

    /**
     * Method converting all Commands stored in this State to a String and
     * then sorting them alphabetically before combining them to one String.
     *
     * @return ordered String containing all Commands printed.
     */
    public String printCommands() {
        //Converting and filling the Commands into a storage
        String[] sorting = new String[commands.size()];
        for (int i = 0; i < sorting.length; i++) {
            sorting[i] = commands.get(i).commandToString();
        }

        //Sorting and combining
        Arrays.sort(sorting);
        String toReturn = "";
        for (int i = 0; i < sorting.length; i++) {
            toReturn = toReturn + sorting[i];
        }

        return toReturn;
    }
}