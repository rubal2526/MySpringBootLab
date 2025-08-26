package com.rookies4.myspringbootlab.repository; // 다른 파일들과 동일한 패키지로 맞춥니다.

import com.rookies4.myspringbootlab.entity.BookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookDetailRepository extends JpaRepository<BookDetail, Long> {

    Optional<BookDetail> findByBookId(Long bookId);

    @Query("SELECT bd FROM BookDetail bd LEFT JOIN FETCH bd.book WHERE bd.id = :id")
    Optional<BookDetail> findByIdWithBook(@Param("id") Long bookDetailId);

    List<BookDetail> findByPublisherContainingIgnoreCase(String publisher);
}