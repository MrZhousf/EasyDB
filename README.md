# EasyDB
基于ORMLite封装的数据库操作工具类

##功能点
* 支持主键、索引
* 支持增、删、改、查
* 支持多条件查询、排序、统计、清空、是否存在
* 支持批处理、数据库事务操作
* 支持模糊查询like、between、equal、>、<、>=、<=、<>
* 支持分页查询，分页查询只需要定义每页条数即可
* 支持批量处理
* 支持数据库升级，使数据库升级更为简洁
* 完整的日志
* 后续优化中...


##引用方式
###Maven
```java
<dependency>
  <groupId>com.zhousf.lib</groupId>
  <artifactId>easydb</artifactId>
  <version>1.5.1</version>
  <type>pom</type>
</dependency>
```
###Gradle
```java
compile 'com.zhousf.lib:easydb:1.5.1'
```

##提交记录
* 2016-12-15 项目提交
* 2016-12-20 增加批处理、事务操作功能

##项目演示DEMO
项目中已包含所有支持业务的demo，详情请下载项目参考源码。

##Application中配置
```java
EasyDBConfig.init()
        .showDBLog(true)//显示数据库操作日志
        .registerHelper(EasyDBHelper.get())//注册数据库Helper-预实例化
        .build();
```

##自定义数据库单例
单例模式防止频繁打开关闭数据库，从而导致数据操作性能下降，甚至出现一些异常错误。
自定义数据库单例继承BaseDBHelper，BaseDBHelper进行数据库操作的缓存管理
```java
// 数据库辅助类
public class EasyDBHelper extends BaseDBHelper {

	//版本号
	private static final int DB_VERSION = 2;

	//数据库名称
	private static final String DB_NAME = "easy_android.db";

	//数据表清单-数据库与表的关系更直观
	private static final Class<?>[] tables = {
		SimpleData.class
	};

	private static EasyDBHelper helper = null;

	public static EasyDBHelper get(){
		if(null == helper){
			synchronized (EasyDBHelper.class){
				if(null == helper){
					helper = new EasyDBHelper();
				}
			}
		}
		return helper;
	}

	private EasyDBHelper() {
		super(BaseApplication.getApplication(),DB_NAME, null, DB_VERSION, tables);
	}

	@Override
	protected BaseDBHelper initHelper() {
		return get();
	}

	@Override
	protected boolean upgrade(int oldVersion, int newVersion) throws SQLException {
	    //数据库升级操作
		if(oldVersion < 2){
			//增加字段ext
			getDao(SimpleData.class).executeRaw("ALTER TABLE'simpledata' ADD COLUMN ext TEXT DEFAULT 'default';");
		}
		return true;
	}

}

```

##数据库操作方法
```java
//获取数据库操作接口
BaseDao<SimpleData> dao = EasyDBHelper.get().dao(SimpleData.class);

//增加
int line = dao.create(new SimpleData(1,"信息1"));

//查询所有
List<SimpleData> list = dao.queryForAll();

//多条件查询并排序
List<SimpleData> list = dao.query(WhereInfo.get().between("index",1,18).equal("group1",true).order("id", false));
 
//分页查询-每页5条
WhereInfo info = WhereInfo.get().limit(5);
List<SimpleData> list = dao.queryLimit(info);//第一页查询
list = dao.queryLimit(info);//第二页查询

//更新
dao.update(data)

//删除
dao.delete(data)

//条目统计
long num = dao.countOf(WhereInfo.get().equal("group1", true));

//是否存在
boolean isExist = dao.isExist(WhereInfo.get().equal("description","信息2"));

//清空表
int line = dao.clearTable();

//批处理-批量操作消耗性能最低
dao.callBatchTasks(new Callable<SimpleData>() {
    @Override
    public SimpleData call() throws Exception {
        List<SimpleData> list = dao.queryForAll();
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
        List<SimpleData> list = dao.queryForAll();
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

```

##数据库操作接口
```java
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
       int create(T model);
   
       /**
        * 增加集合
        * @param list 映射类集合
        * @return 影响行数
        */
       int create(List<T> list);
   
       /**
        * 删除
        * @param model 映射类
        * @return 影响行数
        */
       int delete(T model);
   
       /**
        * 更新集合
        * @param list 映射类集合
        * @return 影响行数
        */
       int delete(List<T> list);
   
       /**
        * 更新
        * @param model 映射类
        * @return 影响行数
        */
       int update(T model);
   
       /**
        * 查询所有
        * @return 映射类集合
        */
       List<T> queryForAll();
   
       /**
        * 查询所有并排序
        * @param whereInfo 查询信息体
        * @return 映射类集合
        */
       List<T> queryForAll(WhereInfo whereInfo);
   
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
        * 执行事务
        * @param callable 事务回调
        */
       void callInTransaction(Callable<T> callable);
   
       /**
        * 批处理-大量数据库操作时请采用该方法（性能最优）
        * @param callable 回调
        */
       <CT> CT callBatchTasks(Callable<CT> callable);

}
```

##相关截图
###基本操作界面
![](https://github.com/MrZhousf/EasyDB/blob/master/pic/1.jpg?raw=true)

##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* QQ: 424427633


##感激
感谢以下的项目,排名不分先后

* [ORMLite](https://github.com/j256/ormlite-android) 