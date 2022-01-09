package io.github.blanketmc.blanket.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T>{
    protected final Class<T> clazz;
    protected final List<String> values;

    protected EnumArgumentType(final Class<T> enumClass, final Predicate<String> include) {
        this.clazz = enumClass;
        final List<String> values = this.values = new ArrayList<>();
        for (final Field field : enumClass.getDeclaredFields()) {
            final String value = field.getName();
            if (include.test(value)) {
                values.add(value);
            }
        }
    }

    protected EnumArgumentType(final Class<T> enumClass) {
        this.clazz = enumClass;
        final List<String> values = this.values = new ArrayList<>();
        for (final Field field : enumClass.getDeclaredFields()) {
            values.add(field.getName());
        }
    }

    @Override
    public T parse(final StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(this.clazz, reader.readString().toUpperCase());
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumeration(final Class<T> enumClass) {
        return new EnumArgumentType<>(enumClass);
    }

    public List<String> getValues() {
        return values;
    }
}