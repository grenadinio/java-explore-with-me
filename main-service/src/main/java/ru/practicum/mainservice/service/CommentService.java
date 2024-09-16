package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotAllowedException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.comment.Comment;
import ru.practicum.mainservice.model.comment.CommentDto;
import ru.practicum.mainservice.model.comment.CommentMapper;
import ru.practicum.mainservice.model.comment.NewCommentDto;
import ru.practicum.mainservice.model.comment.UpdateCommentDto;
import ru.practicum.mainservice.model.event.Event;
import ru.practicum.mainservice.model.user.User;
import ru.practicum.mainservice.repository.CommentsRepository;
import ru.practicum.mainservice.repository.EventsRepository;
import ru.practicum.mainservice.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentsRepository commentsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getAllEventComments(Long eventId) {
        return commentsRepository.getAllByEventId(eventId)
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    public CommentDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto) {
        Event event = validateAndGetEventById(eventId);
        User user = validateAndGetUserById(userId);
        Comment comment = commentMapper.toComment(newCommentDto, event, user);
        comment.setPublishedOn(LocalDateTime.now());
        return commentMapper.toCommentDto(commentsRepository.save(comment));
    }

    public CommentDto updateComment(Long eventId, Long userId, Long commentId, UpdateCommentDto updateCommentRequest) {
        validateAndGetEventById(eventId);
        validateAndGetUserById(userId);
        Comment comment = validateAndGetCommentById(commentId);
        if (!Objects.equals(eventId, comment.getEvent().getId())) {
            throw new ConflictException("Комментарий с id = " + commentId + " не относится к ивенту с id = " + eventId);
        }
        if (!Objects.equals(userId, comment.getUser().getId())) {
            throw new NotAllowedException("Вы не можете изменять чужой комментарий");
        }
        comment.setText(updateCommentRequest.getText());
        return commentMapper.toCommentDto(commentsRepository.save(comment));
    }

    public void deleteComment(Long eventId, Long userId, Long commentId) {
        validateAndGetEventById(eventId);
        validateAndGetUserById(userId);
        Comment comment = validateAndGetCommentById(commentId);
        if (!Objects.equals(eventId, comment.getEvent().getId())) {
            throw new ConflictException("Комментарий с id = " + commentId + " не относится к ивенту с id = " + eventId);
        }
        if (!Objects.equals(userId, comment.getUser().getId())) {
            throw new NotAllowedException("Вы не можете удалить чужой комментарий");
        }
        commentsRepository.delete(comment);
    }

    public CommentDto updateCommentAdmin(Long eventId, Long commentId, UpdateCommentDto updateCommentRequest) {
        validateAndGetEventById(eventId);
        Comment comment = validateAndGetCommentById(commentId);
        if (!Objects.equals(eventId, comment.getEvent().getId())) {
            throw new ConflictException("Комментарий с id = " + commentId + " не относится к ивенту с id = " + eventId);
        }
        comment.setText(updateCommentRequest.getText());
        return commentMapper.toCommentDto(commentsRepository.save(comment));
    }

    public void deleteCommentAdmin(Long eventId, Long commentId) {
        validateAndGetEventById(eventId);
        Comment comment = validateAndGetCommentById(commentId);
        if (!Objects.equals(eventId, comment.getEvent().getId())) {
            throw new ConflictException("Комментарий с id = " + commentId + " не относится к ивенту с id = " + eventId);
        }
        commentsRepository.delete(comment);
    }

    private Event validateAndGetEventById(Long eventId) {
        Optional<Event> event = eventsRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new NotFoundException("Event with id = " + eventId + " was not found");
        }
        return event.get();
    }

    private User validateAndGetUserById(Long userId) {
        Optional<User> user = usersRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id = " + userId + " was not found");
        }
        return user.get();
    }

    private Comment validateAndGetCommentById(Long commentId) {
        Optional<Comment> comment = commentsRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new NotFoundException("Comment with id = " + commentId + " was not found");
        }

        return comment.get();
    }
}
