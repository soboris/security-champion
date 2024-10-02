package com.secchamp.chal.controller;

import com.secchamp.chal.entity.Notice;
import com.secchamp.chal.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // Add a notice
    @PostMapping("/add")
    public ResponseEntity<Notice> addNotice(@RequestBody Notice notice) {
        Notice newNotice = noticeService.addNotice(notice.getNoticeContent(), notice.getCreatedBy());
        return ResponseEntity.ok(newNotice);
    }

    // Display all notices (no pagination)
    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }

    // Display notice by ID
    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Long id) {
        Optional<Notice> notice = noticeService.getNoticeById(id);
        return notice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete notice by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNoticeById(@PathVariable Long id) {
        noticeService.deleteNoticeById(id);
        return ResponseEntity.noContent().build();
    }

    // Search notices by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Notice>> searchNotices(@RequestParam String keyword) {
        List<Notice> notices = noticeService.searchNotices(keyword);
        return ResponseEntity.ok(notices);
    }
}
