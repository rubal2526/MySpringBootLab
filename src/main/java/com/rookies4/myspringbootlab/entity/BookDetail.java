package com.rookies4.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // @Builder 어노테이션을 사용하려면 모든 필드를 포함한 생성자가 필요합니다.
@Builder          // 테스트 코드에서 사용된 빌더 패턴을 적용합니다.
@ToString(exclude = "book")
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_detail_id")
    private Long id;

    private String description;
    private String language;

    // 테스트 코드의 'pageCount' 필드와 일치시키기 위해 'pages'에서 수정합니다.
    private Integer pageCount;

    private String publisher;
    private String coverImageUrl;
    private String edition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true, nullable = false)
    private Book book;
}