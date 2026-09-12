package org.example.dogs.domain.query;

public record DogFilter(
        String name,
        String breed,
        String supplier
) {
    public DogFilter() {
        this(null, null, null);
    }
}
