package com.rookies4.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // @Builder 어노테이션을 사용하려면 모든 필드를 포함한 생성자가 필요합니다.
@Builder          // 테스트 코드에서 사용된 빌더 패턴을 적용합니다.
@ToString(exclude = "bookDetail")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private LocalDate publishDate;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BookDetail bookDetail;

    public void setBookDetail(BookDetail bookDetail) {
        this.bookDetail = bookDetail;
        if (bookDetail != null) {
            bookDetail.setBook(this);
        }
    }
}