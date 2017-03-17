package dictinsight.utils;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple cache implementation use hash table in memory
 * when the map need to cache, the elements will be saved into database.
 * you can modify method update() for different database or file system
 * <p>
 * The implement is simple and can cause memory fragmentation further version
 * may prealloc space for cache
 * 
 * @author caowei
 * @author liujg modify
 * @param <Key>
 * @param <Value>
 */
public class HashMapCache<Key, Value>{
    
    public HashMapCache() { }
    /**
     * building
     * @param maxObjects
     * @param maxLifeSeconds
     */
    public HashMapCache(int maxObjects, int maxLifeSeconds) {
        this.maxObjects = maxObjects;
        this.maxLifeSeconds = maxLifeSeconds;
    }
    
    public HashMapCache(int maxObjects, int maxLifeSeconds,int buket_count) {
        this.maxObjects = maxObjects;
        this.maxLifeSeconds = maxLifeSeconds;
        this.BUKET_COUNT=buket_count;
    }
    
    public int BUKET_COUNT = 10;
    public int maxBucketSize;
    public int currentBucket = 0;
    public ConcurrentHashMap<Key, Value>[] buckets;
    /**
     * Measured in count, default is 50/s
     */
    public int maxObjects = 300000;
    /**
     * Measured in ms, default is 30 min
     */
    public long maxLifeSeconds = 60 * 30;
    /**
     * Measured in ms, default is 1 min
     */
    public long updateTime;
    
    public Timer timer;
    /**
     * get max objects
     * @return
     */
    public int getMaxObjects() {
        return maxObjects;
    }
    /**
     * set max objects 
     * @param maxObjects int
     */    
    public void setMaxObjects(int maxObjects) {
        this.maxObjects = maxObjects;
    }
    /**
     * get max life time
     * @return
     */
    public long getMaxLifeSeconds() {
        return maxLifeSeconds;
    }
    /**
     * set max life time
     * @param maxLifeSeconds long
     */
    public void setMaxLifeSeconds(long maxLifeSeconds) {
        this.maxLifeSeconds = maxLifeSeconds;
    }

    /**
     * Intialize the cache
     */
    @SuppressWarnings("unchecked")
    public void init() {
        maxBucketSize = maxObjects / BUKET_COUNT;
        updateTime = 1000 * (maxLifeSeconds / BUKET_COUNT);
        currentBucket = 0;
        // init index
        buckets = new ConcurrentHashMap[BUKET_COUNT];
        for (int i = 0; i < BUKET_COUNT; i++) {
            buckets[i] = new ConcurrentHashMap<Key, Value>();
        }
        timer = new Timer(true);
        timer.schedule(new java.util.TimerTask() {
            public void run() {
                updateIndex();
            }
        }, updateTime, updateTime);
    }

    public void updateIndex() {
        int nextBucket = (currentBucket + 1) % BUKET_COUNT;
        buckets[nextBucket].clear();
        currentBucket = nextBucket;
    }

    public void put(Key key, Value data) {
        if (buckets[currentBucket].size() > maxBucketSize) {
            updateIndex();
        }
        buckets[currentBucket].put(key, data);
    }
    
    public Value get(Key key) {
        int stop = currentBucket;
        int start = currentBucket + BUKET_COUNT;
        for (; start > stop; start--) {
            if (buckets[start % BUKET_COUNT].containsKey(key)) {
                return buckets[start % BUKET_COUNT].get(key);
            }
        }
        return null;
    }
    
    public void invalidate(Key key) {
        int stop = currentBucket;
        int start = currentBucket + BUKET_COUNT;
        for (; start > stop; start--) {
            if (buckets[start % BUKET_COUNT].containsKey(key)) {
                buckets[start % BUKET_COUNT].remove(key);
            }
        }
        return;
    }
    
    public void clear() {
        for (int i = 0; i < BUKET_COUNT; i++) {
            buckets[i].clear();
        }
    }
    
}