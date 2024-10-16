package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TodoDslResponse {
    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;


    public TodoDslResponse(Long id, String title, String contents, String weather, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
