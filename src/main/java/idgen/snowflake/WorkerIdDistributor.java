package idgen.snowflake;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author zhen
 */
public class WorkerIdDistributor {

    private final static long workerIdBits = 10;

    public static long getWorkerId() {
        // 1. Create config object
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");

        RedissonClient redisson = Redisson.create(config);
        long workerId = redisson.getAtomicLong("worker_id").incrementAndGet();

        // Circular id, mod by (1 << 10) 1024
        workerId %= 1 << workerIdBits;
        return workerId;
    }
}
