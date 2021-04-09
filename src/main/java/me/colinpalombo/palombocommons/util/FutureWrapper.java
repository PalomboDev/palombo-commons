package me.colinpalombo.palombocommons.util;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FutureWrapper<T> extends CompletableFuture<T> {

    private Consumer<Optional<T>> objectConsumer;
    private Consumer<Throwable> throwableConsumer;

    private FutureWrapper(Consumer<Optional<T>> objectConsumer, Consumer<Throwable> throwableConsumer) {
        this.objectConsumer = objectConsumer;
        this.throwableConsumer = throwableConsumer;

        addCallback(objectConsumer, throwableConsumer);
    }

    private FutureWrapper(Consumer<Optional<T>> objectConsumer) {
        this(objectConsumer, Throwable::printStackTrace);
    }

    private FutureWrapper() {

    }

    @Override
    public FutureWrapper<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        super.whenComplete(action);

        return this;
    }

    @Override
    public FutureWrapper<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        super.whenCompleteAsync(action);

        return this;
    }

    @Override
    public FutureWrapper<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        super.whenCompleteAsync(action, executor);

        return this;
    }

    public FutureWrapper<T> addCallback(Consumer<Optional<T>> objectConsumer) {
        return addCallback(objectConsumer, null);
    }

    public FutureWrapper<T> addCallback(Consumer<Optional<T>> objectConsumer, Consumer<Throwable> throwableConsumer) {
        this.objectConsumer = objectConsumer;
        this.throwableConsumer = throwableConsumer;

        return whenComplete((object, throwable) -> {
            if (this.objectConsumer != null) {
                this.objectConsumer.accept(object != null ? Optional.of(object) : Optional.empty());
            }

            if (this.throwableConsumer == null) {
                this.throwableConsumer = Throwable::printStackTrace;
            }

            if (throwable != null) {
                this.throwableConsumer.accept(throwable);
            }
        });
    }

    public static <T> FutureWrapper<T> of(Consumer<Optional<T>> objectConsumer, Consumer<Throwable> throwableConsumer) {
        return new FutureWrapper<T>(objectConsumer, throwableConsumer);
    }

    public static <T> FutureWrapper<T> of(Consumer<Optional<T>> objectConsumer) {
        return new FutureWrapper<T>(objectConsumer);
    }

    public static <T> FutureWrapper<T> empty() {
        return new FutureWrapper<T>();
    }
}