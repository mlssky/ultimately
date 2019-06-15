package tv.yixia.basijava;


/**
 * Created by mengliwei on 2019-05-29.
 */
public class ThreadTest {

    static class T1 implements Runnable {
        Object lock;
        int    sum;

        public T1(Object lock, int sum) {
            this.lock = lock;
            this.sum = sum;
        }

        //1 2 3
        //2 1 4 3
        @Override
        public void run() {
            synchronized (lock) {
                for (int i = 2; i <= sum; i += 2) {
                    System.out.println(i);
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    static class T2 implements Runnable {
        Object lock;
        int    sum;

        public T2(Object lock, int sum) {
            this.lock = lock;
            this.sum = sum;
        }

        @Override
        public void run() {
            synchronized (lock) {
                for (int i = 1; i <= sum; i += 2) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(i);

                    if ((sum & 1) == 1 && (i + 2) == sum) { //最后一位是1，奇数
                        System.out.println(i + 2);
                        break;
                    }
                    lock.notify();
                }
            }
        }
    }

    public static class ATask implements Runnable {
        private Object lock1;
        private Object notifyWaitLock;
        public ATask(Object lock1, Object notifyWaitLock) {
            this.lock1 = lock1;
            this.notifyWaitLock = notifyWaitLock;
        }

        @Override
        public void run() {
            synchronized (lock1) {
                int i = 0;
                while (i++ < 10) {
                    System.out.println("A");
                    notifyWaitLock.notify();
                    try {
                        lock1.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class BCTask implements Runnable {

        private Object lock1;
        private Object notifyWaitLock;
        private String info;

        public BCTask(Object lock1, Object notifyWaitLock, String info) {
            this.lock1 = lock1;
            this.notifyWaitLock = notifyWaitLock;
            this.info = info;
        }

        @Override
        public void run() {
            synchronized (lock1) {
                int i = 0;
                while (i++ < 10) {
                    try {
                        lock1.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(info);
                    notifyWaitLock.notify();
                }
            }
        }
    }

    public static void main(String[] args) {

        Object lockA = new Object();
        Object lockB = new Object();
        Object lockC = new Object();

        ATask task = new ATask(lockA, lockB);
        new Thread(task).start();
        BCTask bTask = new BCTask(lockB, lockC, "B");
        new Thread(bTask).start();
        BCTask cTask = new BCTask(lockC, lockA, "C");
        new Thread(cTask).start();



//        final Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("ttt>>>");
//            }
//        });
//        thread.start();
//
//        Thread thread1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (thread.isAlive()) {
//                    try {
//                        thread.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                System.out.println("><<<<<<<");
//            }
//        });
//
//        thread1.start();


//        Thread thread=new Thread();
//        thread.start();
//
//        Thread  thread1=new Thread();
//        thread.start();

//        Object lock = new Object();
//        T1 t1 = new T1(lock, 16);
//        T2 t2 = new T2(lock, 16);
//
//        new Thread(t2).start();
//
//        new Thread(t1).start();

    }

    //AB
    static class P1 implements Runnable {
        int total;
        int pos = 1;

        public P1(int total) {
            this.total = total;
        }

        @Override
        public void run() {
            while (pos <= total) {
                synchronized (this) {
                    System.out.println(Thread.currentThread().getName() + ":" + pos++);
                    notify();
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
