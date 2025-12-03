package de.orat.math.gacasadi.caching.annotation.processor.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class ExceptionHandler {

    // If set, ignores showWarnings.
    static protected final boolean IS_DEBUG = false;

    protected final Messager messager;
    protected final boolean warnFailedToCache;
    protected final boolean warnUncached;

    public ExceptionHandler(Messager messager) {
        this.messager = messager;
        this.warnFailedToCache = true;
        this.warnUncached = false;
    }

    public ExceptionHandler(ExceptionHandler handler, boolean warnFailedToCache, boolean warnUncached) {
        this.messager = handler.messager;
        this.warnFailedToCache = warnFailedToCache;
        this.warnUncached = warnUncached;
    }

    public interface Executable {

        void execute() throws ErrorException, FailedToCacheException, UncachedException, Exception;
    }

    /**
     * If used within a for loop, it allows to report multiple errors.
     */
    public void handle(Executable executable) {
        try {
            executable.execute();
        } catch (ErrorException ex) {
            if (ExceptionHandler.IS_DEBUG) {
                String message = extractStackTrace(ex);
                error(ex.element, message);
                // Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            } else {
                error(ex.element, ex.getMessage());
            }
        } catch (FailedToCacheException ex) {
            if (ExceptionHandler.IS_DEBUG) {
                String message = extractStackTrace(ex);
                warn(ex.element, message);
            } else if (this.warnFailedToCache) {
                warn(ex.element, ex.getMessage());
            }
        } catch (UncachedException ex) {
            if (ExceptionHandler.IS_DEBUG) {
                String message = extractStackTrace(ex);
                warn(ex.element, message);
            } else if (this.warnUncached) {
                warn(ex.element, ex.getMessage());
            }
        } catch (Exception ex) {
            String message = extractStackTrace(ex);
            error(null, message);
        }
    }

    protected static String extractStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }

    protected void error(Element e, String message, Object... args) {
        this.messager.printMessage(
            Diagnostic.Kind.ERROR,
            String.format("[Cache] " + message, args),
            e);
    }

    protected void warn(Element e, String message, Object... args) {
        this.messager.printMessage(
            Diagnostic.Kind.MANDATORY_WARNING,
            String.format("[Cache] " + message, args),
            e);
    }
}
