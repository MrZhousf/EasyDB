package com.easydb.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easydb.R;
import com.easydb.core.BaseActivity;
import com.easydb.demo.model.SimpleData;
import com.easydb.util.LogUtil;
import com.easydblib.callback.EasyRun;
import com.easydblib.dao.BaseDao;
import com.easydblib.helper.DBHelper;
import com.easydblib.info.OrderInfo;
import com.easydblib.info.WhereInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Bind(R.id.tvResult)
    TextView tvResult;

    BaseDao<SimpleData> dao;

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = DBHelper.get().dao(SimpleData.class);
    }

    @OnClick({R.id.createBtn, R.id.queryBtn, R.id.queryWhereBtn, R.id.queryPageBtn,
            R.id.updateBtn, R.id.deleteBtn, R.id.countBtn, R.id.isExistBtn,
            R.id.clearTableBtn, R.id.likeBtn, R.id.batchBtn, R.id.asyncBtn,
            R.id.dropBtn })
    public void onClick(View view) {
        List<SimpleData> list;
        switch (view.getId()) {
            case R.id.createBtn:
                //增加
                List<SimpleData> addList = new ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    if(i % 2 == 0){
                        addList.add(new SimpleData(i,"信息"+i));
                    }else{
                        addList.add(new SimpleData(i,"我是"+i));
                    }
                }
                int line = dao.add(addList);
                tvResult.setText("增加总条数："+line);
                break;
            case R.id.queryBtn:
                //查询
                list = dao.queryAll(OrderInfo.get().order("id",false));
                printList(list);
                break;
            case R.id.queryWhereBtn:
                //多条件查询并排序
                list = dao.query(WhereInfo.get().between("index",1,18).equal("group1",true).order("id", false));
                printList(list);
                break;
            case R.id.queryPageBtn:
                //分页查询-每页5条
                WhereInfo info = WhereInfo.get().limit(5);
                list = dao.queryLimit(info);//第一页查询
                printList(list);
                List<SimpleData> listLimit = dao.queryLimit(info);//第二页查询
                printList(listLimit);
                break;
            case R.id.updateBtn:
                //更新-采用事务方式
                dao.callInTransaction(new Callable<SimpleData>() {
                    @Override
                    public SimpleData call() throws Exception {
                        List<SimpleData> list = dao.queryAll();
                        if(!list.isEmpty()){
                            SimpleData data = list.get(0);
                            data.description = "更新内容";
                            data.index = 500;
                            dao.update(data);
                        }
                        return null;
                    }
                });
                break;
            case R.id.deleteBtn:
                //删除
                list = dao.queryAll();
                if(!list.isEmpty()){
                    if(dao.delete(list.get(0))==1){
                        tvResult.setText("删除成功");
                    }else{
                        tvResult.setText("删除失败");
                    }
                }
                break;
            case R.id.countBtn:
                //条目统计
                long num = dao.countOf(WhereInfo.get().equal("group1", true));
                tvResult.setText("总条数："+num);
                break;
            case R.id.isExistBtn:
                //是否存在
                boolean isExist = dao.isExist(WhereInfo.get().equal("description","信息1"));
                tvResult.setText(isExist?"存在":"不存在");
                break;
            case R.id.clearTableBtn:
                //清空表
                int clear = dao.clearTable();
                tvResult.setText("清空表："+clear);
                break;
            case R.id.likeBtn:
                //模糊查询
                list = dao.query(WhereInfo.get().like("description","我是%"));
                printList(list);
                break;
            case R.id.batchBtn:
                //批处理
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
                break;
            case R.id.asyncBtn:
                //异步任务
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
                break;
            case R.id.dropBtn:
                //删除表
                dao.dropTable();
                break;
        }
    }


    void printList(List<SimpleData> list) {
        StringBuilder builder = new StringBuilder();
        for (SimpleData d : list) {
            builder.append(d.toString());
            builder.append("\n");
        }
        LogUtil.d(TAG, builder.toString());
        tvResult.setText(builder.toString());
    }


}
