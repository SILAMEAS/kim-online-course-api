package com.sila.share.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface GetClassType {
    /**
     * To get a class type of parameters.
     *
     * @param parameterTypeIndex refers to the parameter type index
     * @return the {@link Type} of the parameter
     */
    default Type getClassType(int parameterTypeIndex) {
        // Traverse the class hierarchy to find the parameterized superclass
        var superClass = getClass().getGenericSuperclass();

        while (superClass instanceof Class<?>) {
            superClass = ((Class<?>) superClass).getGenericSuperclass();
        }

        if (superClass instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getActualTypeArguments()[parameterTypeIndex];
        }

        throw new IllegalArgumentException("Superclass is not parameterized");
    }
}
