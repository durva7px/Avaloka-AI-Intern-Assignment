# DSA Assessment: LRU Cache & Event Scheduler (Java)

Solutions to two data-structure problems, plus extensions for room assignment and thread safety.

## Folder Structure

```
.
├── README.md
├── LRUCache/
│   ├── LRUCache.java            # HashMap + doubly linked list, O(1) get/put
│   └── SimpleLRU.java           # (optional) LinkedHashMap-based version
├── EventScheduler/
│   └── EventScheduler.java      # canAttendAll, minRoomsRequired
├── Future Proofing/
│   └── RoomAssigner.java        # assigns "Room A", "Room B", ... to each event
└── Concurrency/
    └── ThreadSafeLRUCache.java  # lock-based wrapper around LRUCache
```

Each `.java` file must have the same name as the `public` class inside it (case-sensitive).

## Requirements

- JDK 11 or later (`java -version` to check)

## How to Run

`javac` takes a **file name** (with `.java`); `java` takes a **class name** (no extension).

### LRUCache

```bash
cd LRUCache
javac LRUCache.java
java LRUCache
```

Expected output:

```
1
-1
-1
3
4
```

### EventScheduler

```bash
cd EventScheduler
javac EventScheduler.java
java EventScheduler
```

Expected output:

```
true
false
1
2
3
0
```

### Future Proofing

The folder name contains a space, so wrap it in quotes:

```bash
cd "Future Proofing"
javac RoomAssigner.java
java RoomAssigner
```

Expected output:

```
[Room A, Room B, Room B]
```

### Concurrency

`ThreadSafeLRUCache` wraps the `LRUCache` class from the `LRUCache` folder, so compile both together:

```bash
cd Concurrency
javac -d out ThreadSafeLRUCache.java ../LRUCache/LRUCache.java
```

`ThreadSafeLRUCache` has no `main` method; it is meant to be used from other code. To run a demo, add a `main` that starts several threads calling `get` and `put`, then run:

```bash
java -cp out ThreadSafeLRUCache
```

---

## Problem 1: LRU Cache

**Operations**

- `get(key)`: returns the value, or `-1` if missing, and marks the key as most recently used.
- `put(key, value)`: inserts or updates; if full, evicts the least recently used key first.

**Approach**

- A `HashMap<Integer, Node>` maps each key to its list node, giving O(1) lookup.
- A doubly linked list keeps nodes in usage order: most recent next to the `head` sentinel, least recent next to the `tail` sentinel.
- Each access moves the node to the front. Eviction removes the node before `tail` and deletes its key from the map (each node stores its key for this purpose).
- Sentinel nodes remove special cases for empty lists and list ends.

**Why these structures:** the map gives direct access to a node; the `prev` pointer lets the node be unlinked in O(1). A singly linked list would need an O(n) walk to find the predecessor, and an array would need O(n) shifting.

## Problem 2: Event Scheduler

Events are `int[]{start, end}`. Adjacent events (one ends exactly when the next starts) do **not** overlap.

**`canAttendAll`:** sort by start time and check neighbours. If any event starts strictly before the previous one ends, return `false`.

**`minRoomsRequired`:** process events in start order with a min-heap of end times for rooms in use.

1. If the earliest-ending room is free by this event's start (`peek() <= start`), reuse it (`poll`).
2. Push this event's end time.

The heap grows only when every room is busy, so its final size is the maximum number of simultaneous meetings, which is the number of rooms required.

## Future Proofing: Room Assignment

`RoomAssigner.assignRooms` returns a room name for each event, in input order.

- A min-heap of `{endTime, roomId}` tracks busy rooms.
- A min-heap of room ids tracks free rooms, so the lowest-numbered free room is reused first.
- For each event in start order, every room that has ended by its start moves from busy to free. The event takes a free room or opens a new one.
- Room ids are converted to names: `0 → Room A`, `25 → Room Z`, `26 → Room AA`.

## Concurrency: Thread-Safe LRU Cache

`get` changes the linked list, so it is a write. A read-write lock that lets readers share access would allow concurrent `get` calls to corrupt the list.

`ThreadSafeLRUCache` guards both `get` and `put` with a single `ReentrantLock`, so each operation is atomic. Options for higher throughput:

- **Lock striping:** split the cache into N segments, each with its own lock; eviction becomes per-segment LRU.
- **Buffered or approximate reads:** batch reorderings, or use a CLOCK-style policy where a read only sets a flag.
- **Use a library:** Caffeine is the standard production choice in Java.

Replacing `HashMap` with `ConcurrentHashMap` is not enough on its own, because the map and the list must be updated together atomically.

## Complexity

| Method | Time | Space |
|---|---|---|
| `LRUCache.get` | O(1) average | O(1) extra |
| `LRUCache.put` | O(1) average | O(1) extra |
| LRU cache overall | — | O(capacity) |
| `canAttendAll` | O(n log n) | O(n) |
| `minRoomsRequired` | O(n log n) | O(n) |
| `assignRooms` | O(n log n) | O(n) |
| `ThreadSafeLRUCache.get` / `put` | O(1) plus lock cost | O(1) extra |

`HashMap` operations are O(1) on average; since Java 8, heavily colliding buckets become balanced trees, so the worst case is O(log n).

## Trade-offs of HashMap + Doubly Linked List

| Structure | Lookup | Reorder / evict |
|---|---|---|
| HashMap only | O(1) | O(n), no ordering |
| Linked list only | O(n) | O(1) |
| Both together | O(1) | O(1) |

Costs: extra memory for two pointers per node and the duplicated key, weaker CPU cache locality, and a structural write on every read. `LinkedHashMap` with access order and `removeEldestEntry` provides the same design out of the box (see `SimpleLRU.java`).