package com.chad.baserecyclerviewadapterhelper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.baserecyclerviewadapterhelper.adapter.PullToRefreshAdapter;
import com.chad.baserecyclerviewadapterhelper.base.BaseActivity;
import com.chad.baserecyclerviewadapterhelper.data.DataServer;
import com.chad.baserecyclerviewadapterhelper.entity.Status;
import com.chad.baserecyclerviewadapterhelper.loadmore.CustomLoadMoreView;
import com.chad.baserecyclerviewadapterhelper.util.Constant;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Response;


interface RequestCallBack {
    void success(List<Status> data);

    void fail(Exception e);
}

class Request extends Thread {
    private static final int PAGE_SIZE = 6;
    private int mPage;
    private RequestCallBack mCallBack;
    private Handler mHandler;

    private static boolean mFirstPageNoMore;
    private static boolean mFirstError = true;

    public Request(int page, RequestCallBack callBack) {
        mPage = page;
        mCallBack = callBack;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        try {Thread.sleep(500);} catch (InterruptedException e) {}

        if (mPage == 2 && mFirstError) {
            mFirstError = false;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallBack.fail(new RuntimeException("fail"));
                }
            });
        } else {
            int size = PAGE_SIZE;
            if (mPage == 1) {
                if (mFirstPageNoMore) {
                    size = 1;
                }
                mFirstPageNoMore = !mFirstPageNoMore;
                if (!mFirstError) {
                    mFirstError = true;
                }
            } else if (mPage == 4) {
                size = 1;
            }

            final int dataSize = size;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCallBack.success(DataServer.getSampleData(dataSize));
                }
            });
        }
    }
}

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class PullToRefreshUseActivity extends BaseActivity {

    private static final int PAGE_SIZE = 6;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PullToRefreshAdapter mAdapter;

    private int mNextRequestPage = 1;

    private FloatingActionButton mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //上传按钮
        mButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PullToRefreshUseActivity.this, PhotoUploadActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Pets Community");
//        setBackBtn();

        initAdapter();


//        addHeadView();
        initRefreshLayout();
        mSwipeRefreshLayout.setRefreshing(true);
        refresh();
    }
    String picturePath="";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //picturePath就是图片在储存卡所在的位置
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        }
    }



    OkHttpClient client = new OkHttpClient();

	private String run(String url) throws IOException {
		okhttp3.Request request = new okhttp3.Request.Builder()
				.url(url)
				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}



    private void initAdapter() {

        //强制在主线程发送请求
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

		String res = "";
	    try {

		    res = run(Constant.BASE_URL+"/Showmessage.do");
		    String[] ss = res.trim().split("<br />");
		    List<String> strings = new ArrayList<String>();
		    for (String string : ss) {
			    strings.add(string);
		    }
            mAdapter = new PullToRefreshAdapter(strings);
	    } catch (IOException e) {
		    e.printStackTrace();
	    }


        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });

//	    官方注释
//        mAdapter.setPreLoadNumber(3);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                Toast.makeText(PullToRefreshUseActivity.this, Integer.toString(position), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void addHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.head_view, (ViewGroup) mRecyclerView.getParent(), false);
        headView.findViewById(R.id.iv).setVisibility(View.GONE);
        ((TextView) headView.findViewById(R.id.tv)).setText("change load view");
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setNewData(null);
                mAdapter.setLoadMoreView(new CustomLoadMoreView());
                mRecyclerView.setAdapter(mAdapter);
                Toast.makeText(PullToRefreshUseActivity.this, "change complete", Toast.LENGTH_LONG).show();

                mSwipeRefreshLayout.setRefreshing(true);
                refresh();
            }
        });
        mAdapter.addHeaderView(headView);
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        mNextRequestPage = 1;
        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        new Request(mNextRequestPage, new RequestCallBack() {
            @Override
            public void success(List<Status> data) {
                setData(true, data);
                mAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void fail(Exception e) {
                Toast.makeText(PullToRefreshUseActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                mAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).start();
    }

    private void loadMore() {
        new Request(mNextRequestPage, new RequestCallBack() {
            @Override
            public void success(List<Status> data) {
                /**
                 * fix https://github.com/CymChad/BaseRecyclerViewAdapterHelper/issues/2400
                 */
                boolean isRefresh =mNextRequestPage ==1;
                setData(isRefresh, data);
            }

            @Override
            public void fail(Exception e) {
                mAdapter.loadMoreFail();
                Toast.makeText(PullToRefreshUseActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
            }
        }).start();
    }

    private void setData(boolean isRefresh, List data) {
        mNextRequestPage++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mAdapter.setNewData(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
            Toast.makeText(this, "no more data", Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.loadMoreComplete();
        }
    }
}