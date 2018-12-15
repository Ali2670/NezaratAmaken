package com.ibm.hamsafar.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ibm.hamsafar.R;
import com.ibm.hamsafar.adapter.OnLoadMoreListener;
import com.ibm.hamsafar.adapter.SampleAdapter;
import com.ibm.hamsafar.utils.Tools;

import java.net.ConnectException;
import java.util.ArrayList;

import hamsafar.ws.util.exception.ExceptionConstants;
import hamsafar.ws.util.exception.GenericBusinessException;
import ibm.ws.RestCaller;
import ibm.ws.WsResult;

import static hamsafar.ws.util.exception.ExceptionConstants.CONNECTION_TIMEOUT_EXCEPTION;
import static hamsafar.ws.util.exception.ExceptionConstants.NULL_VALUE_RETURN;
import static hamsafar.ws.util.exception.ExceptionConstants.RESULT_IS_OK;
import static hamsafar.ws.util.exception.ExceptionConstants.UNKNOWN_EXCEPTION;

/**
 * A simple {@link Fragment} subclass.
 */
public class SampleFragment extends Fragment {

 private static  SampleFragment sampleFragment;
    ListHttp listHttp;
    Integer loaded = 0;
    String loadMore;
    Boolean fragmentResume = false, fragmentVisible = false, fragmentOnCreated = false;
    SwipeRefreshLayout mSwipeRefreshLayout;
    View rootView;
    private ArrayList<Object> list = new ArrayList<>();
    //    private SampleAdapter postAdapter;
    private boolean refresh = false;
    private SampleAdapter adapter;

    public SampleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sampleFragment=this;
        if (rootView != null) {

            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_sample, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        Tools.SendPm();


        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        adapter = new SampleAdapter(recyclerView, list, getActivity(), false);
        recyclerView.setAdapter(adapter);

        //set load more listener for the RecyclerView adapter
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (loadMore == null || loadMore.equals("false")) {
                    loadMore = "true";
                    mSwipeRefreshLayout.setRefreshing(true);
                    listHttp = new ListHttp();
                    listHttp.execute(new Object());
                } else {
                    adapter.setLoaded();
                }

            }
        });
        loaded = 0;


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh = true;
                loaded = 0;
                refreshItems();
            }
        });


        myUIUpdate();


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void myUIUpdate() {

        if (refresh || loadMore == null || !loadMore.equals("endof")) {
            mSwipeRefreshLayout.setRefreshing(true);
            listHttp = new ListHttp();
            listHttp.execute(new Object());
        }
    }

    void refreshItems() {
        myUIUpdate();
    }

    public void FillViews() {

    }

    private class ListHttp extends AsyncTask<Object, Void, WsResult> {


        Toast toast;

        @Override
        protected WsResult doInBackground(Object... params) {

            WsResult result;

            try {
                Object obj = RestCaller.SingleObjectReturn("UserPosts", (Object[]) params);

                if (obj == null/* || obj.getAllPosts() == null || obj.getAllPosts().size() == 0*/) {
                    result = new WsResult(null, NULL_VALUE_RETURN);
                    loadMore = "endof";
                } else {
                    loadMore = "false";
                    result = new WsResult(obj, RESULT_IS_OK);
//                    if (obj.getAllPosts().size() < 10) {
//                        loadMore = "endof";
//                    }


                }
            } catch (GenericBusinessException e) {
                result = new WsResult(null, e.getErrCode());

            } catch (ConnectException e) {
                result = new WsResult(null, CONNECTION_TIMEOUT_EXCEPTION);
            }
            return result;

        }

        @Override
        protected void onPostExecute(WsResult result) {

            try {

                if (result.getErr().equals(RESULT_IS_OK)) {
                    loaded++;
                    if (refresh) {
                        refresh = false;
                        loaded = 0;
                        list = new ArrayList<>();
                    }
                    Object obj = result.getObject();
//                        list.addAll(obj.getAll());

                } else {
                    if (loadMore == null || !loadMore.equals("endof")) {
                        Err(result.getErr());
                    }
                }

            } catch (Exception e) {
                Err(UNKNOWN_EXCEPTION);

            }

//            searchTrddBtn.setEnabled(true);
            mSwipeRefreshLayout.setRefreshing(false);
            adapter.setLoaded();
            adapter.notifyDataSetChanged();


        }

        private void Err(int i) {
            String errStr = Tools.getStringByName("Exc." + i);
            if (toast != null) {
                toast.cancel();
            }
            Tools.ShowDialog(getActivity(), Tools.getStringByName("Error"), errStr);

            if (ExceptionConstants.REST_INVALID_SESSION_ID == i) {
//                startActivity(new Intent(CategoryList.this, LoginActivity.class));
//                finish();
            }
        }


        @Override
        protected void onPreExecute() {
//            recordNotFoundTv.setVisibility(View.INVISIBLE);
          /*  if (loadMore == null) {
                Toast.makeText(getActivity(), "err . drop down to refresh", Toast.LENGTH_SHORT).show();

            }
*/
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
