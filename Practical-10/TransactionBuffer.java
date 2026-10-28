package minibank.service;
public class TransactionBuffer<T> {
    private final Object[] items;
    private int head, tail, count;
    public TransactionBuffer(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive.");
        items = new Object[capacity];
    }
    public synchronized void put(T item) throws InterruptedException {
        while (count == items.length) wait();
        items[tail] = item;
        tail = (tail + 1) % items.length;
        count++;
        notifyAll();
    }
    @SuppressWarnings("unchecked")
    public synchronized T take() throws InterruptedException {
        while (count == 0) wait();
        T item = (T) items[head];
        items[head] = null;
        head = (head + 1) % items.length;
        count--;
        notifyAll();
        return item;
    }
}
