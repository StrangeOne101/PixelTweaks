package com.strangeone101.pixeltweaks;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Credit to the Pixelmon team for their Scheduling class that I based it off.
 * However, their scheduler cannot schedule tasks client side, so this class had
 * to be made in addition to it
 */
public class ClientScheduler {
    public static class ScheduledTask
    {
        public int ticks;
        public Runnable task;
    }
    private static ArrayList<ScheduledTask> tasks = new ArrayList<>();
    private static boolean iterating = false;
    private static ArrayList<ScheduledTask> queue = new ArrayList<>();


    public static void tick() {
        iterating = true;
        Iterator<ScheduledTask> it = tasks.iterator();
        while (it.hasNext()) {

            ScheduledTask t = it.next();
            if (--t.ticks <= 0) {

                t.task.run();
                it.remove();
            }
        }
        iterating = false;
        if (!queue.isEmpty()) {

            tasks.addAll(queue);
            queue.clear();
        }
    }


    public static void schedule(int ticks, Runnable task) {
        ScheduledTask t = new ScheduledTask();
        t.ticks = ticks;
        t.task = task;

        if (iterating) {
            queue.add(t);
        } else {
            tasks.add(t);
        }
    }
}
