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
import com.easydblib.info.DBInfo;

import java.util.ArrayList;
import java.util.List;

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

    @OnClick({R.id.createBtn, R.id.queryBtn, R.id.queryWhereBtn, R.id.queryPageBtn, R.id.updateBtn, R.id.deleteBtn, R.id.countBtn})
    public void onClick(View view) {
        List<SimpleData> list;
        switch (view.getId()) {
            case R.id.createBtn:
                //增加
                List<SimpleData> addList = new ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    addList.add(new SimpleData(i,"信息"+i));
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
                list = dao.query(DBInfo.get().where("group1",true).where("group1",true).order("id", false));
                printList(list);
                break;
            case R.id.queryPageBtn:
                //分页查询-每页5条
                DBInfo info = DBInfo.get().limit(5);
                list = dao.queryLimit(info);//第一页查询
                printList(list);
                List<SimpleData> listLimit = dao.queryLimit(info);//第二页查询
                printList(listLimit);
                break;
            case R.id.updateBtn:
                //更新
                list = dao.queryForAll();
                if(!list.isEmpty()){
                    SimpleData data = list.get(0);
                    data.description = "更新内容";
                    if(dao.update(data)==1){
                        tvResult.setText("更新成功");
                    }else{
                        tvResult.setText("更新失败");
                    }
                }
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
                long num = dao.countOf(DBInfo.get().where("group1", true));
                tvResult.setText("总条数："+num);
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
