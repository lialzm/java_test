package com.catfish;

/**
 * Created by lcy on 17/5/18.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

class ReverseList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;

    public ReverseList(Collection<T> c) {
        super(c);
    }

    public Iterable<T> reversed() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    int size = size();
                    int cursor = 0;


                    @Override
                    public boolean hasNext() {
                        return cursor != size();
                    }

                    @Override
                    public T next() {
                        int i = cursor;
                        T t = get(size - i - 1);
                        cursor++;
                        return t;
                    }
                };
            }
        };
    }
}
