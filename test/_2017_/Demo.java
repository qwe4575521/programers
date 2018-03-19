package _2017_;

/**
 * Created by xucl on 2017-12-04.
 */
public class Demo {
    public static void main(String[] args)
    {
        new Demo().methodA();
    }

    private void methodA(){
        System.out.println("------进入methodA----------");
        methodB();
    }

    private void methodB(){
        System.out.println("------进入methodB----------");
        StackTraceElement elements[] = Thread.currentThread().getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            StackTraceElement stackTraceElement=elements[i];
            String className=stackTraceElement.getClassName();
            String methodName=stackTraceElement.getMethodName();
            String fileName=stackTraceElement.getFileName();
            int lineNumber=stackTraceElement.getLineNumber();
            System.out.println("StackTraceElement数组下标 i="+i+",fileName="
                    +fileName+",className="+className+",methodName="+methodName+",lineNumber="+lineNumber);
        }
    }
}
