package de.orat.math.gacasadi.caching.annotation.processor.common;

import javax.lang.model.element.Element;

public class UncachedException extends Exception {

    public final Element element;

    protected UncachedException(Element element, String message, Object... args) {
        super(String.format(message, args));
        this.element = element;
    }

    protected UncachedException(Element element, Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
        this.element = element;
    }

    public static UncachedException create(Element element, String message, Object... args) {
        return new UncachedException(element, message, args);
    }

    public static UncachedException create(Element element, Throwable cause, String message, Object... args) {
        return new UncachedException(element, cause, message, args);
    }
}
