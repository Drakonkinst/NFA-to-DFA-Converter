import java.util.*;

public class FSA {
    private final List<State> states;
    private final String[] alphabet;
    private final State initialState;
    private final Set<State> acceptStates;
    private final List<Transition> transitions;
    private final boolean isDFA;

    public FSA(List<State> states,
            String[] alphabet,
            State initialState,
            Set<State> acceptStates,
            List<Transition> transitions,
            boolean isDFA) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialState = initialState;
        this.acceptStates = acceptStates;
        this.transitions = transitions;
        this.isDFA = isDFA;
    }
    
    public FSA(List<State> states,
            String[] alphabet,
            State initialState,
            Set<State> acceptStates,
            List<Transition> transitions) {
        this(states, alphabet, initialState, acceptStates, transitions, false);
    }

    public void print() {
        System.out.println("States: " + states);
        System.out.println("Alphabet: " + Arrays.toString(alphabet));
        System.out.println("Initial State: " + initialState);
        System.out.println("Accept States: " + acceptStates);

        // Print transitions
        System.out.println("Transitions (" + transitions.size() + "):");
        for(Transition transition : transitions) {
            System.out.println(transition);
        }
    }

    // If the FSA is not a DFA, converts it into a DFA
    public FSA convertToDFA() {
        // If FSA is already DFA, do nothing
        if(isDFA) {
            return this;
        }
        
        // Compute information needed to construct DFA
        List<State> newStates = computePowerSet(states);
        String[] newAlphabet = alphabet.clone();
        State newInitialState = mergeStates(computeEpsilonClosure(initialState));
        List<Transition> newTransitions = computeTransitions();
        Set<State> newAcceptStates = computeAcceptStates(newTransitions);

        return new FSA(newStates, newAlphabet, newInitialState, newAcceptStates, newTransitions, true);
    }

    // If input is sorted, then all state names in the output are also sorted
    private List<State> computePowerSet(List<State> states) {
        List<State> subsets = new ArrayList<>();
        List<State> powerSet = new ArrayList<>();
        powerSet.add(State.EMPTY); // Power set always contains the empty set
        // Each index of the exists array represents
        // whether the state at that index exists.
        boolean[] exists = new boolean[states.size()];
        // Equivalent to 2 ^ states.size() - 1
        int numIterations = (1 << states.size()) - 1;

        // Iterate through all possible permutations of the exists array
        for(int i = 0; i < numIterations; ++i) {
            addOne(exists);
            Set<String> subset = new HashSet<>();

            for(int j = 0; j < exists.length; ++j) {
                if(exists[j]) {
                    subset.addAll(states.get(j).getNames());
                }
            }
            
            subsets.add(State.of(subset));
        }

        // Add subsets in sorted order (defined by State#compareTo())
        Collections.sort(subsets);
        for(State subset : subsets) {
            powerSet.add(subset);
        }

        return powerSet;
    }

    // Increments a bit array by 1.
    private void addOne(boolean[] bitArr) {
        int carry = 1;
        for(int i = bitArr.length - 1; carry > 0 && i >= 0; --i) {
            int current = bitArr[i] ? 1 : 0;
            int result = current + carry;
            carry = result >> 1;
            bitArr[i] = (result & 1) > 0;
        }
    }

    // Returns the epsilon closure of a given state as a set of states that
    // contains the start state.
    private Set<State> computeEpsilonClosure(State start) {
        Set<State> result = new HashSet<>();
        Stack<State> toVisit = new Stack<>();
        toVisit.push(start);

        while(!toVisit.isEmpty()) {
            State state = toVisit.pop();
            result.add(state);

            // Iterate through all transitions
            for(Transition transition : transitions) {
                // If epsilon transition, add end state to queue
                if(transition.matches(state, Transition.EPSILON)) {
                    State end = transition.getEnd();
                    if(!result.contains(end)) {
                        toVisit.push(end);
                    }
                }
            }
        }
        
        return result;
    }

    // Computes all transitions for the resulting DFA.
    private List<Transition> computeTransitions() {
        List<Transition> transitionList = new ArrayList<>();
        Set<State> foundStates = new HashSet<>();
        Queue<State> toExplore = new LinkedList<>();
        toExplore.add(initialState);

        // Each iteration adds all transitions from the given state.
        while(!toExplore.isEmpty()) {
            // Compute the start state by first merging the epsilon closure.
            State start = mergeStates(computeEpsilonClosure(toExplore.poll()));
            Set<String> startStateNames = start.getNames();
            foundStates.add(start);

            // Iterate through all symbols in the alphabet.
            for(String symbol : alphabet) {
                Set<State> endStates = new HashSet<>();
                
                // Iterate through all states in the epsilon closure.
                for(String stateName : startStateNames) {
                    State state = State.of(stateName);
                    
                    // Collect the epsilon closure of all matching transitions
                    for(Transition transition : transitions) {
                        // If transition matches, add the epsilon closure of the end state
                        if(transition.matches(state, symbol)) {
                            endStates.addAll(computeEpsilonClosure(transition.getEnd()));
                        }
                    }
                }

                // Merge all individual end states into a single state
                State endState;
                if(endStates.isEmpty()) {
                    endState = State.EMPTY;
                } else {
                    endState = mergeStates(endStates);
                }

                // If endState has not been explored, add it to the queue
                if(!foundStates.contains(endState)) {
                    foundStates.add(endState);
                    toExplore.add(endState);
                }
                
                // Add a single new Transition for the given symbol
                transitionList.add(new Transition(start, symbol, endState));
            }
        }

        return transitionList;
    }

    // Helpers

    private State mergeStates(Collection<State> states) {
        if(states.size() == 1) {
            return states.iterator().next();
        }

        Set<String> stateNames = new HashSet<>();
        for(State state : states) {
            stateNames.addAll(state.getNames());
        }
        return State.of(stateNames);
    }

    private Set<State> computeAcceptStates(List<Transition> newTransitions) {
        Set<String> acceptStateNames = mergeStates(acceptStates).getNames();
        Set<State> newAcceptStates = new HashSet<>();

        for(Transition transition : newTransitions) {
            State start = transition.getStart();
            State end = transition.getEnd();
            
            if(!newAcceptStates.contains(start)) {
                for(String s : start.getNames()) {
                    if(acceptStateNames.contains(s)) {
                        newAcceptStates.add(start);
                    }
                }   
            }
            
            if(!newAcceptStates.contains(end)) {
                for(String s : end.getNames()) {
                    if(acceptStateNames.contains(s)) {
                        newAcceptStates.add(end);
                    }
                }   
            }
        }

        return newAcceptStates;
    }
}