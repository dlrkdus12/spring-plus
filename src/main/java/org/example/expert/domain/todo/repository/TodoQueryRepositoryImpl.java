package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoDslResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Todo findByIdByDsl(long todoId) {
        return queryFactory
                .selectFrom(todo)
                .where(
                        todoIdEq(todoId))
                .fetchOne();
    }

    @Override
    public Page<TodoDslResponse> search(Pageable pageable) {
        List<Todo> todos = queryFactory
                .select(todo)
                .distinct()
                .from(todo)
                .leftJoin(todo.managers, manager).fetchJoin()
                .leftJoin(todo.comments, comment)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(Wildcard.count) //  select count(*)
                .from(todo)
                .fetchOne();

        List<TodoDslResponse> result = todos.stream()
                .map(todo ->
                        new TodoDslResponse(
                                todo.getId(),
                                todo.getTitle(),
                                todo.getContents(),
                                todo.getWeather(),
                                todo.getCreatedAt(),
                                todo.getModifiedAt()
                        )
                        ).toList();

        return new PageImpl<>(result, pageable, count);
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }
}
