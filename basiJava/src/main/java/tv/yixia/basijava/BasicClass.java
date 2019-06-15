package tv.yixia.basijava;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;


//IM
//userID:                                      用户
//user： 用户列表                              A->B
//                                             B->A
//friend:好友列表
//消息:发送ID-接收用户ID（群聊，单聊）
//


public class BasicClass {

    public static void main(String[] args) {


        String s = "abbabcdefg";
        s.contains("d");
        //abb
        //13
        int n = s.length(), ans = 0;
        Map<Character, Integer> map = new HashMap<>();// current index of character
        // try to extend the range [i, j]
        for (int j = 0, i = 0; j < n; j++) {  //[i,j)  //i-3
            if (map.containsKey(s.charAt(j))) {
                //窗口移到下一个位置
                i = Math.max(map.get(s.charAt(j)), i);
            }//
            ans = Math.max(ans, j - i + 1);//记录当前有效字符串的长度
            map.put(s.charAt(j), j + 1);
        }
        System.out.println("i=" + ans);


    }


    /**
     * 快速排序
     */
    public static void testQuckSortV2() {
        int[] arr = new int[]{12, -25, 3, 1, 0, -23, 98, 19, 20, 10, 89, 4, -19};
        System.out.print("<<<<<<<");
        quickV2(arr, 0, arr.length - 1);
        System.out.print("===");
        for (int i = 0, n = arr.length; i < n; i++) {
            System.out.print(arr[i] + ",");
        }
    }

    public static void quickV2(int[] arr, int low, int high) {
        if (low < high) {
            int middle = getMiddleV2(arr, low, high);
            quickV2(arr, low, middle - 1);
            quickV2(arr, middle + 1, high);
        }
    }


    public static int getMiddleV2(int[] arr, int low, int high) {
        int middle = arr[low];
        while (low < high) {

            //将高位小于中间值的移到低位
            while (low < high && arr[high] > middle) {
                high--;
            }
            arr[low] = arr[high];

            while (low < high && arr[low] < middle) {
                low++;
            }
            arr[high] = arr[low];
        }
        arr[low] = middle;

        return low;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tree
    ///////////////////////////////////////////////////////////////////////////


    public static void testTree() {
        BinaryTreeNode node = new BinaryTreeNode(1);
        node.left = new BinaryTreeNode(2, new BinaryTreeNode(4), new BinaryTreeNode(5));
        node.right = new BinaryTreeNode(3, new BinaryTreeNode(6), new BinaryTreeNode(7));

        //\深度优先：前序/中/后
        LinkedList<BinaryTreeNode> queue = new LinkedList<>();
        queue.push(node);
        BinaryTreeNode prev = null;
        while (!queue.isEmpty()) {
            BinaryTreeNode node1 = queue.peek();
            if ((node1.left == null && node1.right == null) || (prev != null && (node1.left == prev || node1.right == prev))) {
                queue.pop();
                System.out.print(node1.value + ",");
                prev = node1;
            } else {
                if (node1.right != null) {
                    queue.push(node1.right);
                }
                if (node1.left != null) {
                    queue.push(node1.left);
                }
            }
        }


//        //前：1,2,4,5,3,6,7,
//        //中：4,2,5,1,6,3,7
        //后：4,5,2,6,7,3,1,
//        //root node ->left node->right node
//        BinaryTreeNode rootNode = node;
//
//        while (rootNode != null || !queue.isEmpty()) {
//
//            if (rootNode != null) {
//                //前
////                System.out.print(rootNode.value + ",");
//                queue.push(rootNode);
//                rootNode = rootNode.left;
//            } else {
//                //无left node
//                BinaryTreeNode top = queue.pop();
//                //中
//                System.out.print(top.value + ",");
//                rootNode = top.right;
//            }
//
//        }

    }


    public static class BinaryTreeNode {
        int            value;
        BinaryTreeNode left;
        BinaryTreeNode right;

        public BinaryTreeNode(int value) {
            this.value = value;
        }

        public BinaryTreeNode(int value, BinaryTreeNode left, BinaryTreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

    }
    ///////////////////////////////////////////////////////////////////////////
    // 快速排序
    ///////////////////////////////////////////////////////////////////////////

    public static void testQuick() {
        int[] arr = new int[]{2, 9, 0, -12, 89, 2, 109, 10, -1, 8, 2, 1};
        quickSort(arr, 0, arr.length - 1);
        for (int i = 0, n = arr.length; i < n; i++) {
            System.out.print(arr[i] + ",");
        }
    }

    static void quickSort(int[] arr, int start, int end) {
        if (start < end && arr != null) {
            int middle = getMiddle(arr, start, end);
            quickSort(arr, start, middle - 1);
            quickSort(arr, middle + 1, end);
        }

    }

    private static int getMiddle(int[] arr, int low, int high) {
        int tmp = arr[low];
        while (low < high) {
            while (low < high && arr[high] >= tmp) {
                high--;
            }
            arr[low] = arr[high];

            while (low < high && arr[low] <= tmp) {
                low++;
            }
            arr[high] = arr[low];
        }
        arr[low] = tmp;

        return low;
    }


//    一面
//    介绍LeakCanary源码；
//    介绍APK瘦身，瘦身了多少；
//    ClassLoader相关场景：apk中有两个v7包的AppCompatActivity先加载哪个？为什么？
//    介绍内存优化所做的工作；
    //a b c
//    算法：字符串数组["abc","bac","abe","bae","cab"...],里边每个字符串长度不定，但保证都是'a-z'和'A-Z',请写出算法，
    //abc
//    //HashMap s=new Map();
    //s.put(ss,"dd"); //n*n

//    将包含相同的字符组成的字符串进行分组。如上述输出[["abc","bac","cab"],["bae","abe"]...]，并回答实现的算法的复杂度是多少。
//    字节码层面解释，为什么非静态内部类能够访问外部类的私有方法和成员变量（答案：会在外部类生成静态acess开头的方法）？Android有65K方法的问题，怎么样能够避免生成上述方法（答：修改访问修饰符为public/protected/default）？
//    事件分发场景：两个ViewPager嵌套，内外层大小完全一致，但外部纵向，内部横向，问此时会出现什么异常吗？怎么处理？
//    如果先在外部纵向滑动的过程中又进行横向滑动，会发生什么情况？怎么避免？
//    事件分发场景：一个FrameLayout,内部一个Button，它们两个都设置了点击事件，分别点击不同的区域，会发生什么？为什么？如果Button换成TextView呢？
//    ArrayList里有两个重载方法remove(int index),remove(Object obj),现在有个ArrayList array（1，2，3，4），调用array.remove(2)，数组里会变成什么样？为什么？如果想要把3给移除掉，应该怎么做？
//    Android里动画的实现有几种？区别是什么？各自使用的场景是什么？
//    MVP与MVC的区别是什么？
    //M P<->V
    //MVC model   V-layout   - C(控制)
    //M<--C<---V
    //-------->V
    //    线程创建太多导致的OOM，有什么办法可以避免？
//    Kotlin知道吗？了解多少？
//    插入字节码的方式有哪些？
//    C++程序题输出结果（考察方法内申请的局部变量在方法结束后会怎样，不会，题也没记住，跟指针相关）
//    除了Android以外，平时还关注哪方面技术？
//    平时学习通过哪些途径？
//    二面
//            介绍EventBus源码
//    LruCache原理，get操作发生里什么？算法复杂度是多少？调整双向链表的算法复杂度是多少？o(1)

//    为什么登录页的启动模式要设计成SingleTop，而不是SingleTask或SingleInstance？
//    算法：计算一个ViewGroup下边有几个View，不包含自身
//    怎么去发现内存抖动，当从AS的工具中直观的观察不到波动以后？
//    MVC与MVP的区别？
//    主线程的Looper为什么不会导致阻塞？如果只让用Java实现，怎么去做？
//    乐观锁和悲观锁是什么？乐观锁是怎么实现的？各自的应用场景是什么？
//    三面
//    IdleHandler何时被执行？原本耗时的操作，放到IdleHandler里合适吗？为什么？该怎么优化？
//    头条的主页是一个RecyclerView,如果滑动时发生卡顿了，你觉得卡顿会是哪儿？为什么？加载图片等是放在异步线程里做的，为什么还会卡顿主线程？怎么优化？

}
