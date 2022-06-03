package hello.typeconverter.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class BooleanToIntegerConverter implements Converter<Boolean, Integer> {

    @Override
    public Integer convert(Boolean source) {
        log.info("Boolean To Integer Source = {}", source);
        return source == Boolean.TRUE ? 1 : 0;
    }

}
