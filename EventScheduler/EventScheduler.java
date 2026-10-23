package EventScheduler;

import java.util.Arrays;
import java.util.PriorityQueue;

public class EventScheduler {

    // Each event is int[]{start, end}
    private static int[][] sortedByStart(int[][] events) {
        int[][] copy = events.clone();   
        Arrays.sort(copy, (a, b) -> Integer.compare(a[0], b[0]));
        return copy;
    }

    public static boolean canAttendAll(int[][] events) {
        int[][] ordered = sortedByStart(events);
        for (int i = 1; i < ordered.length; i++) {
            if (ordered[i][0] < ordered[i - 1][1]) {   
                return false;
            }
        }
        return true;
    }

    public static int minRoomsRequired(int[][] events) {
        if (events == null || events.length == 0) return 0;
        int[][] ordered = sortedByStart(events);
        PriorityQueue<Integer> endTimes = new PriorityQueue<>();  
        for (int[] e : ordered) {
            if (!endTimes.isEmpty() && endTimes.peek() <= e[0]) {
                endTimes.poll();                
            }
            endTimes.offer(e[1]);
        }
        return endTimes.size();
    }

    public static void main(String[] args) {
        System.out.println(canAttendAll(new int[][]{{9, 10}, {10, 11}, {11, 12}}));   
        System.out.println(canAttendAll(new int[][]{{9, 11}, {10, 12}}));             
        System.out.println(minRoomsRequired(new int[][]{{9, 10}, {10, 11}, {11, 12}})); 
        System.out.println(minRoomsRequired(new int[][]{{0, 30}, {5, 10}, {15, 20}}));  
        System.out.println(minRoomsRequired(new int[][]{{1, 5}, {2, 6}, {3, 7}, {5, 8}})); 
        System.out.println(minRoomsRequired(new int[][]{}));                            
    }
}
