package org.example.dogs.domain.query;

public record DogPageRequest(
        int page,
        int size
) {
    public DogPageRequest {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
    }

    public static DogPageRequest of(int page, int size) {
        return new DogPageRequest(page, size);
    }
}
