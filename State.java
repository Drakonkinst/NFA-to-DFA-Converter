import java.util.*;

/**
 * State class that represents a state consisting of one or more names.
 * States in the original NFA usually have a single name, when
 * constructing the DFA we may need states that encompass multiple states from
 * the NFA so multiple names are used.
 * 
 * States are cached to ensure that objects are re-used when possible.
 * The empty state is a special constant object.
 */
public class State implements Comparable<State> {
    public static final State EMPTY = new State();
    private static final String EMPTY_NAME = "EM";
    private static final Map<Set<String>, State> cache = new HashMap<>();

    // Static constructor that re-uses State objects if they have a re-used name
    public static State of(Set<String> names) {
        if(names.isEmpty()) {
            throw new IllegalArgumentException("State cannot be empty");
        }

        // If it already exists in the cache, re-use object
        if(cache.containsKey(names)) {
            return cache.get(names);
        }

        // Create new State object and cache it
        State state = new State(names);
        cache.put(names, state);
        return state;
    }

    // Syntatic sugar for creating a State with only one name
    public static State of(String name) {
        Set<String> set = new HashSet<>();
        set.add(name);
        return of(set);
    }

    // Creates the display name of the State. Display name is calculated
    // once and stored since it is a non-trivial operation.
    private static String constructDisplayName(Set<String> names) {
        List<String> nameList = new ArrayList<>(names);
        return '{' + String.join(", ", nameList) + '}';
    }
    
    private final Set<String> names;
    private final String displayName;

    // Normal constructor
    private State(Set<String> names, String displayName) {
        this.names = names;
        this.displayName = displayName;
    }

    // Only specify names, automatically create
    private State(Set<String> names) {
        this(names, constructDisplayName(names));
    }

    // Empty state constructor
    private State() {
        this(Collections.emptySet(), EMPTY_NAME);
    }

    public Set<String> getNames() {
        return names;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // Compare by number of names, tiebreaking by the contents of the names
    @Override
    public int compareTo(State o) {
        int sizeDiff = names.size() - o.getNames().size();
        if(sizeDiff == 0) {
            return toString().compareTo(o.toString());
        }
        return sizeDiff;
    }
}