/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hrrm.famoney.function.throwing;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.IntConsumer;

import com.hrrm.famoney.function.throwing.exception.WrappedException;

/**
 * Represents a function that accepts one argument and does not return any
 * value; Function might throw a checked exception instance.
 *
 * @param <T> the type of the input to the function
 * @param <E> the type of the thrown checked exception
 *
 * @author Grzegorz Piwowarek
 */
@FunctionalInterface
public interface ThrowingIntConsumer<E extends Exception> {

    void accept(int t) throws E;

    static IntConsumer unchecked(final ThrowingIntConsumer<?> consumer) {
        return requireNonNull(consumer).uncheck();
    }

    /**
     * Returns a new BiConsumer instance which rethrows the checked exception using
     * the Sneaky Throws pattern
     * 
     * @return BiConsumer instance that rethrows the checked exception using the
     *         Sneaky Throws pattern
     */
    static IntConsumer sneaky(final ThrowingIntConsumer<?> consumer) {
        Objects.requireNonNull(consumer);
        return t -> {
            try {
                consumer.accept(t);
            } catch (final Exception e) {
                SneakyThrowUtil.sneakyThrow(e);
            }
        };
    }

    /**
     * Chains given ThrowingConsumer instance
     * 
     * @param after - consumer that is chained after this instance
     * @return chained Consumer instance
     */
    default ThrowingIntConsumer<E> andThenConsume(final ThrowingIntConsumer<? extends E> after) {
        requireNonNull(after);
        return t -> {
            accept(t);
            after.accept(t);
        };
    }

    /**
     * @return this consumer instance as a Function instance
     */
    default ThrowingIntFunction<Void, E> asFunction() {
        return arg -> {
            accept(arg);
            return null;
        };
    }

    /**
     * @return a Consumer instance which wraps thrown checked exception instance
     *         into a RuntimeException
     */
    default IntConsumer uncheck() {
        return t -> {
            try {
                accept(t);
            } catch (final Exception e) {
                throw new WrappedException(e);
            }
        };
    }
}
