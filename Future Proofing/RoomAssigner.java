import java.util.*;

public class RoomAssigner {

    public static List<String> assignRooms(int[][] events) {
        int n = events.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(events[a][0], events[b][0]));

        PriorityQueue<int[]> busy = new PriorityQueue<>(
            (a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        PriorityQueue<Integer> free = new PriorityQueue<>();
        int nextRoom = 0;
        String[] result = new String[n];

        for (int idx : order) {
            int start = events[idx][0], end = events[idx][1];
            while (!busy.isEmpty() && busy.peek()[0] <= start) {
                free.offer(busy.poll()[1]);
            }
            int room = free.isEmpty() ? nextRoom++ : free.poll();
            busy.offer(new int[]{end, room});
            result[idx] = roomName(room);
        }
        return Arrays.asList(result);
    }

    static String roomName(int idx) {
        StringBuilder sb = new StringBuilder();
        idx++;
        while (idx > 0) {
            idx--;
            sb.append((char) ('A' + idx % 26));
            idx /= 26;
        }
        return "Room " + sb.reverse();
    }

    public static void main(String[] args) {
        System.out.println(assignRooms(new int[][]{{0, 30}, {5, 10}, {15, 20}}));
        // [Room A, Room B, Room B]
    }
}