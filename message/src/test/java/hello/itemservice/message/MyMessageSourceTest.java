package hello.itemservice.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MyMessageSourceTest {
    @Autowired
    private MessageSource messageSource;

    @Test
    void getName() {
        String name = messageSource.getMessage("name", null, null);
        assertEquals("로이", name);
    }

    @Test
    void getNotExistValue() {
        assertThrows(NoSuchMessageException.class, () -> {
            messageSource.getMessage("not_exist_value", null, null);
        });
    }

    @Test
    void getNotExistValueWithDefaultValue() {
        String defaultMessage = messageSource.getMessage("not_exist_value", null, "기본 이름", null);
        assertEquals("기본 이름", defaultMessage);
    }

    @Test
    void defaultLanguage() {
        String defaultLocale = messageSource.getMessage("name", null, null);
        String krLocale = messageSource.getMessage("name", null, Locale.KOREA);
        assertEquals("로이", defaultLocale);
        assertEquals("로이", krLocale);
    }

    @Test
    void englishLanguage() {
        String enLocale = messageSource.getMessage("name", null, Locale.ENGLISH);
        assertEquals("roy", enLocale);
    }

}
