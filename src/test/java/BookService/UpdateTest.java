package BookService;

import com.example.demo.dto.book.UpdateBookDto;
import com.example.demo.entity.BookEntity;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class UpdateTest {
    @InjectMocks
    private BookService bookService;
    @Mock
    private BookRepository bookRepository;

    @Test
    void testUpdateBookSuccess() {
        BookEntity book = new BookEntity();
        book.setIsbn("1");
        book.setName("test");

        BookEntity updatedBook = new BookEntity();
        book.setIsbn("1");
        book.setName("new");

        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(null);
        UpdateBookDto name = new UpdateBookDto("new");
        BookEntity result = bookService.update("1", name);

        //assertNotNull(result);
        assertEquals("new", result.getName());
        verify(bookRepository, times(1)).findById("1");
        verify(bookRepository, times(1)).save(any(BookEntity.class));
    }


}
