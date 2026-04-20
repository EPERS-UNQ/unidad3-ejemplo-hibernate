package ar.edu.unq.unidad3.modelo;

public record Pair<A, B>(A first, B second) {
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}