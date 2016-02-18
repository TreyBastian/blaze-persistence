package com.blazebit.persistence.view.impl.collection;

import java.util.NavigableMap;
import java.util.NavigableSet;

public class RecordingNavigableMap<C extends NavigableMap<K, V>, K, V> extends RecordingSortedMap<C, K, V> implements NavigableMap<K, V> {

    public RecordingNavigableMap(C delegate) {
        super(delegate);
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        // TODO: implement
        throw new UnsupportedOperationException("Submaps for entity view collections are not yet supported!");
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        // TODO: implement
        throw new UnsupportedOperationException("Submaps for entity view collections are not yet supported!");
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        // TODO: implement
        throw new UnsupportedOperationException("Submaps for entity view collections are not yet supported!");
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        // TODO: implement
        throw new UnsupportedOperationException("Submaps for entity view collections are not yet supported!");
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        // TODO: implement
        throw new UnsupportedOperationException("Submaps for entity view collections are not yet supported!");
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        // TODO: implement
        throw new UnsupportedOperationException("Submaps for entity view collections are not yet supported!");
    }

    
    /**************
     * Read-only
     *************/

    @Override
    public java.util.Map.Entry<K, V> lowerEntry(K key) {
        return delegate.lowerEntry(key);
    }

    @Override
    public K lowerKey(K key) {
        return delegate.lowerKey(key);
    }

    @Override
    public java.util.Map.Entry<K, V> floorEntry(K key) {
        return delegate.floorEntry(key);
    }

    @Override
    public K floorKey(K key) {
        return delegate.floorKey(key);
    }

    @Override
    public java.util.Map.Entry<K, V> ceilingEntry(K key) {
        return delegate.ceilingEntry(key);
    }

    @Override
    public K ceilingKey(K key) {
        return delegate.ceilingKey(key);
    }

    @Override
    public java.util.Map.Entry<K, V> higherEntry(K key) {
        return delegate.higherEntry(key);
    }

    @Override
    public K higherKey(K key) {
        return delegate.higherKey(key);
    }

    @Override
    public java.util.Map.Entry<K, V> firstEntry() {
        return delegate.firstEntry();
    }

    @Override
    public java.util.Map.Entry<K, V> lastEntry() {
        return delegate.lastEntry();
    }

    @Override
    public java.util.Map.Entry<K, V> pollFirstEntry() {
        return delegate.pollFirstEntry();
    }

    @Override
    public java.util.Map.Entry<K, V> pollLastEntry() {
        return delegate.pollLastEntry();
    }

}