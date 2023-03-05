package ru.practicum.ewm.stats.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

@Configuration
public class EwmStatsAppConfig implements WebMvcConfigurer {
    private final String localDateFormat;
    private final String localDateTimeFormat;

    @Autowired
    public EwmStatsAppConfig(@Value("${ewm-stats-default-date-format}") String localDateFormat,
                             @Value("${ewm-stats-default-date-time-format}") String localDateTimeFormat) {
        this.localDateFormat = localDateFormat;
        this.localDateTimeFormat = localDateTimeFormat;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {

        return builder -> {
            builder.simpleDateFormat(localDateTimeFormat);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(localDateFormat)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(localDateTimeFormat)));
            builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(localDateFormat)));
            builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(localDateTimeFormat)));
        };
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(localDateFormat));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(localDateTimeFormat));
        registrar.registerFormatters(registry);
    }
}