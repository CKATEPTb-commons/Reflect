package dev.ckateptb.reflection;

import lombok.experimental.Delegate;

import java.lang.reflect.Field;

public class ReflectField extends ReflectWrapper<Field> {
    @Delegate
    private final Field field;

    ReflectField(Field field) {
        super(field);
        this.field = field;
        this.field.trySetAccessible();
    }

    @Override
    public int modifiers() {
        return this.getModifiers();
    }
}