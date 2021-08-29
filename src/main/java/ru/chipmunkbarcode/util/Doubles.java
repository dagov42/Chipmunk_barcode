package ru.chipmunkbarcode.util;

public final class Doubles {
    private Doubles() {
        // utility class
    }

    /**
     * It's usually not a good idea to check floating point numbers for exact equality. This method allows us to check for
     * approximate equality.
     *
     * @param d1 the first double
     * @param d2 the second double
     * @return whether or not the two doubles are approximately equal (to within 0.0001)
     */
    public static boolean roughlyEqual(double d1, double d2) {
        return Math.abs(d1 - d2) < 0.0001;
    }
}
