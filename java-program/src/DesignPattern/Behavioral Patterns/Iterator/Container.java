package DesignPattern.Iterator;

interface Container<T> {
    Iterator<T> createIterator();
}
