package io.github.bucheapp.roost.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {
	@Autowired
	private MessageSource messageSource;
	
	public String get(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}
	
	public String get(String key, Object... args) {
		return messageSource.getMessage(key, args, Locale.getDefault());
	}
}
