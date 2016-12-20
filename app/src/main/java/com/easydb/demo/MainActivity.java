package com.easydb.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.easydb.R;
import com.easydb.core.EasyDBHelper;
import com.easydb.demo.model.SimpleData;
import com.easydb.util.LogUtil;
import com.easydblib.dao.BaseDao;
import com.easydblib.info.WhereInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Bind(R.id.tvResult)
    TextView tvResult;

    BaseDao<SimpleData> dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dao = EasyDBHelper.get().dao(SimpleData.class);
    }

    @OnClick({R.id.createBtn, R.id.queryBtn, R.id.queryWhereBtn, R.id.queryPageBtn,
            R.id.updateBtn, R.id.deleteBtn, R.id.countBtn, R.id.isExistBtn,
            R.id.clearTableBtn, R.id.likeBtn, R.id.batchBtn})
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
                int line = dao.create(addList);
                tvResult.setText("增加总条数："+line);
                break;
            case R.id.queryBtn:
                //查询
                list = dao.queryForAll();
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
                        List<SimpleData> list = dao.queryForAll();
                        if(!list.isEmpty()){
                            SimpleData data = list.get(0);
                            data.description = "更新内容";
                            dao.update(data);
                        }
                        return null;
                    }
                });
                break;
            case R.id.deleteBtn:
                list = dao.queryForAll();
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
                boolean isExist = dao.isExist(WhereInfo.get().equal("description","信息2"));
                tvResult.setText(isExist?"存在":"不存在");
                break;
            case R.id.clearTableBtn:
                int clear = dao.clearTable();
                tvResult.setText("清空表："+clear);
                break;
            case R.id.likeBtn:
                list = dao.query(WhereInfo.get().like("description","我是%"));
                printList(list);
                break;
            case R.id.batchBtn:
                //批处理
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

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

}
