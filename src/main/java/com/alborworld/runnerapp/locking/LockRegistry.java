package com.alborworld.runnerapp.locking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alborworld.runnerapp.model.Runner;

public class LockRegistry {

    private final ConcurrentHashMap<Runner, ReadWriteLock> locks = new ConcurrentHashMap<>();

    public Lock getWriteLockFor(Runner runner) {
        return getLockFor(runner).writeLock();
    }

    public Lock getReadLockFor(Runner runner) {
        return getLockFor(runner).readLock();
    }

    private ReadWriteLock getLockFor(Runner runner) {
        locks.putIfAbsent(runner, new ReentrantReadWriteLock());
        return locks.get(runner);
    }
}