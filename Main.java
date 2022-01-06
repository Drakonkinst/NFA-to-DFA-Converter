import java.io.*;
import java.util.*;

public class Main {
    // Input
    
    // Returns an FSA based on the given file path.
    private static FSA readNFAFromInput(String filePath) {
        List<String> lines = readLines(filePath);
        if(lines.size() < 5) {
            throw new IllegalStateException("Input file must have at least 5 lines");
        }
        
        // Extract all info needed to create the FSA from lines
        List<State> states = readStates(lines.get(0));
        String[] alphabet = splitByTabs(lines.get(1));
        State initialState = toState(lines.get(2));
        Set<State> acceptStates = new HashSet<>(readStates(lines.get(3)));
        List<Transition> transitions = readTransitions(lines);
        
        return new FSA(states, alphabet, initialState, acceptStates, transitions);
    }
    
    // Reads a file as a list of lines.
    private static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            
            // Read lines until EOF
            while(line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            
            reader.close();
        } catch(IOException e) {
            System.out.println("Error while reading input.");
            e.printStackTrace();
        }
        
        return lines;
    }
    
    // Reads line 5 onwards as a list of transitions
    private static List<Transition> readTransitions(List<String> lines) {
        List<Transition> transitionMap = new ArrayList<>();
        
        // Read transition rules until the last line
        for(int i = 4; i < lines.size(); ++i) {    
            String line = lines.get(i);
            int commaIndex = line.indexOf(',');
            int equalsIndex = line.indexOf('=');
            
            // Check if it matches the general form of a transition
            if(commaIndex < 0 || equalsIndex < 0 || equalsIndex <= commaIndex) {
                throw new IllegalArgumentException("Transition rule should be of the form \"A, x = B\"");
            }
            
            // Extract info
            State start = toState(line.substring(0, commaIndex));
            String symbol = line.substring(commaIndex + 1, equalsIndex).trim();
            State end = toState(line.substring(equalsIndex + 1));

            transitionMap.add(new Transition(start, symbol, end));
        }
        
        return transitionMap;
    }
    
    // Returns a list of states based on a tab-separated string of states
    private static List<State> readStates(String line) {
        List<State> states = new ArrayList<>();
        String[] stateStrings = splitByTabs(line);

        for(String stateString : stateStrings) {
            states.add(toState(stateString));
        }
        
        Collections.sort(states);
        return states;
    }
    
    // Splits a line using the tab character
    private static String[] splitByTabs(String line) {
        return line.split("\t");
    }
    
    // Converts a string to a state
    private static State toState(String stateString) {
        stateString = stateString.trim();
        
        // Check if string matches form "{x}"
        if(stateString.length() <= 2 || stateString.charAt(0) != '{' || stateString.charAt(stateString.length() - 1) != '}') {
            throw new IllegalArgumentException("State " + stateString + " must be enclosed by curly braces {}");
        }
        
        return State.of(stateString.substring(1, stateString.length() - 1));
    }
    
    // Output
    
    // Writes FSA to file
    private static void writeOutputFromDFA(FSA dfa, String fileName) {
        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.println(statesToString(dfa.getStates()));
            writer.println(alphabetToString(dfa.getAlphabet()));
            writer.println(dfa.getInitialState().toString());
            writer.println(statesToString(dfa.getAcceptStates()));

            // Print each transition in their own line
            List<Transition> transitions = dfa.getTransitions();
            for(Transition transition : transitions) {
                writer.println(transition.toString());
            }
            
            writer.close();
        } catch (IOException e) {
            System.out.println("Error while writing output.");
            e.printStackTrace();
        }
    }
    
    // Converts a collection of States to a tab-separated line
    private static String statesToString(Collection<State> states) {
        Iterator<State> stateIterator = states.iterator();
        String result = "";

        while(stateIterator.hasNext()) {
            result += stateIterator.next() + "\t";
        }
        
        // Remove last character
        if(result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
    
    // Converts a String array to a tab-separated line
    private static String alphabetToString(String[] alphabet) {
        String result = "";
        for(String symbol : alphabet) {
            result += symbol + "\t";
        }
        
        // Remove last character
        if(result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        
        return result;
    }
    
    // Main
    public static void main(String[] args) {
        if(args.length < 1) {
            throw new IllegalArgumentException("Input path must be specified!");
        }
        
        // Read NFA
        FSA nfa = readNFAFromInput(args[0]);
        System.out.println("=== NFA ===");
        nfa.print();
        System.out.println();
        
        // Convert and write DFA
        FSA dfa = nfa.convertToDFA();
        System.out.println("=== DFA ===");
        dfa.print();
        writeOutputFromDFA(dfa, "output.DFA");
    }
}