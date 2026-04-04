package dev.ckateptb.reflection.type;

import dev.ckateptb.reflection.Reflect;
import dev.ckateptb.reflection.api.AnnotationHolder;
import dev.ckateptb.reflection.api.ModifierHolder;
import dev.ckateptb.reflection.api.NameHolder;
import dev.ckateptb.reflection.api.ValueHolder;
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
public interface IReflectClass<T> extends ModifierHolder, AnnotationHolder, ValueHolder<T>, NameHolder {
    /**
     * Retrieves all fields matching the given predicate.
     *
     * @param filter predicate to apply to each field
     * @return a collection of {@link IReflectField} instances satisfying the filter
     */
    Collection<IReflectField<?>> getFieldsByFilter(Predicate<IReflectField<?>> filter);

    /**
     * Finds the first field matching the given predicate.
     *
     * @param filter predicate to apply to each field
     * @return an Optional containing the first {@link IReflectField} satisfying the filter
     */
    default Optional<IReflectField<?>> findFirstFieldByFilter(Predicate<IReflectField<?>> filter) {
        return this.getFieldsByFilter(filter).stream().findFirst();
    }

    /**
     * Retrieves the first field matching the given predicate.
     *
     * @param filter predicate to apply to each field
     * @return the first {@link IReflectField} satisfying the filter
     * @throws java.util.NoSuchElementException if no field matches
     */
    default IReflectField<?> getFirstFieldByFilter(Predicate<IReflectField<?>> filter) {
        return this.findFirstFieldByFilter(filter).orElseThrow();
    }

    /**
     * Finds the last field matching the given predicate.
     *
     * @param filter predicate to apply to each field
     * @return an Optional containing the last {@link IReflectField} satisfying the filter
     */
    default Optional<IReflectField<?>> findLastFieldByFilter(Predicate<IReflectField<?>> filter) {
        return this.getFieldsByFilter(filter).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last field matching the given predicate.
     *
     * @param filter predicate to apply to each field
     * @return the last {@link IReflectField} satisfying the filter
     * @throws java.util.NoSuchElementException if no field matches
     */
    default IReflectField<?> getLastFieldByFilter(Predicate<IReflectField<?>> filter) {
        return this.findLastFieldByFilter(filter).orElseThrow();
    }

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
     * Finds the first field annotated with the specified annotation type.
     *
     * @param annotation the annotation to look for
     * @return an Optional containing the first field bearing the given annotation
     */
    default Optional<IReflectField<?>> findFirstFieldWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getFieldsByFilter(field -> field.isAnnotationPresent(annotation)).stream().findFirst();
    }

    /**
     * Retrieves the first field annotated with the specified annotation type.
     *
     * @param annotation the annotation to look for
     * @return the first field bearing the given annotation
     * @throws java.util.NoSuchElementException if no field matches
     */
    default IReflectField<?> getFirstFieldWithAnnotation(Class<? extends Annotation> annotation) {
        return this.findFirstFieldWithAnnotation(annotation).orElseThrow();
    }

    /**
     * Finds the last field annotated with the specified annotation type.
     *
     * @param annotation the annotation to look for
     * @return an Optional containing the last field bearing the given annotation
     */
    default Optional<IReflectField<?>> findLastFieldWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getFieldsByFilter(field -> field.isAnnotationPresent(annotation)).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last field annotated with the specified annotation type.
     *
     * @param annotation the annotation to look for
     * @return the last field bearing the given annotation
     * @throws java.util.NoSuchElementException if no field matches
     */
    default IReflectField<?> getLastFieldWithAnnotation(Class<? extends Annotation> annotation) {
        return this.findLastFieldWithAnnotation(annotation).orElseThrow();
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
     * Finds the first field whose type is assignable from the given reflective class.
     *
     * @param <R>  the target field type
     * @param type the {@link IReflectClass} representing the desired field type
     * @return an Optional containing the first field of the specified type
     */
    default <R> Optional<IReflectField<R>> findFirstFieldWithType(IReflectClass<R> type) {
        return this.getFieldsWithType(type).stream().findFirst();
    }

    /**
     * Retrieves the first field whose type is assignable from the given reflective class.
     *
     * @param <R>  the target field type
     * @param type the {@link IReflectClass} representing the desired field type
     * @return the first field of the specified type
     * @throws java.util.NoSuchElementException if no field matches
     */
    default <R> IReflectField<R> getFirstFieldWithType(IReflectClass<R> type) {
        return this.findFirstFieldWithType(type).orElseThrow();
    }

    /**
     * Finds the last field whose type is assignable from the given reflective class.
     *
     * @param <R>  the target field type
     * @param type the {@link IReflectClass} representing the desired field type
     * @return an Optional containing the last field of the specified type
     */
    default <R> Optional<IReflectField<R>> findLastFieldWithType(IReflectClass<R> type) {
        return this.getFieldsWithType(type).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last field whose type is assignable from the given reflective class.
     *
     * @param <R>  the target field type
     * @param type the {@link IReflectClass} representing the desired field type
     * @return the last field of the specified type
     * @throws java.util.NoSuchElementException if no field matches
     */
    default <R> IReflectField<R> getLastFieldWithType(IReflectClass<R> type) {
        return this.findLastFieldWithType(type).orElseThrow();
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
     * Finds the first field whose type is assignable from the given class.
     *
     * @param <R>  the target field type
     * @param type the {@link Class} object representing the desired field type
     * @return an Optional containing the first field of the specified type
     */
    default <R> Optional<IReflectField<R>> findFirstFieldWithType(Class<R> type) {
        return this.getFieldsWithType(type).stream().findFirst();
    }

    /**
     * Retrieves the first field whose type is assignable from the given class.
     *
     * @param <R>  the target field type
     * @param type the {@link Class} object representing the desired field type
     * @return the first field of the specified type
     * @throws java.util.NoSuchElementException if no field matches
     */
    default <R> IReflectField<R> getFirstFieldWithType(Class<R> type) {
        return this.findFirstFieldWithType(type).orElseThrow();
    }

    /**
     * Finds the last field whose type is assignable from the given class.
     *
     * @param <R>  the target field type
     * @param type the {@link Class} object representing the desired field type
     * @return an Optional containing the last field of the specified type
     */
    default <R> Optional<IReflectField<R>> findLastFieldWithType(Class<R> type) {
        return this.getFieldsWithType(type).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last field whose type is assignable from the given class.
     *
     * @param <R>  the target field type
     * @param type the {@link Class} object representing the desired field type
     * @return the last field of the specified type
     * @throws java.util.NoSuchElementException if no field matches
     */
    default <R> IReflectField<R> getLastFieldWithType(Class<R> type) {
        return this.findLastFieldWithType(type).orElseThrow();
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
    default <R> Optional<IReflectField<R>> findFieldWithName(String name) {
        return this.getFieldsByFilter(field -> field.getName().equals(name)).stream().findFirst()
                .map(iReflectField -> (IReflectField<R>) iReflectField);
    }

    /**
     * Finds a field by its name.
     *
     * @param <R>  the expected field type
     * @param name name of the field to find
     * @return the field
     * @throws java.util.NoSuchElementException if field is not found
     */
    @SuppressWarnings("unchecked")
    default <R> IReflectField<R> getFieldWithName(String name) {
        return (IReflectField<R>) this.findFieldWithName(name).orElseThrow();
    }

    /**
     * Retrieves methods matching the given predicate.
     *
     * @param filter predicate to apply to each method
     * @return a collection of {@link IReflectMethod} instances satisfying the filter
     */
    Collection<IReflectMethod<?>> getMethodsByFilter(Predicate<IReflectMethod<?>> filter);

    /**
     * Finds the first method matching the given predicate.
     *
     * @param filter predicate to apply to each method
     * @return an Optional containing the first {@link IReflectMethod} satisfying the filter
     */
    default Optional<IReflectMethod<?>> findFirstMethodByFilter(Predicate<IReflectMethod<?>> filter) {
        return this.getMethodsByFilter(filter).stream().findFirst();
    }

    /**
     * Retrieves the first method matching the given predicate.
     *
     * @param filter predicate to apply to each method
     * @return the first {@link IReflectMethod} satisfying the filter
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getFirstMethodByFilter(Predicate<IReflectMethod<?>> filter) {
        return this.findFirstMethodByFilter(filter).orElseThrow();
    }

    /**
     * Finds the last method matching the given predicate.
     *
     * @param filter predicate to apply to each method
     * @return an Optional containing the last {@link IReflectMethod} satisfying the filter
     */
    default Optional<IReflectMethod<?>> findLastMethodByFilter(Predicate<IReflectMethod<?>> filter) {
        return this.getMethodsByFilter(filter).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last method matching the given predicate.
     *
     * @param filter predicate to apply to each method
     * @return the last {@link IReflectMethod} satisfying the filter
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getLastMethodByFilter(Predicate<IReflectMethod<?>> filter) {
        return this.findLastMethodByFilter(filter).orElseThrow();
    }

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
     * Finds the first method with the given name.
     *
     * @param name the method name
     * @return an Optional containing the first method matching the given name
     */
    default Optional<IReflectMethod<?>> findFirstMethodWithName(String name) {
        return this.getMethodsByFilter(method -> method.getName().equals(name)).stream().findFirst();
    }

    /**
     * Retrieves the first method with the given name.
     *
     * @param name the method name
     * @return the first method matching the given name
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getFirstMethodWithName(String name) {
        return this.findFirstMethodWithName(name).orElseThrow();
    }

    /**
     * Finds the last method with the given name.
     *
     * @param name the method name
     * @return an Optional containing the last method matching the given name
     */
    default Optional<IReflectMethod<?>> findLastMethodWithName(String name) {
        return this.getMethodsByFilter(method -> method.getName().equals(name)).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last method with the given name.
     *
     * @param name the method name
     * @return the last method matching the given name
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getLastMethodWithName(String name) {
        return this.findLastMethodWithName(name).orElseThrow();
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
     * Finds the first method matching the specified parameter list.
     *
     * @param params the raw {@link Parameter} array to match
     * @return an Optional containing the first method with exactly the given parameters
     */
    default Optional<IReflectMethod<?>> findFirstMethodWithParameters(Parameter... params) {
        return this.getMethodsWithParameters(params).stream().findFirst();
    }

    /**
     * Retrieves the first method matching the specified parameter list.
     *
     * @param params the raw {@link Parameter} array to match
     * @return the first method with exactly the given parameters
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getFirstMethodWithParameters(Parameter... params) {
        return this.findFirstMethodWithParameters(params).orElseThrow();
    }

    /**
     * Finds the last method matching the specified parameter list.
     *
     * @param params the raw {@link Parameter} array to match
     * @return an Optional containing the last method with exactly the given parameters
     */
    default Optional<IReflectMethod<?>> findLastMethodWithParameters(Parameter... params) {
        return this.getMethodsWithParameters(params).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last method matching the specified parameter list.
     *
     * @param params the raw {@link Parameter} array to match
     * @return the last method with exactly the given parameters
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getLastMethodWithParameters(Parameter... params) {
        return this.findLastMethodWithParameters(params).orElseThrow();
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
     * Finds the first method matching the specified parameter types.
     *
     * @param params the classes of parameter types
     * @return an Optional containing the first method with exactly the given parameter types
     */
    default Optional<IReflectMethod<?>> findFirstMethodWithParameters(Class<?>... params) {
        return this.getMethodsWithParameters(params).stream().findFirst();
    }

    /**
     * Retrieves the first method matching the specified parameter types.
     *
     * @param params the classes of parameter types
     * @return the first method with exactly the given parameter types
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getFirstMethodWithParameters(Class<?>... params) {
        return this.findFirstMethodWithParameters(params).orElseThrow();
    }

    /**
     * Finds the last method matching the specified parameter types.
     *
     * @param params the classes of parameter types
     * @return an Optional containing the last method with exactly the given parameter types
     */
    default Optional<IReflectMethod<?>> findLastMethodWithParameters(Class<?>... params) {
        return this.getMethodsWithParameters(params).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last method matching the specified parameter types.
     *
     * @param params the classes of parameter types
     * @return the last method with exactly the given parameter types
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getLastMethodWithParameters(Class<?>... params) {
        return this.findLastMethodWithParameters(params).orElseThrow();
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
     * Finds the first method matching the specified reflective parameter types.
     *
     * @param params the {@link IReflectClass} parameter types
     * @return an Optional containing the first method with exactly the given reflective parameter types
     */
    default Optional<IReflectMethod<?>> findFirstMethodWithParameters(IReflectClass<?>... params) {
        return this.getMethodsWithParameters(params).stream().findFirst();
    }

    /**
     * Retrieves the first method matching the specified reflective parameter types.
     *
     * @param params the {@link IReflectClass} parameter types
     * @return the first method with exactly the given reflective parameter types
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getFirstMethodWithParameters(IReflectClass<?>... params) {
        return this.findFirstMethodWithParameters(params).orElseThrow();
    }

    /**
     * Finds the last method matching the specified reflective parameter types.
     *
     * @param params the {@link IReflectClass} parameter types
     * @return an Optional containing the last method with exactly the given reflective parameter types
     */
    default Optional<IReflectMethod<?>> findLastMethodWithParameters(IReflectClass<?>... params) {
        return this.getMethodsWithParameters(params).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last method matching the specified reflective parameter types.
     *
     * @param params the {@link IReflectClass} parameter types
     * @return the last method with exactly the given reflective parameter types
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getLastMethodWithParameters(IReflectClass<?>... params) {
        return this.findLastMethodWithParameters(params).orElseThrow();
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
     * Finds the first method matching the specified parameter names.
     *
     * @param paramNames names of the parameters
     * @return an Optional containing the first method with exactly the given parameter names
     */
    default Optional<IReflectMethod<?>> findFirstMethodWithParameters(String... paramNames) {
        return this.getMethodsWithParameters(paramNames).stream().findFirst();
    }

    /**
     * Retrieves the first method matching the specified parameter names.
     *
     * @param paramNames names of the parameters
     * @return the first method with exactly the given parameter names
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getFirstMethodWithParameters(String... paramNames) {
        return this.findFirstMethodWithParameters(paramNames).orElseThrow();
    }

    /**
     * Finds the last method matching the specified parameter names.
     *
     * @param paramNames names of the parameters
     * @return an Optional containing the last method with exactly the given parameter names
     */
    default Optional<IReflectMethod<?>> findLastMethodWithParameters(String... paramNames) {
        return this.getMethodsWithParameters(paramNames).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last method matching the specified parameter names.
     *
     * @param paramNames names of the parameters
     * @return the last method with exactly the given parameter names
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getLastMethodWithParameters(String... paramNames) {
        return this.findLastMethodWithParameters(paramNames).orElseThrow();
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
     * Finds the first method whose return type exactly matches the specified class.
     *
     * @param <R>        the return type
     * @param returnType the {@link Class} of the return type
     * @return an Optional containing the first method with the specified return type
     */
    @SuppressWarnings("unchecked")
    default <R> Optional<IReflectMethod<R>> findFirstMethodWithReturnType(Class<R> returnType) {
        return this.getMethodsByFilter(method -> method.getReturnType().getType().equals(returnType))
                .stream().findFirst().map(method -> (IReflectMethod<R>) method);
    }

    /**
     * Retrieves the first method whose return type exactly matches the specified class.
     *
     * @param <R>        the return type
     * @param returnType the {@link Class} of the return type
     * @return the first method with the specified return type
     * @throws java.util.NoSuchElementException if no method matches
     */
    default <R> IReflectMethod<R> getFirstMethodWithReturnType(Class<R> returnType) {
        return this.<R>findFirstMethodWithReturnType(returnType).orElseThrow();
    }

    /**
     * Finds the last method whose return type exactly matches the specified class.
     *
     * @param <R>        the return type
     * @param returnType the {@link Class} of the return type
     * @return an Optional containing the last method with the specified return type
     */
    @SuppressWarnings("unchecked")
    default <R> Optional<IReflectMethod<R>> findLastMethodWithReturnType(Class<R> returnType) {
        return this.getMethodsByFilter(method -> method.getReturnType().getType().equals(returnType))
                .stream().reduce((a, b) -> b).map(method -> (IReflectMethod<R>) method);
    }

    /**
     * Retrieves the last method whose return type exactly matches the specified class.
     *
     * @param <R>        the return type
     * @param returnType the {@link Class} of the return type
     * @return the last method with the specified return type
     * @throws java.util.NoSuchElementException if no method matches
     */
    default <R> IReflectMethod<R> getLastMethodWithReturnType(Class<R> returnType) {
        return this.<R>findLastMethodWithReturnType(returnType).orElseThrow();
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
     * Finds the first method whose return type exactly matches the specified reflective class.
     *
     * @param <R>        the return type
     * @param returnType the {@link IReflectClass} of the return type
     * @return an Optional containing the first method with the specified return type
     */
    default <R> Optional<IReflectMethod<R>> findFirstMethodWithReturnType(IReflectClass<R> returnType) {
        return this.findFirstMethodWithReturnType(returnType.getType());
    }

    /**
     * Retrieves the first method whose return type exactly matches the specified reflective class.
     *
     * @param <R>        the return type
     * @param returnType the {@link IReflectClass} of the return type
     * @return the first method with the specified return type
     * @throws java.util.NoSuchElementException if no method matches
     */
    default <R> IReflectMethod<R> getFirstMethodWithReturnType(IReflectClass<R> returnType) {
        return this.findFirstMethodWithReturnType(returnType).orElseThrow();
    }

    /**
     * Finds the last method whose return type exactly matches the specified reflective class.
     *
     * @param <R>        the return type
     * @param returnType the {@link IReflectClass} of the return type
     * @return an Optional containing the last method with the specified return type
     */
    default <R> Optional<IReflectMethod<R>> findLastMethodWithReturnType(IReflectClass<R> returnType) {
        return this.findLastMethodWithReturnType(returnType.getType());
    }

    /**
     * Retrieves the last method whose return type exactly matches the specified reflective class.
     *
     * @param <R>        the return type
     * @param returnType the {@link IReflectClass} of the return type
     * @return the last method with the specified return type
     * @throws java.util.NoSuchElementException if no method matches
     */
    default <R> IReflectMethod<R> getLastMethodWithReturnType(IReflectClass<R> returnType) {
        return this.findLastMethodWithReturnType(returnType).orElseThrow();
    }

    /**
     * Finds a method by name with no parameters.
     *
     * @param name the method name
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> findMethodWithNameAndParameters(String name) {
        return this.getMethodsWithParameters(new Parameter[0]).stream().filter(method -> method.getName().equals(name)).findFirst();
    }

    /**
     * Finds a method by name with no parameters.
     *
     * @param name the method name
     * @return the method
     * @throws java.util.NoSuchElementException if method is not found
     */
    default IReflectMethod<?> getMethodWithNameAndParameters(String name) {
        return this.findMethodWithNameAndParameters(name).orElseThrow();
    }

    /**
     * Finds a method by name and parameter types.
     *
     * @param name   the method name
     * @param params the parameter classes
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> findMethodWithNameAndParameters(String name, Class<?>... params) {
        return this.getMethodsWithParameters(params).stream().filter(method -> method.getName().equals(name)).findFirst();
    }

    /**
     * Finds a method by name and parameter types.
     *
     * @param name   the method name
     * @param params the parameter classes
     * @return the method
     * @throws java.util.NoSuchElementException if method is not found
     */
    default IReflectMethod<?> getMethodWithNameAndParameters(String name, Class<?>... params) {
        return this.findMethodWithNameAndParameters(name, params).orElseThrow();
    }

    /**
     * Finds a method by name and reflective parameter types.
     *
     * @param name   the method name
     * @param params the {@link IReflectClass} parameter types
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> findMethodWithNameAndParameters(String name, IReflectClass<?>... params) {
        return this.findMethodWithNameAndParameters(name, Arrays.stream(params).map(IReflectClass::getType).toArray(Class[]::new));
    }

    /**
     * Finds a method by name and reflective parameter types.
     *
     * @param name   the method name
     * @param params the {@link IReflectClass} parameter types
     * @return the method
     * @throws java.util.NoSuchElementException if method is not found
     */
    default IReflectMethod<?> getMethodWithNameAndParameters(String name, IReflectClass<?>... params) {
        return this.findMethodWithNameAndParameters(name, params).orElseThrow();
    }

    /**
     * Finds a method by name and parameter names.
     *
     * @param name       the method name
     * @param paramNames the parameter names
     * @return an Optional containing the method if found
     */
    default Optional<IReflectMethod<?>> findMethodWithNameAndParameters(String name, String... paramNames) {
        return this.getMethodsWithParameters(paramNames).stream().filter(method -> method.getName().equals(name)).findFirst();
    }

    /**
     * Finds a method by name and parameter names.
     *
     * @param name       the method name
     * @param paramNames the parameter names
     * @return the method
     * @throws java.util.NoSuchElementException if method is not found
     */
    default IReflectMethod<?> getMethodWithNameAndParameters(String name, String... paramNames) {
        return this.findMethodWithNameAndParameters(name, paramNames).orElseThrow();
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
     * Finds the first method annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return an Optional containing the first method bearing the given annotation
     */
    default Optional<IReflectMethod<?>> findFirstMethodWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getMethodsByFilter(method -> method.isAnnotationPresent(annotation)).stream().findFirst();
    }

    /**
     * Retrieves the first method annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return the first method bearing the given annotation
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getFirstMethodWithAnnotation(Class<? extends Annotation> annotation) {
        return this.findFirstMethodWithAnnotation(annotation).orElseThrow();
    }

    /**
     * Finds the last method annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return an Optional containing the last method bearing the given annotation
     */
    default Optional<IReflectMethod<?>> findLastMethodWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getMethodsByFilter(method -> method.isAnnotationPresent(annotation)).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last method annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return the last method bearing the given annotation
     * @throws java.util.NoSuchElementException if no method matches
     */
    default IReflectMethod<?> getLastMethodWithAnnotation(Class<? extends Annotation> annotation) {
        return this.findLastMethodWithAnnotation(annotation).orElseThrow();
    }

    /**
     * Retrieves constructors matching the given predicate.
     *
     * @param filter predicate to apply to each constructor
     * @return a collection of {@link IReflectConstructor} satisfying the filter
     */
    Collection<IReflectConstructor<T>> getConstructorsByFilter(Predicate<IReflectConstructor<T>> filter);

    /**
     * Finds the first constructor matching the given predicate.
     *
     * @param filter predicate to apply to each constructor
     * @return an Optional containing the first {@link IReflectConstructor} satisfying the filter
     */
    default Optional<IReflectConstructor<T>> findFirstConstructorByFilter(Predicate<IReflectConstructor<T>> filter) {
        return this.getConstructorsByFilter(filter).stream().findFirst();
    }

    /**
     * Retrieves the first constructor matching the given predicate.
     *
     * @param filter predicate to apply to each constructor
     * @return the first {@link IReflectConstructor} satisfying the filter
     * @throws java.util.NoSuchElementException if no constructor matches
     */
    default IReflectConstructor<T> getFirstConstructorByFilter(Predicate<IReflectConstructor<T>> filter) {
        return this.findFirstConstructorByFilter(filter).orElseThrow();
    }

    /**
     * Finds the last constructor matching the given predicate.
     *
     * @param filter predicate to apply to each constructor
     * @return an Optional containing the last {@link IReflectConstructor} satisfying the filter
     */
    default Optional<IReflectConstructor<T>> findLastConstructorByFilter(Predicate<IReflectConstructor<T>> filter) {
        return this.getConstructorsByFilter(filter).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last constructor matching the given predicate.
     *
     * @param filter predicate to apply to each constructor
     * @return the last {@link IReflectConstructor} satisfying the filter
     * @throws java.util.NoSuchElementException if no constructor matches
     */
    default IReflectConstructor<T> getLastConstructorByFilter(Predicate<IReflectConstructor<T>> filter) {
        return this.findLastConstructorByFilter(filter).orElseThrow();
    }

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
     *
     * @param params - constructor parameters
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> findConstructorWithParameters(Class<?>... params) {
        return this.getConstructorsByFilter(constructor ->
                Arrays.equals(constructor.getParameters().stream()
                        .map(ReflectParameter::getType)
                        .toArray(Class[]::new), params)).stream().findFirst();
    }

    /**
     * Finds a constructor by parameter types.
     *
     * @param params constructor parameters
     * @return the constructor
     * @throws java.util.NoSuchElementException if constructor is not found
     */
    default IReflectConstructor<T> getConstructorWithParameters(Class<?>... params) {
        return this.findConstructorWithParameters(params).orElseThrow();
    }

    /**
     * Finds a constructor by raw parameters.
     *
     * @param params - constructor parameters
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> findConstructorWithParameters(Parameter... params) {
        return this.findConstructorWithParameters(Arrays.stream(params).map(Parameter::getType).toArray(Class[]::new));
    }

    /**
     * Finds a constructor by raw parameters.
     *
     * @param params constructor parameters
     * @return the constructor
     * @throws java.util.NoSuchElementException if constructor is not found
     */
    default IReflectConstructor<T> getConstructorWithParameters(Parameter... params) {
        return this.findConstructorWithParameters(params).orElseThrow();
    }

    /**
     * Finds a constructor by reflective parameter types.
     *
     * @param params - constructor parameters
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> findConstructorWithParameters(IReflectClass<?>... params) {
        return this.findConstructorWithParameters(Arrays.stream(params).map(IReflectClass::getType).toArray(Class[]::new));
    }

    /**
     * Finds a constructor by reflective parameter types.
     *
     * @param params constructor parameters
     * @return the constructor
     * @throws java.util.NoSuchElementException if constructor is not found
     */
    default IReflectConstructor<T> getConstructorWithParameters(IReflectClass<?>... params) {
        return this.findConstructorWithParameters(params).orElseThrow();
    }

    /**
     * Finds a constructor by parameter names.
     *
     * @param paramNames - constructor parameters name
     * @return an optional of {@link IReflectConstructor}
     */
    default Optional<IReflectConstructor<T>> findConstructorWithParameters(String... paramNames) {
        return this.getConstructorsByFilter(constructor ->
                Arrays.equals(constructor.getParameters().stream()
                        .map(ReflectParameter::getName)
                        .toArray(String[]::new), paramNames)).stream().findFirst();
    }

    /**
     * Finds a constructor by parameter names.
     *
     * @param paramNames constructor parameter names
     * @return the constructor
     * @throws java.util.NoSuchElementException if constructor is not found
     */
    default IReflectConstructor<T> getConstructorWithParameters(String... paramNames) {
        return this.findConstructorWithParameters(paramNames).orElseThrow();
    }

    /**
     * Retrieves the default (no-arg) constructor if present.
     *
     * @return an Optional containing the default constructor
     */
    default Optional<IReflectConstructor<T>> findDefaultConstructor() {
        return getConstructorsByFilter(c -> c.getParameters().isEmpty())
                .stream().findFirst();
    }

    /**
     * Retrieves the default (no-arg) constructor.
     *
     * @return the default constructor
     * @throws java.util.NoSuchElementException if default constructor is not found
     */
    default IReflectConstructor<T> getDefaultConstructor() {
        return this.findDefaultConstructor().orElseThrow();
    }

    /**
     * Retrieves constructors annotated with the specified annotation.
     *
     * @param annotation - annotation type
     * @return a collection of {@link IReflectConstructor}
     *
     */
    default Collection<IReflectConstructor<T>> getConstructorsWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getConstructorsByFilter(c -> c.isAnnotationPresent(annotation));
    }

    /**
     * Finds the first constructor annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return an Optional containing the first {@link IReflectConstructor} bearing the given annotation
     */
    default Optional<IReflectConstructor<T>> findFirstConstructorWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getConstructorsByFilter(c -> c.isAnnotationPresent(annotation)).stream().findFirst();
    }

    /**
     * Retrieves the first constructor annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return the first {@link IReflectConstructor} bearing the given annotation
     * @throws java.util.NoSuchElementException if no constructor matches
     */
    default IReflectConstructor<T> getFirstConstructorWithAnnotation(Class<? extends Annotation> annotation) {
        return this.findFirstConstructorWithAnnotation(annotation).orElseThrow();
    }

    /**
     * Finds the last constructor annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return an Optional containing the last {@link IReflectConstructor} bearing the given annotation
     */
    default Optional<IReflectConstructor<T>> findLastConstructorWithAnnotation(Class<? extends Annotation> annotation) {
        return this.getConstructorsByFilter(c -> c.isAnnotationPresent(annotation)).stream().reduce((a, b) -> b);
    }

    /**
     * Retrieves the last constructor annotated with the specified annotation.
     *
     * @param annotation the annotation type
     * @return the last {@link IReflectConstructor} bearing the given annotation
     * @throws java.util.NoSuchElementException if no constructor matches
     */
    default IReflectConstructor<T> getLastConstructorWithAnnotation(Class<? extends Annotation> annotation) {
        return this.findLastConstructorWithAnnotation(annotation).orElseThrow();
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
     * Retrieves the name of this target.
     *
     * @return the target name
     */
    default String getSimpleName() {
        return this.getName();
    }

    /**
     * Checks if this object can be considered an instance of the specified class.
     *
     * @param clazz The class to check against.
     * @return true if this object's type is assignable from the specified class, false otherwise.
     */
    default boolean isInstanceOf(Class<?> clazz) {
        return clazz.isAssignableFrom(this.getType());
    }

    /**
     * Returns the package name of the type associated with this instance.
     *
     * @return The package name as a String, or an empty string if no package is associated.
     */
    default String getPackage() {
        return this.getType().getPackageName();
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
