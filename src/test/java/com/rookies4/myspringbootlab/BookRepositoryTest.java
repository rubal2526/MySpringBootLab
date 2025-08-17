package com.rookies4.myspringbootlab;

import com.rookies4.myspringbootlab.Entity.Book;
import com.rookies4.myspringbootlab.Repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("도서 등록 테스트")
    void testCreateBook() {
        Book newBook = Book.builder()
                .title("스프링 부트 입문")
                .author("홍길동")
                .isbn("9788956746425")
                .publishDate(LocalDate.of(2025, 5, 7))
                .price(30000)
                .build();

        Book savedBook = bookRepository.save(newBook);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("스프링 부트 입문");
    }

    @Test
    @DisplayName("ISBN으로 도서 조회 테스트")
    void testFindByIsbn() {
        String isbn = "9788956746425";
        Book bookToSave = Book.builder()
                .title("스프링 부트 입문")
                .author("홍길동")
                .isbn(isbn)
                .publishDate(LocalDate.of(2025, 5, 7))
                .price(30000)
                .build();
        bookRepository.save(bookToSave);

        Optional<Book> foundBook = bookRepository.findByIsbn(isbn);

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getIsbn()).isEqualTo(isbn);
    }

    @Test
    @DisplayName("저자명으로 도서 목록 조회 테스트")
    void testFindByAuthor() {
        String author = "홍길동";
        Book book1 = Book.builder().title("스프링 부트 입문").author(author).isbn("1111").price(30000).build();
        Book book2 = Book.builder().title("자바의 정석").author(author).isbn("2222").price(28000).build();
        Book book3 = Book.builder().title("JPA 프로그래밍").author("박둘리").isbn("3333").price(35000).build();
        bookRepository.saveAll(List.of(book1, book2, book3));

        List<Book> booksByAuthor = bookRepository.findByAuthor(author);

        assertThat(booksByAuthor).isNotNull();
        assertThat(booksByAuthor.size()).isEqualTo(2);
        assertThat(booksByAuthor).extracting(Book::getAuthor).containsOnly(author);
    }

    @Test
    @DisplayName("도서 정보 수정 테스트")
    void testUpdateBook() {
        Book bookToSave = Book.builder().title("구 스프링 부트").author("옛날 저자").isbn("12345").price(25000).build();
        Book savedBook = bookRepository.save(bookToSave);

        String updatedTitle = "신 스프링 부트";
        savedBook.updateBookInfo(updatedTitle, "최신 저자", 32000);

        Optional<Book> updatedBook = bookRepository.findById(savedBook.getId());
        assertThat(updatedBook).isPresent();
        assertThat(updatedBook.get().getTitle()).isEqualTo(updatedTitle);
    }

    @Test
    @DisplayName("도서 삭제 테스트")
    void testDeleteBook() {
        Book bookToSave = Book.builder().title("삭제될 책").author("아무개").isbn("9999").price(10000).build();
        Book savedBook = bookRepository.save(bookToSave);
        Long bookId = savedBook.getId();

        bookRepository.deleteById(bookId);

        Optional<Book> deletedBook = bookRepository.findById(bookId);
        assertThat(deletedBook).isEmpty();
    }
}
