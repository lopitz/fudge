package com.lolplane.fudge.generators;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import lombok.experimental.UtilityClass;

@UtilityClass
class AnnotationExtractor {
    public static <T extends Annotation> T extractAnnotationOfTypeFromAnyMethodInClass(Class<T> annotationClass, Class<?> targetClass) {
        return Arrays
            .stream(targetClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(annotationClass))
            .map(method -> method.getAnnotation(annotationClass))
            .findFirst()
            .orElseThrow();
    }
}
