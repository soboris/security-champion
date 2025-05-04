package com.secchamp.chal.service;

import com.secchamp.chal.entity.Notice;
import com.secchamp.chal.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    public Notice addNotice(String content, String createdBy) {
        Notice notice = new Notice();
        notice.setNoticeContent(content);
        notice.setCreatedBy(createdBy);
        notice.setCreatedDate(LocalDateTime.now());
        return noticeRepository.save(notice);
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }

    public void deleteNoticeById(Long id) {
        noticeRepository.deleteById(id);
    }

    public List<Notice> searchNotices(String keyword) {
        return noticeRepository.findByNoticeContentContaining(keyword);
    }
}
