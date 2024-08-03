public record Tuple<T extends Comparable<T>>(T first, T second) {

    public Tuple {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Both elements of a Tuple must be non-null");
        }

        // Ensure elements are ordered
        if (first.compareTo(second) > 0) {
            T temp = first;
            first = second;
            second = temp;
        }
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

}
