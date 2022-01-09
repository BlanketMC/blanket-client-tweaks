package io.github.blanketmc.blanket.config;

public abstract class EntryListener<T> {

    //Allows you to modify the value before it is set.
    public abstract T onEntryChange(T currentValue, T newValue);
}
