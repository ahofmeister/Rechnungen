package de.alexanderhofmeister.rechnungen.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public final class ClassUtil {

    private ClassUtil() {
        // Helper class
    }

    private static <E> Type getActualTypeArgument(final Class<? extends E> instanceClass, final Class<E> superClass,
                                                  final int argumentIndex) {
        final List<Type> parents = new ArrayList<>();
        parents.add(instanceClass.getGenericSuperclass());
        parents.addAll(Arrays.asList(instanceClass.getGenericInterfaces()));
        for (final Type parentType : parents) {
            final Class<?> parentClass = parentType instanceof ParameterizedType
                    ? (Class<?>) ((ParameterizedType) parentType).getRawType() : (Class<?>) parentType;

            // First check if we found the super class or interface
            if (superClass.equals(parentClass)) {
                // We found the requested super class - use the binding
                return ((ParameterizedType) parentType).getActualTypeArguments()[argumentIndex];
            } else if (parentClass != null && superClass.isAssignableFrom(parentClass)) {
                // Else step up
                final Type type = getActualTypeArgument(parentClass.asSubclass(superClass), superClass, argumentIndex);
                if (type instanceof Class) {
                    return type;
                } else if (type instanceof TypeVariable) {
                    return ((ParameterizedType) parentType).getActualTypeArguments()[Arrays
                            .asList(parentClass.getTypeParameters()).indexOf(type)];
                }
            }
        }

        return null;
    }

    /**
     * Resolves the actual binding of a generic type that was specified in a
     * superclass and bound in a subclass.
     *
     * @param instanceClass the implementing class
     * @param superClass    the superclass or interface which specifies the generic type
     *                      variable
     * @param argumentIndex the index of the type variable in the superclass definition (0
     *                      = the first type variable)
     * @return the bound class for the variable in instanceClass
     */
    @SuppressWarnings("unchecked")
    public static <T, E> Class<T> getActualTypeBinding(final Class<? extends E> instanceClass,
                                                       final Class<E> superClass, final int argumentIndex) {
        Type type = getActualTypeArgument(instanceClass, superClass, argumentIndex);
        if (type instanceof TypeVariable<?>) {
            type = ((TypeVariable<?>) type).getBounds()[0];
        }
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        if (type instanceof Class<?>) {
            return (Class<T>) type;
        }
        throw new NoSuchElementException(
                "Can't find binding for the " + argumentIndex + ". argument of " + superClass + " in " + instanceClass);
    }

}
