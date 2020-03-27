package net.polarizedions.annoucerbot.utils;

import java.util.Objects;

public class Pair<One, Two> {
    public One one;
    public Two two;

    public Pair(One one, Two two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "one=" + one +
                ", two=" + two +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(one, pair.one) &&
                Objects.equals(two, pair.two);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two);
    }
}
