# EasyDB
基于ORMLite封装的数据库操作工具类——致力于最简洁的数据库操作API。
由于目前流行的数据框架（如Realm、GreenDAO、ObjectBox）在做项目模块架构时都无法使得表模型分散，表管理统一的架构设计，
所以本框架还是从SQLite出发，综合考量Android Studio应用架构设计思想通过技术封装以实现该目标。

## 功能点
* 支持Android Studio多个Module表模型分散，表管理统一
* 支持自定义数据库路径：SD卡或系统
* 支持主键、索引
* 支持增、删、改、查
* 支持多条件查询、排序、统计、清空、是否存在
* 支持批处理、数据库事务操作
* 支持模糊查询like、between、equal、>、<、>=、<=、<>
* 支持分页查询，分页查询只需要定义每页条数即可
* 支持批量处理
* 支持异步任务操作
* 支持数据库升级，使数据库升级更为简洁
* 完整的日志与异常处理
* Android Studio自动生成表字段别名代码
* 后续优化中...


## 引用方式
### Maven
```
<dependency>
  <groupId>com.zhousf.lib</groupId>
  <artifactId>easydb</artifactId>
  <version>1.7.9</version>
  <type>pom</type>
</dependency>
```
### Gradle
```
compile 'com.zhousf.lib:easydb:1.7.9'
```

## 提交记录
* 2016-12-15 项目提交
* 2016-12-20 增加批处理、事务操作功能
* 2016-12-23 增加异步任务功能
* 2016-12-27 扩展更新、删除操作
* 2017-05-23 优化结构，采用动态代理方式
* 2017-06-12 增加自定义数据库路径功能
* 2018-01-11 支持Android Studio多个Module表模型分散，表管理统一

## 项目演示DEMO
项目中已包含所有支持业务的demo，详情请下载项目参考源码。
所有集成过程只需2步：1、Application初始化；2、编写表模型

## Application中配置
```
        DBHelper.builder()
                .setDbPath(Environment.getExternalStorageDirectory() + "/easy_db")//数据库保存路径
                .setDbName("easy")//数据库名称
                .setDbVersion(1)//数据库版本号
                .showDBLog(true)//显示数据库操作日志
                .setLogTAG("EASY_DB")//日志显示标识
                .build(this);
        DBHelper.builder().onUpgrade(new IUpgrade() {
            @Override
            public void upgrade(DBHelper dbHelper, int oldVersion, int newVersion) throws SQLException {
                Log.d("upgrade","oldVersion="+oldVersion+",newVersion="+newVersion);
                if(oldVersion < 2){
                    //增加字段ext
                    dbHelper.addColumn(SimpleData.class,SimpleData._ext,String.class,"100");
                }
            }
        });
```


## 数据库操作方法
```
//获取数据库操作接口
BaseDao<SimpleData> dao = DBHelper.get().dao(SimpleData.class);

//增加
int line = dao.add(new SimpleData(1,"信息1"));

//查询所有
List<SimpleData> list = dao.queryAll();

//多条件查询并排序
List<SimpleData> list = dao.query(WhereInfo.get().between(SimpleData._index,1,18)
    .equal("group1",true).order(SimpleData._id, false));
 
//分页查询-每页5条
WhereInfo info = WhereInfo.get().limit(5);
List<SimpleData> list = dao.queryLimit(info);//第一页查询
list = dao.queryLimit(info);//第二页查询

//更新
dao.update(data)

//删除
dao.delete(data)

//条目统计
long num = dao.countOf(WhereInfo.get().equal(SimpleData._group1, true));

//是否存在
boolean isExist = dao.isExist(WhereInfo.get().equal(SimpleData._description,"信息2"));

//清空表
int line = dao.clearTable();

//批处理-批量操作消耗性能最低
dao.callBatchTasks(new Callable<SimpleData>() {
    @Override
    public SimpleData call() throws Exception {
        List<SimpleData> list = dao.queryAll();
        for(SimpleData data : list){
            data.description += "_批处理";
            dao.update(data);
        }
        return null;
    }
});

//更新-采用事务方式，自动回滚
dao.callInTransaction(new Callable<SimpleData>() {
    @Override
    public SimpleData call() throws Exception {
        List<SimpleData> list = dao.queryAll();
        if(!list.isEmpty()){
            SimpleData data = list.get(0);
            data.description = "更新内容";
            dao.update(data);
        }
        return null;
    }
});

//删除表
int clear = dao.dropTable();

/**
 * 异步任务方式一：在Activity/Fragment中尽量采用异步任务操作数据库
 * 异步任务可以重写run方法和onMainThread方法，可根据业务自定义重写方式
 **/
dao.asyncTask(new EasyRun<List<SimpleData>>(){
    //该方法在异步线程中执行
    @Override
    public List<SimpleData> run() throws Exception {
        return dao.queryAll();
    }
    //该方法在UI线程中执行
    @Override
    public void onMainThread(List<SimpleData> data) throws Exception {
        printList(data);
    }
});
//异步任务方式二
dao.asyncTask(new EasyRun<Object>(){
    @Override
    public Object run() throws Exception {
        return dao.queryAll();
    }

    @Override
    public void onMainThread(Object data) throws Exception {
        printList((List<SimpleData>)data);
    }
});
//异步任务方式三
dao.asyncTask(new EasyRun<SimpleData>(){
    @Override
    public SimpleData run() throws Exception {
        return dao.queryAll().get(0);
    }

    @Override
    public void onMainThread(SimpleData data) throws Exception {
        tvResult.setText(data.toString());
    }
});


```

## 数据库操作方法
支持Android Studio多个Module表模型分散，表管理统一时请在表类中增加@TableModel注解，这样不同module的表都可以存放在自己模块下，
框架会自动注册应用中所有module的表。通过AndroidStudio插件自动生成字段别名，插件在根目录中AndroidPlugins.jar。
安装插件：打开AndroidStudio -> File -> Settings -> Plugins -> Install plugin from disk -> Restart Android Studio
使用插件自动生成代码：AndroidStudio -> Code -> Create Field

```java
@TableModel
public class SimpleData {

	//以下字段别名可以通过AndroidStudio插件自动生成
	public final static String _id = "id";
	public final static String _index = "index";
	public final static String _description = "description";
	public final static String _myDouble = "myDouble";
	public final static String _myFloat = "myFloat";
	public final static String _myLong = "myLong";
	public final static String _date = "date";
	public final static String _group1 = "group1";
	public final static String _group2 = "group2";
	public final static String _ext = "ext";
	public final static String _father = "father";

	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField(index = true)
	public int index;
	@DatabaseField
	public String description;
	@DatabaseField
	public Date date;
	@DatabaseField
	public boolean group1;
	@DatabaseField
	public boolean group2;
	@DatabaseField
	public String ext;

	//必须有无参构造方法
	public SimpleData() {
	}

	public SimpleData(int index,String description) {
		this.date = new Date(System.currentTimeMillis());
		this.index = index;
		this.description = description;
		this.group1 = ((index % 2) == 0);
		this.group2 = ((index % 4) == 0);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(", ").append("index=").append(index);
		sb.append(", ").append("description=").append(description);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
		sb.append(", ").append("date=").append(dateFormatter.format(date));
		sb.append(", ").append("group1=").append(group1);
		sb.append(", ").append("group2=").append(group2);
		sb.append(", ").append("ext=").append(ext);
		return sb.toString();
	}
}
```

## 数据库操作接口
```java
package com.easydblib.dao;
import com.easydblib.callback.EasyRun;
import com.easydblib.info.WhereInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 数据库操作接口
 * @author : zhousf
 */
public interface BaseDao<T> {

    /**
     * 增加
     * @param model 映射类
     * @return 影响行数
     */
    int add(T model);

    /**
     * 增加集合
     * @param list 映射类集合
     * @return 影响行数
     */
    int add(List<T> list);

    /**
     * 增加或更新
     * @param model 映射类
     * @return 影响行数
     */
    int addOrUpdate(T model);

    /**
     * 不存在时增加
     * @param model 映射类
     * @return 增加的对象
     */
    T addIfNotExists(T model);

    /**
     * 删除
     * @param model 映射类
     * @return 影响行数
     */
    int delete(T model);

    /**
     * 删除集合
     * @param list 映射类集合
     * @return 影响行数
     */
    int delete(List<T> list);

    /**
     * 根据条件删除
     * @param whereInfo 查询信息体
     * @return 影响行数
     */
    int delete(WhereInfo whereInfo);

    /**
     * 更新
     * @param model 映射类
     * @return 影响行数
     */
    int update(T model);

    /**
     * 更新-根据查询条件进行更新，只更新第一条数据，若无则添加
     * @param model 映射类
     * @param whereInfo 查询信息体
     * @return 影响行数
     */
    int update(T model,WhereInfo whereInfo);

    /**
     * 查询所有
     * @return 映射类集合
     */
    List<T> queryAll();

    /**
     * 查询所有并排序
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryAll(WhereInfo whereInfo);

    /**
     * 多条件查询并排序
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    List<T> query(WhereInfo whereInfo);


    /**
     * 分页查询
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryLimit(WhereInfo whereInfo);

    /**
     * 自定义查询
     * @param queryBuilder 查询构建
     * @return 映射类集合
     */
    List<T> query(QueryBuilder<T, Integer> queryBuilder);

    /**
     * 统计条目数
     * @return 条目数
     */
    long countOf();

    /**
     * 统计条目数
     * @param whereInfo 查询信息体
     * @return 条目数
     */
    long countOf(WhereInfo whereInfo);

    /**
     * 是否存在
     * @param whereInfo  查询信息体
     * @return true 存在  false 不存在
     */
    boolean isExist(WhereInfo whereInfo);

    /**
     * 执行原生的SQL语句
     * @param statement SQL语句
     * @param arguments 参数值-占位符?的值
     * @return 影响行数
     */
    int executeRaw(String statement, String... arguments);

    /**
     * 清空表
     * @return 条目数
     */
    int clearTable();

    /**
     * 删除表
     * @return 条目数
     */
    int dropTable();

    /**
     * 获取数据表DAO
     * @return dao
     */
    Dao<T, Integer> fetchDao();

    /**
     * 获取表名
     * @return 表名
     */
    String getTableName();

    /**
     * 执行事务
     * @param callable 事务回调
     */
    void callInTransaction(Callable<T> callable);

    /**
     * 批处理-大量数据库操作时请采用该方法（性能最优）
     * @param callable 回调
     */
    <CT> CT callBatchTasks(Callable<CT> callable);

    /**
     * 异步执行
     * @param easyRun 异步run
     */
    <T> void asyncTask(EasyRun<T> easyRun);

}

```

## 数据库操作代理类
```java
package com.easydblib.handler;
import android.util.Log;
import com.easydblib.EasyDBConfig;
import com.easydblib.dao.RealBaseDao;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author : zhousf
 * @description : 数据库操作代理类：主要进行预处理、日志打印
 * @date : 2017/5/23.
 */
public class EasyDBProxyHandler<T> implements InvocationHandler {

    private Object obj;
    private Dao<T, Integer> dao;
    private Class<T> mClass;
    private String databaseName;

    public EasyDBProxyHandler(Dao<T, Integer> dao, Class<T> mClass, String databaseName) {
        this.dao = dao;
        this.mClass = mClass;
        this.databaseName = databaseName;
    }

    public RealBaseDao<T> getProxy(Object targetObject) {
        this.obj = targetObject;
        Object proxy = Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                targetObject.getClass().getInterfaces(), this);
        return (RealBaseDao<T>)proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = getTime();
        doBefore();
        Object result = method.invoke(obj, args);
        doAfter(method,result,startTime);
        return result;
    }

    /**
     * 执行前操作
     */
    private void doBefore(){
        prepareDeal();
    }

    /**
     * 执行后操作
     */
    private void doAfter(Method method, Object result, long startTime){
        if(result != null){
            String methodName = method.getName();
            if(result instanceof Integer || result instanceof Long){
                String line = String.valueOf(result);
                doLog(methodName+"["+(getTime()-startTime)+"ms] 影响行数："+line);
            }else if(result instanceof List){
                int line = ((List) result).size();
                doLog(methodName+"["+(getTime()-startTime)+"ms] 影响行数："+line);
            }else if(result instanceof Boolean){
                String res = String.valueOf(result);
                doLog(methodName+"["+(getTime()-startTime)+"ms] ："+res);
            }else {
                doLog(methodName+"["+(getTime()-startTime)+"ms] ");
            }
        }
    }

    /**
     * 预处理
     */
    private void prepareDeal(){
        checkTable();
    }

    /**
     * 检查数据表
     */
    private void checkTable(){
        try {
            TableUtils.createTableIfNotExists(dao.getConnectionSource(),mClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getTime(){
        return System.currentTimeMillis();
    }

    /**
     * 打印日志
     */
    private void doLog(String msg){
        if(EasyDBConfig.showDBLog)
            Log.d(EasyDBConfig.logTAG,msg+" | "+mClass.getSimpleName()+" | "+databaseName);
    }
}
```

## 相关截图
### 基本操作界面
![](https://github.com/MrZhousf/EasyDB/blob/master/pic/1.jpg?raw=true)

## 有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* QQ: 424427633


## 感激
感谢以下的项目,排名不分先后

* [ORMLite](https://github.com/j256/ormlite-android) 