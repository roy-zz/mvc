package hello.typeconverter;

import static org.assertj.core.api.Assertions.assertThat;

import hello.typeconverter.converter.BooleanToIntegerConverter;
import hello.typeconverter.converter.IntegerToBooleanConverter;
import hello.typeconverter.converter.MonitorSpecToStringConverter;
import hello.typeconverter.converter.StringToMonitorSpecConverter;
import hello.typeconverter.type.MonitorSpec;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

public class ConversionServiceTest {

    @Test
    void conversionServiceTest() {

        // 등록
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new IntegerToBooleanConverter());
        conversionService.addConverter(new BooleanToIntegerConverter());
        conversionService.addConverter(new MonitorSpecToStringConverter());
        conversionService.addConverter(new StringToMonitorSpecConverter());

        // 사용
        assertThat(conversionService.convert(1, Boolean.class)).isEqualTo(Boolean.TRUE);
        assertThat(conversionService.convert(Boolean.TRUE, Integer.class)).isEqualTo(1);

        MonitorSpec monitorSpec = conversionService.convert("Samsung_27", MonitorSpec.class);
        assertThat(monitorSpec).isEqualTo(new MonitorSpec("Samsung", 27));

        String monitorSpecString = conversionService.convert(new MonitorSpec("LG", 32), String.class);
        assertThat(monitorSpecString).isEqualTo("LG_32");

    }
}
