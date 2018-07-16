package com.aisafer.minasocket.testmain;

/**
 * main方法测试
 *
 * @Author:weiyuanlong
 * @Date: Created in 2018-06-11 10:10:52
 * @Modified By:
 */
public class TestMain {

    public static void main(String[] args) {
        method_02();

    }

    private static void method_02() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while(threadGroup.getParent() != null){
            threadGroup = threadGroup.getParent();
        }
        int totalThread = threadGroup.activeCount();
        System.out.println(totalThread);
    }

}
