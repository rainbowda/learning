# CountDownLatch案例代码   
## 目录结构
```
+---countDownLatch
|   |   Case.java  案例测试代码
|   |   README.md  说明文档
|   |
|   \---loadingData
|       |   AbstractDataRunnable.java 实现Runnable的抽象类
|       |
|       \---impl 加载数据的实现类
|               BackGroundData.java  
|               GoodsData.java
|               MapData.java    
|               PersonageData.java
```

## 需求
模拟游戏一开始需要加载一些基础数据后才能开始游戏，基础数据加载完可以继续加载其他数据。基础数据包含人物、地图、背景、物品等等。  

## 解决方案
利用`CountDownLatch`来实现，基础数据加载完毕后，`CountDownLatch`计数器进行减一操作。当`CountDownLatch`计数器为0时，表示可以开始游戏。  

### 定义抽象类
定义抽象类`AbstractDataRunnable`并实现`Runnable`接口   

抽象类包含两个属性  
```
private String name;
private CountDownLatch count;
```
通过构造函数初始化两个属性   
```
public AbstractDataRunnable(String name, CountDownLatch count) {
    this.name = name;
    this.count = count;
}
```

定义方法，提供一个抽象方法`handle()`供子类去实现，`getName()`和`afterCountDown()`提供默认的实现。   
```
public String getName() {
    return name;
}

public abstract void handle() throws InterruptedException;

public void afterCountDown(){
    System.out.println(this.getName() + ":CountDownLatch计数减一之后,继续加载其他数据...");
};
```

run方法如下，在调用`handle()`方法之后执行`count.countDown();`，让`CountDownLatch`计数器进行减一操作.计数器减一之后可以继续加载额外的数据，并不影响当前线程    
```
public void run() {
    try {
        System.out.println(this.getName()+" 开始加载...");
        Long l1 = System.currentTimeMillis();
        handle();
        Long l2 = System.currentTimeMillis();
        System.out.println(this.getName()+" 加载完成,花费时间:"+(l2-l1));
    } catch (Exception e){
        e.printStackTrace();
    } finally {
        count.countDown();
    }
    afterCountDown();
}
```
### 定义一些数据加载类
背景数据加载类如下，实现了抽象类`AbstractDataRunnable`的`handle()`方法,在`handle()`方法休眠了2秒   
```
public class BackGroundData extends AbstractDataRunnable {

    public BackGroundData(String name, CountDownLatch count) {
        super(name, count);
    }

    @Override
    public void handle() throws InterruptedException {
        //模拟加载时间，2秒
        Thread.sleep(2000);
    }
}
```

其他数据加载类代码就不贴出来了，睡眠的时间不同而已

### 开始游戏
开始游戏类如下，通过构造函数传入`CountDownLatch`计数器，然后在run方法中执行`count.await();`方法进行等待基础数据加载完毕。
```
class StartGame implements Runnable{
    private CountDownLatch count;

    public StartGame(CountDownLatch count) {
        this.count = count;
    }

    @Override
    public void run() {
        try {
            System.out.println("开始加载基础数据...");
            Long l1 = System.currentTimeMillis();
            count.await();
            Long l2 = System.currentTimeMillis();
            System.out.println("基础数据加载完毕，总共花费时长:"+(l2-l1)+".可以开始游戏...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
### 测试
```
public static void main(String[] args) throws IOException {
    CountDownLatch count = new CountDownLatch(4);

    //主线程
    Thread startGameThread = new Thread(new StartGame(count));
    startGameThread.start();

    //加载数据线程
    Thread mapThread = new Thread(new MapData("地图",count));
    Thread goodsThread = new Thread(new GoodsData("物品",count));
    Thread personageThread = new Thread(new PersonageData("人物",count));
    Thread backGroundThread = new Thread(new BackGroundData("背景",count));


    mapThread.start();
    goodsThread.start();
    personageThread.start();
    backGroundThread.start();

    System.in.read();
}
```
测试结果内容
```
开始加载基础数据...
地图 开始加载...
物品 开始加载...
人物 开始加载...
背景 开始加载...
人物 加载完成,花费时间:1000
人物:CountDownLatch计数减一之后,继续加载其他数据...
背景 加载完成,花费时间:2000
背景:CountDownLatch计数减一之后,继续加载其他数据...
物品 加载完成,花费时间:2501
物品:CountDownLatch计数减一之后,继续加载其他数据...
地图 加载完成,花费时间:3001
地图:CountDownLatch计数减一之后,继续加载其他数据...
基础数据加载完毕，总共花费时长:3003.可以开始游戏...
```