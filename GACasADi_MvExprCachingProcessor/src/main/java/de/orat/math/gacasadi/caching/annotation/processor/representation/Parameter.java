package de.orat.math.gacasadi.caching.annotation.processor.representation;

import de.orat.math.gacasadi.caching.annotation.processor.GenerateCachedProcessor.Utils;
import de.orat.math.gacasadi.caching.annotation.processor.common.ErrorException;
import javax.lang.model.element.VariableElement;

public final class Parameter {

    public final String type;
    public final String identifier;

    protected Parameter(VariableElement correspondingElement, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments, Utils utils) throws ErrorException {
        this.type = typeParametersToArguments.clearTypeParameterIfPresent(correspondingElement.asType().toString());
        this.identifier = correspondingElement.getSimpleName().toString();

        if (this.type.equals(enclosingClassQualifiedName)) {

        } else if (this.type.equals("int")) {

        } else {
            throw ErrorException.create(correspondingElement,
                "Type of parameter \"%s %s\" was not one of the expected: \"%s\", \"%s\".",
                this.type, this.identifier, enclosingClassQualifiedName, "int");
        }
    }
}
