package tv.yixia.basijava;

/**
 * Created by mengliwei on 2019-10-29.
 */
public class LC25 {

    public class ListNode {
        int      val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    /**
     * Definition for singly-linked list.
     * public class ListNode {
     * int val;
     * ListNode next;
     * ListNode(int x) { val = x; }
     * }
     */
    public static ListNode reverseKGroup(ListNode head, int k) {
        //
//      1.移到k位置
//      2.
        int index = 1;
        ListNode first, last, pre;
        first = head;
        last = head;
        pre = head;
        while ((last = head.next) != null && index < k) {
            index++;
            pre = last;
        }
        //
        return null;
    }
}
