package com.virgo.todoapp.utils.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class UserPaginationResponse<T> {
    private List<T> users;
    private Integer totalItems;
    private Integer currentPage;
    private Integer totalPages;

    public UserPaginationResponse(Page<T> page) {
        this.users = page.getContent();
        this.totalItems = page.getNumberOfElements();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
    }
}
