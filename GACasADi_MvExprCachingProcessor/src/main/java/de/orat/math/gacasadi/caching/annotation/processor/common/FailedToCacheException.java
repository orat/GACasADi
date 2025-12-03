package de.orat.math.gacasadi.caching.annotation.processor.common;

import javax.lang.model.element.Element;

public class FailedToCacheException extends Exception {

    public final Element element;

    protected FailedToCacheException(Element element, String message, Object... args) {
        super(String.format(message, args));
        this.element = element;
    }

    protected FailedToCacheException(Element element, Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
        this.element = element;
    }

    public static FailedToCacheException create(Element element, String message, Object... args) {
        return new FailedToCacheException(element, message, args);
    }

    public static FailedToCacheException create(Element element, Throwable cause, String message, Object... args) {
        return new FailedToCacheException(element, cause, message, args);
    }
}
