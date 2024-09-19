package ru.practicum.mainservice.model.comment;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.model.event.Event;
import ru.practicum.mainservice.model.user.User;

@Component
public class CommentMapper {

    public Comment toComment(NewCommentDto newCommentDto, Event event, User user) {
        Comment comment = new Comment();
        comment.setEvent(event);
        comment.setUser(user);
        comment.setText(newCommentDto.getText());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                comment.getUser().getId(),
                comment.getPublishedOn().toString()
        );
    }
}
