package bravin.shi.com.multinested;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv1;
//    private RecyclerView rv2;
//    private RecyclerView rv3;

    private ContentAdapter adapter1;
    private ContentAdapter1 adapter2;
    private ContentAdapter2 adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv1 = findViewById(R.id.rv1);
//        rv2 = findViewById(R.id.rv2);
//        rv3 = findViewById(R.id.rv3);

        rv1.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        rv2.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        rv3.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter1 = new ContentAdapter(MainActivity.this);
//        adapter2 = new ContentAdapter1(MainActivity.this);
//        adapter3 = new ContentAdapter2(MainActivity.this);

        rv1.setAdapter(adapter1);
//        rv2.setAdapter(adapter2);
//        rv3.setAdapter(adapter3);

        initData();
    }

    private void initData() {
        List<DataBean> list1 = new ArrayList<>();
        List<DataBean> list2 = new ArrayList<>();
        List<DataBean> list3 = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            DataBean dataBean = new DataBean();
            dataBean.setName("第一个RecyclerView: " + i);
            list1.add(dataBean);
        }

        for (int i = 0; i < 12; i++) {
            DataBean dataBean = new DataBean();
            dataBean.setName("第二个: " + i);
            list2.add(dataBean);
        }

        for (int i = 0; i < 6; i++) {
            DataBean dataBean = new DataBean();
            dataBean.setName("第三个33333333: " + i);
            list3.add(dataBean);
        }

        adapter1.addData(list1);
//        adapter2.addData(list2);
//        adapter3.addData(list3);
    }
}
