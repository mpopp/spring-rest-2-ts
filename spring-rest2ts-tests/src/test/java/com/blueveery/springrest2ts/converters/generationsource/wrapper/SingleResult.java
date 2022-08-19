package com.blueveery.springrest2ts.converters.generationsource.wrapper;


public class SingleResult<T> extends Result {

    public static final SingleResult<Void> EMPTY = from(null);
    private final T item;

    private SingleResult(final T item) {
        super("single");
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    /**
     * Returns a result containing the specified item.
     *
     * @param result the result item
     * @param <T> the type of the result item
     * @return a result wrapper around the specfied item
     */
    public static <T> SingleResult<T> from(final T result) {
        return new SingleResult<>(result);
    }

    /**
     * Returns the empty result.
     *
     * @return the empty result
     */
    public static SingleResult<Void> empty() {
        return EMPTY;
    }

}
