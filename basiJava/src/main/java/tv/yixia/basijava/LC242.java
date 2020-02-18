package tv.yixia.basijava;

/**
 * Created by mengliwei on 2019-10-29.
 */
public class LC242 {
    //异位词

    public static boolean checkIs(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false;
        }
        char[] s1Arr = s1.toCharArray();
        char[] s2Arr = s2.toCharArray();
        int[] tmp = new int[26];
        for (int i = 0, n = s1Arr.length; i < n; i++) {
            tmp[s1Arr[i] - 'a']++;
            tmp[s2Arr[i] - 'a']--;
        }
        for (int i = 0, n = tmp.length; i < n; i++) {
            if (tmp[i] != 0) {
                return false;
            }
        }
        return true;
    }
}
