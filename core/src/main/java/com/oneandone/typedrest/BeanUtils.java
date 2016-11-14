package com.oneandone.typedrest;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import static java.util.Arrays.stream;
import lombok.SneakyThrows;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;

/**
 * Utility methods for Java beans (classes with getters and setters).
 */
public final class BeanUtils {

    private BeanUtils() {
    }

    /**
     * Lists all properties on a bean type. Ensures properties annotated with
     * {@link Id} or called "name" are always listed first.
     *
     * @param beanType The type of bean to check for properties.
     * @return A list of properties.
     */
    @SneakyThrows
    public static List<PropertyDescriptor> getProperties(Class<?> beanType) {
        LinkedList<PropertyDescriptor> properties = new LinkedList<>();
        for (PropertyDescriptor property : Introspector.getBeanInfo(beanType).getPropertyDescriptors()) {
            if (getAnnotation(beanType, property, Id.class).isPresent() || property.getName().equals("name")) {
                properties.addFirst(property);
            } else {
                properties.add(property);
            }
        }
        return properties;
    }

    /**
     * Lists all properties on a bean type that have a specific annotation on
     * their getter or backing field.
     *
     * @param <TAnnotation> The annotation type to look for.
     * @param beanType The type of bean to check for properties.
     * @param annotationType The annotation type to look for.
     * @return A list of matching properties.
     */
    public static <TAnnotation extends Annotation> List<PropertyDescriptor> getPropertiesWithAnnotation(Class<?> beanType, Class<TAnnotation> annotationType) {
        LinkedList<PropertyDescriptor> result = new LinkedList<>();
        getProperties(beanType).forEach(property -> {
            if (property.getReadMethod() != null && property.getReadMethod().getAnnotation(annotationType) != null
                    || isFieldAnnotated(beanType, property.getName(), annotationType)) {
                result.add(property);
            }
        });
        return result;
    }

    /**
     * Lists all readable and writable properties on a bean type that do not
     * have a specific annotation on their getter or backing field.
     *
     * @param <TAnnotation> The annotation type to look for.
     * @param beanType The type of bean to check for properties.
     * @param annotationType The annotation type to look for.
     * @return A list of matching properties.
     */
    public static <TAnnotation extends Annotation> List<PropertyDescriptor> getPropertiesWithoutAnnotation(Class<?> beanType, Class<TAnnotation> annotationType) {
        LinkedList<PropertyDescriptor> result = new LinkedList<>();
        getProperties(beanType).forEach(property -> {
            if ((property.getReadMethod() == null || property.getReadMethod().getAnnotation(annotationType) == null)
                    && !isFieldAnnotated(beanType, property.getName(), annotationType)) {
                result.add(property);
            }
        });
        return result;
    }

    private static <TAnnotation extends Annotation> boolean isFieldAnnotated(Class<?> beanType, String fieldName, Class<TAnnotation> annotationType) {
        Field field = getField(beanType, fieldName, true);
        return (field != null) && (field.getAnnotation(annotationType) != null);
    }

    /**
     * Returns an annotation of a specific type on a property's getter or its
     * backing field.
     *
     * @param <TAnnotation> The type of annotation to look for.
     * @param beanType The type of bean the property is declared on.
     * @param property The property to check for the annotation.
     * @param annotationType The type of annotation to look for.
     * @return The annotation if present.
     */
    public static <TAnnotation extends Annotation> Optional<TAnnotation> getAnnotation(Class<?> beanType, PropertyDescriptor property, Class<TAnnotation> annotationType) {
        Optional<TAnnotation> annotation = stream(property.getReadMethod().getAnnotationsByType(annotationType)).findAny();
        return annotation.isPresent()
                ? annotation
                : getAnnotationOnField(beanType, property.getName(), annotationType);
    }

    private static <TAnnotation extends Annotation> Optional<TAnnotation> getAnnotationOnField(Class<?> beanType, String fieldName, Class<TAnnotation> annotationType) {
        Field field = getField(beanType, fieldName, true);
        TAnnotation annotation = (field == null) ? null : field.getAnnotation(annotationType);
        return Optional.ofNullable(annotation);
    }
}
