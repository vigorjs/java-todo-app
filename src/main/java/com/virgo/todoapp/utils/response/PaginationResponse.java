package com.virgo.todoapp.utils.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PaginationResponse<T> {
    private List<T> items;
    private Integer totalItems;
    private Integer currentPage;
    private Integer totalPages;

    public PaginationResponse(Page<T> page) {
        this.items = page.getContent();
        this.totalItems = page.getNumberOfElements();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
