package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );

        newTodo.addManager(user);

        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(LocalDateTime startDate, LocalDateTime endDate, String weather, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        // 날씨 검색만 요청된 경우
        if (weather != null && startDate == null && endDate == null) {
            return mapToTodoResponse(todoRepository.findAllByWeatherOrderByModifiedAtDesc(weather, pageable));
        }

        // 수정일 기준 기간 검색이 요청된 경우
        if (startDate != null || endDate != null) {
            return mapToTodoResponse(todoRepository.findAllByModifiedAtBetween(startDate, endDate, pageable));
        }

        // 날씨나 기간 검색 조건이 없는 경우 예외 처리
        throw new IllegalArgumentException("검색 조건을 입력해야 합니다. 날씨 또는 날짜 범위를 지정해 주세요.");
    }

    // Todo를 TodoResponse로 매핑하는 메서드(메서드 참조)
    private Page<TodoResponse> mapToTodoResponse(Page<Todo> todos) {
        return todos.map(this::mapToTodoResponse);
    }

    // 단일 Todo를 TodoResponse로 매핑하는 메서드
    private TodoResponse mapToTodoResponse(Todo todo) {
        User user = todo.getUser();
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }
}
