package com.secchamp.chal.repository;

import com.secchamp.chal.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByNoticeContentContaining(String keyword);
}
