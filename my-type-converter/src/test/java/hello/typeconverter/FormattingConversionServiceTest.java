package hello.typeconverter;

import static org.assertj.core.api.Assertions.assertThat;

import hello.typeconverter.converter.MonitorSpecToStringConverter;
import hello.typeconverter.converter.StringToMonitorSpecConverter;
import hello.typeconverter.formatter.MoneyFormatter;
import hello.typeconverter.type.MonitorSpec;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;

class FormattingConversionServiceTest {

    @Test
    void formattingConversionServiceTest() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();

        conversionService.addConverter(new StringToMonitorSpecConverter());
        conversionService.addConverter(new MonitorSpecToStringConverter());

        conversionService.addFormatter(new MoneyFormatter());

        MonitorSpec monitorSpec = conversionService.convert("Samsung_27", MonitorSpec.class);
        assertThat(monitorSpec).isEqualTo(new MonitorSpec("Samsung", 27));

        assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");
        assertThat(conversionService.convert("1,000", Long.class)).isEqualTo(1000L);
    }

}
