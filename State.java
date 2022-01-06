import java.util.*;

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

    // Creates the display name of the State
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

    public String getDisplayName() {
        return displayName;
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
            return displayName.compareTo(o.getDisplayName());
        }
        return sizeDiff;
    }
}