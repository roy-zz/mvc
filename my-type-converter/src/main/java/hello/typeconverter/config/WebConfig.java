package hello.typeconverter.config;

import hello.typeconverter.converter.BooleanToIntegerConverter;
import hello.typeconverter.converter.IntegerToBooleanConverter;
import hello.typeconverter.converter.MonitorSpecToStringConverter;
import hello.typeconverter.converter.StringToMonitorSpecConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new IntegerToBooleanConverter());
        registry.addConverter(new BooleanToIntegerConverter());
        registry.addConverter(new StringToMonitorSpecConverter());
        registry.addConverter(new MonitorSpecToStringConverter());
    }
}
