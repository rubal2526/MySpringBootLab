package com.rookies4.myspringbootlab.service;

import com.rookies4.myspringbootlab.controller.dto.BookDTO;
import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.entity.BookDetail;
import com.rookies4.myspringbootlab.exception.BusinessException;
import com.rookies4.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDTO.Response> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookDTO.Response::from)
                .toList();
    }

    public BookDTO.Response getBookById(Long id) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException("Book not found with id: " + id, HttpStatus.NOT_FOUND));
        return BookDTO.Response.from(book);
    }

    public BookDTO.Response getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbnWithBookDetail(isbn)
                .orElseThrow(() -> new BusinessException("Book not found with isbn: " + isbn, HttpStatus.NOT_FOUND));
        return BookDTO.Response.from(book);
    }

    public List<BookDTO.Response> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(BookDTO.Response::from)
                .toList();
    }

    public List<BookDTO.Response> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(BookDTO.Response::from)
                .toList();
    }

    @Transactional
    public BookDTO.Response createBook(BookDTO.Request requestDto) {
        // ISBN 중복 검사
        if (bookRepository.existsByIsbn(requestDto.getIsbn())) {
            throw new BusinessException("ISBN already exists: " + requestDto.getIsbn(), HttpStatus.CONFLICT);
        }

        // DTO로부터 Book 엔티티 생성
        Book bookEntity = Book.builder()
                .title(requestDto.getTitle())
                .author(requestDto.getAuthor())
                .isbn(requestDto.getIsbn())
                .price(requestDto.getPrice())
                .publishDate(requestDto.getPublishDate())
                .build();

        // BookDetail 정보가 DTO에 포함된 경우, BookDetail 엔티티 생성 및 연관관계 설정
        if (requestDto.getBookDetail() != null) {
            BookDTO.BookDetailDTO detailDto = requestDto.getBookDetail();
            BookDetail bookDetailEntity = BookDetail.builder()
                    .description(detailDto.getDescription())
                    .language(detailDto.getLanguage())
                    .pageCount(detailDto.getPageCount())
                    .publisher(detailDto.getPublisher())
                    .coverImageUrl(detailDto.getCoverImageUrl())
                    .edition(detailDto.getEdition())
                    .build();
            bookEntity.setBookDetail(bookDetailEntity);
        }

        Book savedBook = bookRepository.save(bookEntity);
        return BookDTO.Response.from(savedBook);
    }

    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request requestDto) {
        // ID로 기존 Book 엔티티 조회
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book not found with id: " + id, HttpStatus.NOT_FOUND));

        // ISBN 변경 시 중복 검사
        if (!existingBook.getIsbn().equals(requestDto.getIsbn()) && bookRepository.existsByIsbn(requestDto.getIsbn())) {
            throw new BusinessException("New ISBN already belongs to another book: " + requestDto.getIsbn(), HttpStatus.CONFLICT);
        }

        // Book 기본 정보 업데이트
        existingBook.setTitle(requestDto.getTitle());
        existingBook.setAuthor(requestDto.getAuthor());
        existingBook.setIsbn(requestDto.getIsbn());
        existingBook.setPrice(requestDto.getPrice());
        existingBook.setPublishDate(requestDto.getPublishDate());

        // BookDetail 정보 업데이트
        if (requestDto.getBookDetail() != null) {
            BookDetail existingDetail = existingBook.getBookDetail();
            BookDTO.BookDetailDTO detailDto = requestDto.getBookDetail();

            // 기존에 상세 정보가 없었다면 새로 생성하여 연결
            if (existingDetail == null) {
                existingDetail = new BookDetail();
                existingBook.setBookDetail(existingDetail);
            }

            // 상세 정보 필드 업데이트
            existingDetail.setDescription(detailDto.getDescription());
            existingDetail.setLanguage(detailDto.getLanguage());
            existingDetail.setPageCount(detailDto.getPageCount());
            existingDetail.setPublisher(detailDto.getPublisher());
            existingDetail.setCoverImageUrl(detailDto.getCoverImageUrl());
            existingDetail.setEdition(detailDto.getEdition());
        }

        Book updatedBook = bookRepository.save(existingBook);
        return BookDTO.Response.from(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        // 삭제할 책이 존재하는지 먼저 확인
        if (!bookRepository.existsById(id)) {
            throw new BusinessException("Book to be deleted not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        bookRepository.deleteById(id);
    }
}