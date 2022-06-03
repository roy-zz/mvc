package hello.typeconverter;

import static org.assertj.core.api.Assertions.assertThat;

import hello.typeconverter.formatter.MoneyFormatter;
import java.text.ParseException;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class MoneyFormatterTest {
    private final MoneyFormatter formatter = new MoneyFormatter();

    @Test
    void parseTest() throws ParseException {
        Number result = formatter.parse("1,000", Locale.KOREA);
        assertThat(result).isEqualTo(1000L);
    }

    @Test
    void printTest() {
        String result = formatter.print(1000, Locale.KOREA);
        assertThat(result).isEqualTo("1,000");
    }

}
