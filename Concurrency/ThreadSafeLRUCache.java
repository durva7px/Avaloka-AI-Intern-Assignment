package Concurrency;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

   public class ThreadSafeLRUCache {
    private final Map<Integer, Integer> cache;
       private final ReentrantLock lock = new ReentrantLock();

       public ThreadSafeLRUCache(int capacity) {
           cache = new LinkedHashMap<Integer, Integer>(Math.max(1, capacity), 0.75f, true) {
               @Override
               protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                   return size() > capacity;
               }
           };
       }

       public int get(int key) {
           lock.lock();
           try {
               Integer value = cache.get(key);
               return value == null ? -1 : value;
           }
           finally { lock.unlock(); }
       }

       public void put(int key, int value) {
           lock.lock();
           try { cache.put(key, value); }
           finally { lock.unlock(); }
       }
   }