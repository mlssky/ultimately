package tv.yixia.basijava;

/**
 * Created by mengliwei on 2019-10-29.
 */
public class DPSingle {

    private DPSingle() {
        //no instance
    }

    /**
     * below JDK1.4 broken
     * 指令重排
     */
    private static volatile DPSingle sDPSinglde;

    public static DPSingle getInstance() {
        if (sDPSinglde == null) {
            synchronized (DPSingle.class) {
                if (sDPSinglde == null) {
                    sDPSinglde = new DPSingle();
                }
            }
        }
        return sDPSinglde;
    }

    /**
     * @return
     */
    public static DPSingle getInstanceV2() {
        return SingleHodler.sDPSinglde;
    }

    private static class SingleHodler {
        public static DPSingle sDPSinglde = new DPSingle();
    }

}
