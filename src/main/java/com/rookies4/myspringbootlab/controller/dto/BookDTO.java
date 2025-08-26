package com.rookies4.myspringbootlab.controller.dto; // DTO 패키지에 위치

import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.entity.BookDetail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

public class BookDTO {

    /**
     * 책 생셩 및 수정을 위한 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Author is required")
        private String author;

        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|[0-9]{1,5}(?:-[0-9]+){2,4}-[0-9X]$)([0-9]{13})$", message = "Invalid ISBN format")
        private String isbn;

        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price must be zero or positive")
        private Integer price;

        @NotNull(message = "Publish date is required")
        @PastOrPresent(message = "Publish date cannot be in the future")
        private LocalDate publishDate;

        @Valid // 중첩된 BookDetailDTO의 유효성 검사를 활성화합니다.
        private BookDetailDTO bookDetail;

        public Book toEntity() {
            Book book = Book.builder()
                    .title(this.title)
                    .author(this.author)
                    .isbn(this.isbn)
                    .price(this.price)
                    .publishDate(this.publishDate)
                    .build();

            if (this.bookDetail != null) {
                BookDetail detailEntity = this.bookDetail.toEntity();
                book.setBookDetail(detailEntity); // 양방향 연관관계 설정
            }
            return book;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookDetailDTO {
        private String description;
        private String language;
        @Positive(message = "Page count must be positive")
        private Integer pageCount;
        private String publisher;
        private String coverImageUrl;
        private String edition;

        public BookDetail toEntity() {
            return BookDetail.builder()
                    .description(this.description)
                    .language(this.language)
                    .pageCount(this.pageCount)
                    .publisher(this.publisher)
                    .coverImageUrl(this.coverImageUrl)
                    .edition(this.edition)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private Integer price;
        private LocalDate publishDate;
        private BookDetailResponse bookDetail;

        public static Response from(Book book) {
            BookDetailResponse detailResponse = null;
            // Book에 연결된 BookDetail이 있을 경우에만 DTO로 변환합니다.
            if (book.getBookDetail() != null) {
                detailResponse = BookDetailResponse.from(book.getBookDetail());
            }

            return Response.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .price(book.getPrice())
                    .publishDate(book.getPublishDate())
                    .bookDetail(detailResponse)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookDetailResponse {
        private Long id;
        private String description;
        private String language;
        private Integer pageCount;
        private String publisher;
        private String coverImageUrl;
        private String edition;

        public static BookDetailResponse from(BookDetail bookDetail) {
            return BookDetailResponse.builder()
                    .id(bookDetail.getId())
                    .description(bookDetail.getDescription())
                    .language(bookDetail.getLanguage())
                    .pageCount(bookDetail.getPageCount())
                    .publisher(bookDetail.getPublisher())
                    .coverImageUrl(bookDetail.getCoverImageUrl())
                    .edition(bookDetail.getEdition())
                    .build();
        }
    }
}