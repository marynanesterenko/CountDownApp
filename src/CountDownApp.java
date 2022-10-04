import java.util.ArrayList;

    public class CountDownApp {
        public static void main(String[] args) {
            CountDownClock clock = new CountDownClock(20);
            ArrayList<Runnable> events = new ArrayList<>();
            events.add(new LaunchEvent(16, "Flood the pad!", clock));
            events.add(new LaunchEvent(6, "Start engines!", clock));
            events.add(new LaunchEvent(0, "Liftoff!", clock));
            clock.start();
            for (Runnable e : events)
                new Thread(e).start();
    }
}
    interface TimeMonitor {
        int getTime();
        void abortCountDown();
    }

    class CountDownClock extends Thread
            implements TimeMonitor {
            private int t;

        public CountDownClock(int start) {
            this.t = start;
        }

        public void run() {
            boolean aborted = false;
            for (; t >= 0; t--) {
                System.out.println("T minus " + t);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    aborted = true;
                }

                if (Thread.interrupted())
                    aborted = true;

                if (aborted) {
                    System.out.println("Stopping the clock!");
                    break;
                }
            }
        }

        @Override
        public int getTime() {
            return t;
        }

        public synchronized void abortCountDown() {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            for (Thread t : threads)
                t.interrupt();

        }
    }

    class LaunchEvent implements Runnable {
        private int start;
        private String message;
        TimeMonitor tm;

        public LaunchEvent(int start, String message, TimeMonitor monitor) {
            this.start = start;
            this.message = message;
            this.tm = monitor;
        }

        public void run() {
            boolean eventDone = false;
            boolean aborted = false;
            while (!eventDone) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    aborted = true;
                }
                if (tm.getTime() <= start) {
                    System.out.println(this.message);
                    eventDone = true;
                    System.out.println("ABORT!!!");
                    tm.abortCountDown();
                }

                if (Thread.interrupted())
                    aborted = true;
                if (aborted) {
                    System.out.println("Aborting" + message);
                    break;
                }
            }
        }
    }
}