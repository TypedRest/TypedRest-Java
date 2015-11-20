package com.oneandone.typedrest;

import java.beans.IntrospectionException;
import static java.beans.Introspector.getBeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;

/**
 * Utility methods for Java beans (classes with getters and setters).
 */
public final class BeanUtils {

    private BeanUtils() {
    }

    /**
     * Lists all properties on a bean type that have a specific annotation on
     * their getter or backing field.
     *
     * @param <TBean> The type of bean to check for properties.
     * @param <TAnnotation> The annotation type to look for.
     * @param beanType The type of bean to check for properties.
     * @param annotationType The annotation type to look for.
     * @return A list of matching properties.
     */
    public static <TBean, TAnnotation extends Annotation> Collection<PropertyDescriptor> getPropertiesWithAnnotation(Class<TBean> beanType, Class<TAnnotation> annotationType) {
        try {
            List<PropertyDescriptor> result = new LinkedList<>();
            for (PropertyDescriptor property : getBeanInfo(beanType).getPropertyDescriptors()) {
                if (property.getReadMethod() != null && property.getReadMethod().getAnnotation(annotationType) != null
                        || isFieldAnnotated(beanType, property.getName(), annotationType)) {
                    result.add(property);
                }
            }
            return result;
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Lists all read and writable properties on a bean type that have a specific annotation on
     * their getter or backing field.
     *
     * @param <TBean> The type of bean to check for properties.
     * @param <TAnnotation> The annotation type to look for.
     * @param beanType The type of bean to check for properties.
     * @param annotationType The annotation type to look for.
     * @return A list of matching properties.
     */
    public static <TBean, TAnnotation extends Annotation> Collection<PropertyDescriptor> getPropertiesWithoutAnnotation(Class<TBean> beanType, Class<TAnnotation> annotationType) {
        try {
            List<PropertyDescriptor> result = new LinkedList<>();
            for (PropertyDescriptor property : getBeanInfo(beanType).getPropertyDescriptors()) {
                if ((property.getReadMethod() == null || property.getReadMethod().getAnnotation(annotationType) == null)
                        && !isFieldAnnotated(beanType, property.getName(), annotationType)) {
                    result.add(property);
                }
            }
            return result;
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <TBean, TAnnotation extends Annotation> boolean isFieldAnnotated(Class<TBean> beanType, String fieldName, Class<TAnnotation> annotationType) {
        Field field = getField(beanType, fieldName, true);
        return (field != null) && (field.getAnnotation(annotationType) != null);
    }
}
