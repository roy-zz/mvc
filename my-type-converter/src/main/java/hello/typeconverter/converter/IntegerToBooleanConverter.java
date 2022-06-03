package hello.typeconverter.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {

    @Override
    public Boolean convert(Integer source) {
        log.info("Integer To Boolean Source = {}", source);
        if (source > 1) {
            throw new IllegalArgumentException("1을 초과하는 숫자는 변경이 불가능합니다.");
        } else {
            return source == 1;
        }
    }

}
