package com.devo.bim.component;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

@Component
@RequiredArgsConstructor
public class Message {

    private final MessageSource messageSource;

    public String getMessage(String prop) {
        return getMessage(prop, null);
    }

    public String getMessage(String prop, @Nullable Object[] args) {
        if (StringUtils.isEmpty(prop)) return prop;
        try {
            String translatedMessage = messageSource.getMessage(prop, args, LocaleContextHolder.getLocale());
            if (StringUtils.isEmpty(translatedMessage)) return prop;
            return translatedMessage;
        } catch (NoSuchMessageException ex)
        {
            return prop;
        }
    }
}
