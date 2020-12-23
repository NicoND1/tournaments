package de.bytemc.tournaments.api;

/**
 * @author Nico_ND1
 */
public final class BooleanResult {

    public static final BooleanResult TRUE = new BooleanResult(true);
    public static final BooleanResult FALSE = new BooleanResult(false);

    public static BooleanResult from(boolean result) {
        return new BooleanResult(result);
    }

    private final boolean result;

    public BooleanResult(boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }
}
