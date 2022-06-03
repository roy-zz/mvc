package hello.typeconverter.converter;

import hello.typeconverter.type.MonitorSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class MonitorSpecToStringConverter implements Converter<MonitorSpec, String> {
    @Override
    public String convert(MonitorSpec source) {
        log.info("MonitorSpec To String source = {}", source);
        return String.format("%s_%s", source.getManufacturer(), source.getInch());
    }
}
