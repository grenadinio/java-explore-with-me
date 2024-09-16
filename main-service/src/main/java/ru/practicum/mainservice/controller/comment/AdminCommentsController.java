package ru.practicum.mainservice.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.model.comment.CommentDto;
import ru.practicum.mainservice.model.comment.UpdateCommentDto;
import ru.practicum.mainservice.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentsController {
    private final CommentService commentService;

    @PatchMapping("{commentId}")
    public CommentDto updateComment(@RequestBody UpdateCommentDto updateCommentRequest, @PathVariable Long eventId,
                                    @PathVariable Long commentId) {
        return commentService.updateCommentAdmin(eventId, commentId, updateCommentRequest);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId, @PathVariable Long commentId) {
        commentService.deleteCommentAdmin(eventId, commentId);
    }
}
