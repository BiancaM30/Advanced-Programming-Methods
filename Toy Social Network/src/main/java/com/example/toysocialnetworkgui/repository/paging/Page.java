package com.example.toysocialnetworkgui.repository.paging;

import java.util.stream.Stream;

public interface Page<E> {
    Pageable getPageable();

    Pageable nextPageable();

    Stream<E> getContent();


}
