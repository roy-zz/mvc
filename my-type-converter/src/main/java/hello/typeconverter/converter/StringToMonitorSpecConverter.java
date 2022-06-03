package hello.typeconverter.converter;

import hello.typeconverter.type.MonitorSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToMonitorSpecConverter implements Converter<String, MonitorSpec> {
    @Override
    public MonitorSpec convert(String source) {
        log.info("String To Monitor Spec Source = {}", source);
        String[] splits = source.split("_");
        String manufacturer = splits[0];
        int inch = Integer.parseInt(splits[1]);
        return new MonitorSpec(manufacturer, inch);
    }
}
