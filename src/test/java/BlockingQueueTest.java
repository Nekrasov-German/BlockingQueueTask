import org.junit.jupiter.api.Test;
import ru.german.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class BlockingQueueTest {

    @Test
    void testBasicEnqueueDequeue() throws Exception {
        var q = new BlockingQueue<Integer>(2);
        q.enqueue(1);
        q.enqueue(2);
        assertEquals(2, q.size());
        assertEquals(1, q.dequeue());
        assertEquals(2, q.dequeue());
        assertEquals(0, q.size());
    }

    @Test
    void testBlockingOnEmpty() throws Exception {
        var q = new BlockingQueue<Integer>(1);
        CountDownLatch latch = new CountDownLatch(1);

        Thread consumer = new Thread(() -> {
            try {
                Integer value = q.dequeue();
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        assertEquals(0, q.size());

        q.enqueue(42);

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Потребитель не проснулся.");
    }

    @Test
    void testBlockingOnFull() throws Exception {
        var q = new BlockingQueue<Integer>(1);
        q.enqueue(99);

        CountDownLatch latch = new CountDownLatch(1);
        Thread producer = new Thread(() -> {
            try {
                q.enqueue(77);
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();

        assertFalse(q.size() > 1, "Длинна очереди не должна превышать вместимость.");

        q.dequeue();

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Производитель не проснулся.");
        assertEquals(77, q.dequeue());
    }
}
