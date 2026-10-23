import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    private static class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head = new Node(0, 0);   // most recent side
    private final Node tail = new Node(0, 0);   // least recent side

    public LRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        this.map = new HashMap<>();
        head.next = tail;
        tail.prev = head;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToFront(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void moveToFront(Node node) {
        remove(node);
        addToFront(node);
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToFront(node);                 
        return node.val;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {                
            node.val = value;
            moveToFront(node);
            return;
        }
        if (map.size() == capacity) {      
            Node lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
        }
        Node fresh = new Node(key, value);
        map.put(key, fresh);
        addToFront(fresh);
    }

           public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));  
        cache.put(3, 3);                   
        System.out.println(cache.get(2));  
        cache.put(4, 4);                   
        System.out.println(cache.get(1));  
        System.out.println(cache.get(3));  
        System.out.println(cache.get(4));  
    }
}