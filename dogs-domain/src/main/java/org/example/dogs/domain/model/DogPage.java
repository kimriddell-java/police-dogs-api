package org.example.dogs.domain.model;

import java.util.Collections;
import java.util.List;

public record DogPage(
        List<Dog> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public DogPage {
        if (content == null) {
            content = Collections.emptyList();
        } else {
            content = List.copyOf(content);
        }
    }

    public static DogPage of(List<Dog> content, DogPageRequest pageRequest, long totalElements) {
        int page = pageRequest.page();
        int size = pageRequest.size();
        int totalPages = size > 0 && totalElements > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new DogPage(content, page, size, totalElements, totalPages);
    }
}
