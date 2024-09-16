package ru.practicum.mainservice.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.model.comment.CommentDto;
import ru.practicum.mainservice.model.comment.NewCommentDto;
import ru.practicum.mainservice.model.comment.UpdateCommentDto;
import ru.practicum.mainservice.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PrivateCommentsController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllEventComments(@PathVariable Long eventId) {
        return commentService.getAllEventComments(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody @Valid NewCommentDto newCommentDto, @PathVariable Long eventId,
                                    @RequestParam Long userId) {
        return commentService.createComment(eventId, userId, newCommentDto);
    }

    @PatchMapping("{commentId}")
    public CommentDto updateComment(@RequestBody UpdateCommentDto updateCommentRequest, @PathVariable Long eventId,
                                    @PathVariable Long commentId, @RequestParam Long userId) {
        return commentService.updateComment(eventId, userId, commentId, updateCommentRequest);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId, @PathVariable Long commentId, @RequestParam Long userId) {
        commentService.deleteComment(eventId, userId, commentId);
    }
}
