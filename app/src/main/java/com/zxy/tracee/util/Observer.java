package com.zxy.tracee.util;

import com.zxy.tracee.model.Diary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxy on 16/3/17.
 */
class Observable<T> {
    List<Observer<T>> observers = new ArrayList<Observer<T>>();
    public void register(Observer<T> observer){
        if (observer == null){
            throw new NullPointerException("Observer is Null");
        }
        synchronized (this){
            if (!observers.contains(observer)){
                observers.add(observer);
            }
        }
    }

    public synchronized void unRegister(Observer<T> observer){
        observers.remove(observer);
    }

    public void notifyObservers(T data){
        for (Observer<T> observer: observers){
            observer.onUpdate(this, data);
        }
    }
}
public interface Observer<T>{
    void onUpdate(Observable<T> observable, T data);
}
class Test{
    public static void main(String args[]){
        Observable<Diary> observable = new Observable<>();
        Observer<Diary> observer1 = new Observer<Diary>() {
            @Override
            public void onUpdate(Observable<Diary> observable, Diary data) {
                System.out.println("1.笔记内容更新了："+data.getContent());
            }
        };
        Observer<Diary> observer2 = new Observer<Diary>() {
            @Override
            public void onUpdate(Observable<Diary> observable, Diary data) {
                System.out.println("2.笔记内容更新了："+data.getContent());

            }
        };
        Diary one = new Diary("1", "Nice Day");
        observable.notifyObservers(one);

        Diary two = new Diary("2", "Bad Day");
        observable.notifyObservers(two);
    }
}


