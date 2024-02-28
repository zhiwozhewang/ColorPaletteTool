package com.whatscolors.demo.utils;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Administrator on 2018/1/4 0004.
 */

public class RxBus {

    private final Subject<Object> bus;
    private static RxBus rxBus;

    private RxBus() {
        bus = PublishSubject.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (null == rxBus) {
            synchronized (RxBus.class) {
                if (null == rxBus) {
                    rxBus = new RxBus();
                }
            }

        }
        return rxBus;
    }

    public void post(Object o) {
        bus.onNext(o);
    }

    public boolean hasObservable() {
        return bus.hasObservers();
    }

    /**
     * 转换为特定类型的Obserbale
     */
    public <T> Observable<T> tObservable(Class<T> type) {
        return bus.ofType(type);
    }
}
