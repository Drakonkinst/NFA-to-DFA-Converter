/**
 * Transition class that represents a transition between two states, with
 * a single symbol input. Epsilon is represented as EPS.
 */
public class Transition {
    public static final String EPSILON = "EPS";

    private final State start;
    private final String symbol;
    private final State end;

    public Transition(State start, String symbol, State end) {
        this.start = start;
        this.symbol = symbol;
        this.end = end;
    }

    // Returns true if the start state and input symbol match
    public boolean matches(State from, String input) {
        return from.equals(start) && input.equals(symbol);
    }

    public State getStart() {
        return start;
    }

    public String getSymbol() {
        return symbol;
    }

    public State getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return start + ", " + symbol + " = " + end;
    }
}