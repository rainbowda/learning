package com.learnJvm.case1_outOfMemoryError;

/**
 * VM Args：-Xss2M （这时候不妨设大些）
 * 谨慎运行
 */
public class JavaVMStackOOM {
 
       private void dontStop() {
              while (true) {
              }
       }
 
       public void stackLeakByThread() {
              while (true) {
                     Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                   dontStop();
                            }
                     });
                     thread.start();
              }
       }
 
       public static void main(String[] args) throws Throwable {
              JavaVMStackOOM oom = new JavaVMStackOOM();
              oom.stackLeakByThread();
       }
}

