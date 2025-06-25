package dev.ckateptb.reflection.type;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.api.AnnotationHolder;
import dev.ckateptb.reflection.api.ModifierHolder;
import dev.ckateptb.reflection.constructor.IReflectConstructor;
import dev.ckateptb.reflection.field.IReflectField;
import dev.ckateptb.reflection.method.IReflectMethod;
import dev.ckateptb.reflection.parameter.ReflectParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Reflective representation of a class or instance, providing fluent
 * APIs for inspecting fields, methods, constructors, annotations,
 * and modifiers, as well as utility mapping functions.
 *
 * @param <T> the underlying type represented
 */
public interface IReflectClass<T> extends ModifierHolder, AnnotationHolder {
    /**
     * Retrieves all fields matching the given predicate.
     *
     * @param filter predicate to apply to each field
     * @return a collection of {@link IReflectField} instances satisfying the filter
     */
    Collection<IReflectField<?>> getFieldsByFilter(Predicate<IReflectField<?>> filter);

    /**
     * Retrieves fields annotated with the specified annotation type.
     *
     * @param annotation the annotation to look for
     * @return fields bearing the given annotation
     */
    default Collection<IReflectField<?>> getFieldsWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getFieldsByFilter(field -> field.isAnnotationPresent(annotation));
    }

    /**
     * Retrieves fields whose type is assignable from the given reflective class.
     *
     * @param <R>  the target field type
     * @param type the {@link IReflectClass} representing the desired field type
     * @return fields of the specified type
     */
    default <R> Collection<IReflectField<R>> getFieldsWithType(IReflectClass<R> type) {
        return this.getFieldsWithType(type.getType());
    }

    /**
     * Retrieves fields whose type is assignable from the given class.
     *
     * @param <R>  the target field type
     * @param type the {@link Class} object representing the desired field type
     * @return fields of the specified type
     */
    @SuppressWarnings("unchecked")
    default <R> Collection<IReflectField<R>> getFieldsWithType(Class<R> type) {
        return (Collection<IReflectField<R>>) ((Collection<?>) this.getFieldsByFilter(field -> type.isAssignableFrom(field.getType())));
    }

    /**
     * Retrieves all declared fields.
     *
     * @return all fields of the class or instance
     */
    default Collection<IReflectField<?>> getFields() {
        return this.getFieldsByFilter((field) -> true);
    }

    /**
     * Finds a field by its name.
     *
     * @param <R>  the expected field type
     * @param name name of the field to find
     * @return an Optional containing the field if found
     */
    @SuppressWarnings("unchecked")
    default <R> Optional<IReflectField<R>> getFieldWithName(String name) {
        return this.getFieldsByFilter(field -> field.getName().equals(name)).stream().findFirst()
                .map(iReflectField -> (IReflectField<R>) iReflectField);
    }

    /**
     * Retrieves methods matching the given predicate.
     *
     * @param filter predicate to apply to each method
     * @return a collection of {@link IReflectMethod} instances satisfying the filter
     */
    Collection<IReflectMethod<?>> getMethodsByFilter(Predicate<IReflectMethod<?>> filter);

    /**
     * Retrieves all declared methods.
     *
     * @return all methods of the class or instance
     */
    default Collection<IReflectMethod<?>> getMethods() {
        return this.getMethodsByFilter((method) -> true);
    }

    /**
     * Retrieves methods by name.
     *
     * @param name the method name
     * @return methods matching the given name
     */
    default Collection<IReflectMethod<?>> getMethodsWithName(String name) {
        return this.getMethodsByFilter((method) -> method.getName().equals(name));
    }

    /**
     * Retrieves methods matching the specified parameter list.
     *
     * @param params the raw {@link Parameter} array to match
     * @return methods with exactly the given parameters
     */
    default Collection<IReflectMethod<?>> getMethodsWithParameters(Parameter... params) {
        return this.getMethodsByFilter((method) -> Arrays.equals(method.getParameters().stream()
                .map(ReflectParameter::getRaw)
                .toArray(Parameter[]::new), params));
    }

    /**
     * Retrieves methods matching the specified parameter types.
     *
     * @param params the classes of parameter types
     * @return methods with exactly the given parameter types
     */
    default Collection<IReflectMethod<?>> getMethodsWithParameters(Class<?>... params) {
        return this.getMethodsByFilter((method) -> Arrays.equals(method.getParameters().stream()
                .map(ReflectParameter::getType)
                .toArray(Class<?>[]::new), params));
    }

    /**
     * Retrieves methods matching the specified reflective parameter types.
     *
     * @param params the {@link IReflectClass} parameter types
     * @return methods with exactly the given reflective parameter types
     */
    default Collection<IReflectMethod<?>> getMethodsWithParameters(IReflectClass<?>... params) {
        return this.getMethodsWithParameters(Arrays.stream(params).map(IReflectClass::getType).toArray(Class[]::new));
    }

    /**
     * Retrieves methods matching the specified parameter names.
     *
     * @param paramNames names of the parameters
     * @return methods with exactly the given parameter names
     */
    default Collection<IReflectMethod<?>> getMethodsWithParameters(String... paramNames) {
        return this.getMethodsByFilter((method) -> Arrays.equals(method.getParameters().stream()
                .map(ReflectParameter::getName)
                .toArray(String[]::new), paramNames));
    }

    /**
     * Retrieves methods whose return type exactly matches the specified class.
     *
     * @param <R>        the return type
     * @param returnType the {@link Class} of the return type
     * @return methods with the specified return type
     */
    @SuppressWarnings("unchecked")
    default <R> Collection<IReflectMethod<R>> getMethodsWithReturnType(Class<R> returnType) {
        return this.getMethodsByFilter((method) -> method.getReturnType().getType().equals(returnType))
                .stream()
                .map(method -> (IReflectMethod<R>) method).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Retrieves methods whose return type exactly matches the specified reflective class.
     *
     * @param <R>        the return type
     * @param returnType the {@link IReflectClass} of the return type
     * @return methods with the specified return type
     */
    default <R> Collection<IReflectMethod<R>> getMethodsWithReturnType(IReflectClass<R> returnType) {
        return this.getMethodsWithReturnType(returnType.getType());
    }

    /**
     * Finds a method by name with no parameters.
     *
     * @param name the method name
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> getMethodWithNameAndParameters(String name) {
        return this.getMethodsWithParameters(new Parameter[0]).stream().filter(method -> method.getName().equals(name)).findFirst();
    }

    /**
     * Finds a method by name and parameter types.
     *
     * @param name   the method name
     * @param params the parameter classes
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> getMethodWithNameAndParameters(String name, Class<?>... params) {
        return this.getMethodsWithParameters(params).stream().filter(method -> method.getName().equals(name)).findFirst();
    }

    /**
     * Finds a method by name and reflective parameter types.
     *
     * @param name   the method name
     * @param params the {@link IReflectClass} parameter types
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> getMethodWithNameAndParameters(String name, IReflectClass<?>... params) {
        return this.getMethodWithNameAndParameters(name, Arrays.stream(params).map(IReflectClass::getType).toArray(Class[]::new));
    }

    /**
     * Finds a method by name and parameter names.
     *
     * @param name       the method name
     * @param paramNames the parameter names
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> getMethodWithNameAndParameters(String name, String... paramNames) {
        return this.getMethodsWithParameters(paramNames).stream().filter(method -> method.getName().equals(name)).findFirst();
    }

    /**
     * Retrieves methods annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return methods bearing the given annotation
     */
    default Collection<IReflectMethod<?>> getMethodsWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getMethodsByFilter((method) -> method.isAnnotationPresent(annotation));
    }

    /**
     * Retrieves constructors matching the given predicate.
     *
     * @param filter predicate to apply to each constructor
     * @return a collection of {@link IReflectConstructor} satisfying the filter
     */
    Collection<IReflectConstructor<T>> getConstructorsByFilter(Predicate<IReflectConstructor<T>> filter);

    /**
     * Retrieves all declared constructors.
     *
     * @return all constructors of the class
     */
    default Collection<IReflectConstructor<T>> getConstructors() {
        return this.getConstructorsByFilter(c -> true);
    }

    /**
     * Finds a constructor by parameter types.
     * @param params - constructor parameters
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> getConstructorWithParameters(Class<?>... params) {
        return this.getConstructorsByFilter(constructor ->
                        Arrays.equals(constructor.getParameters().stream()
                                .map(ReflectParameter::getType)
                                .toArray(Class[]::new), params)).stream().findFirst();
    }

    /**
     * Finds a constructor by raw parameters.
     * @param params - constructor parameters
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> getConstructorWithParameters(Parameter... params) {
        return this.getConstructorWithParameters(Arrays.stream(params).map(Parameter::getType).toArray(Class[]::new));
    }

    /**
     * Finds a constructor by reflective parameter types.
     * @param params - constructor parameters
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> getConstructorWithParameters(IReflectClass<?>... params) {
        return this.getConstructorWithParameters(Arrays.stream(params).map(IReflectClass::getType).toArray(Class[]::new));
    }

    /**
     * Finds a constructor by parameter names.
     * @param paramNames - constructor parameters name
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> getConstructorWithParameters(String... paramNames) {
        return this.getConstructorsByFilter(constructor ->
                        Arrays.equals(constructor.getParameters().stream()
                                .map(ReflectParameter::getName)
                                .toArray(String[]::new), paramNames)).stream().findFirst();
    }

    /**
     * Retrieves the default (no-arg) constructor if present.
     *
     * @return an Optional containing the default constructor
     */
    default Optional<IReflectConstructor<T>> getDefaultConstructor() {
        return getConstructorsByFilter(c -> c.getParameters().isEmpty())
                .stream().findFirst();
    }

    /**
     * Retrieves constructors annotated with the specified annotation.
     * @param annotation - annotation type
     * @return a collection of {@link IReflectConstructor}
     *
     */
    default Collection<IReflectConstructor<T>> getConstructorsWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getConstructorsByFilter(c -> c.isAnnotationPresent(annotation));
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R>  target type
     * @param type class object of the target type (ignored)
     * @return this instance as {@code IReflectClass<R>}
     */
    default <R> IReflectClass<R> cast(Class<R> type) {
        return this.cast();
    }

    /**
     * Casts this reflective handle to another type without changing underlying value.
     *
     * @param <R> target type
     * @return this instance as {@code IReflectClass<R>}
     */
    @SuppressWarnings("unchecked")
    default <R> IReflectClass<R> cast() {
        return (IReflectClass<R>) this;
    }

    /**
     * Applies a mapping function to this reflective class and wraps
     * the result in a new {@link IReflectClass}.
     *
     * @param <R>    result type
     * @param mapper function to apply
     * @return reflective handle of the mapped value
     */
    default <R> IReflectClass<R> map(Function<IReflectClass<T>, R> mapper) {
        return Reflect.on(mapper.apply(this));
    }

    /**
     * Applies a mapping function returning an IReflectClass directly.
     *
     * @param <R>    result type
     * @param mapper function to apply
     * @return the result of the mapper
     */
    default <R> IReflectClass<R> flatMap(Function<IReflectClass<T>, IReflectClass<R>> mapper) {
        return mapper.apply(this);
    }

    /**
     * Performs the given consumer on this reflective class, and returns it.
     *
     * @param consumer action to perform
     * @return this instance
     */
    default IReflectClass<T> peek(Consumer<IReflectClass<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * Retrieves the {@link Class} object wrapped by this reflector.
     *
     * @return the underlying {@code Class<T>}
     */
    Class<T> getType();

    /**
     * Retrieves the underlying instance value, if set.
     *
     * @return the wrapped value, or null if unset
     */
    T getValue();

    /**
     * Binds an instance value to this reflective handle.
     *
     * @param value the object to bind
     * @return this instance with bound value
     */
    IReflectClass<T> setValue(T value);
}
