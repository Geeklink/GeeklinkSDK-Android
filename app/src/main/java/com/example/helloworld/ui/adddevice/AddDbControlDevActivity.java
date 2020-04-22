package com.example.helloworld.ui.adddevice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.BrandsListAdapter;
import com.example.helloworld.adapter.CommonAdapter;
import com.example.helloworld.adapter.holder.ViewHolder;
import com.example.helloworld.view.CommonToolbar;
import com.example.helloworld.bean.SortModel;
import com.example.helloworld.enumdata.AddDevType;
import com.geeklink.smartpisdk.bean.IRLibBrandData;
import com.geeklink.smartpisdk.utils.CommonUtil;
import com.geeklink.smartpisdk.api.ApiManager;
import com.geeklink.smartpisdk.listener.OnGetDBRCBrandListener;
import com.geeklink.smartpisdk.listener.OnSetSubDevicveListener;
import com.gl.ActionFullType;
import com.gl.CarrierType;
import com.gl.CustomType;
import com.gl.DatabaseType;
import com.gl.DeviceMainType;
import com.gl.LanguageType;
import com.gl.StateType;
import com.gl.SubDevInfo;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;

public class AddDbControlDevActivity extends AppCompatActivity implements OnSetSubDevicveListener, OnGetDBRCBrandListener {
    private Context context;
    private String md5 = "";
    private AddDevType addDevType;

    private CommonToolbar toolbar;
    private FrameLayout searchView;
    private RecyclerView searchList;
    private TextView noSearchResult;
    private IndexableLayout indexableLayout;
    private SearchView mSearchView;
    private List<SortModel> sourceDateList = new ArrayList<>();
    private List<SortModel> searchDatas = new ArrayList<>();
    private List<IRLibBrandData> brands = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private BrandsListAdapter adapter;

    private DatabaseType databaseType = DatabaseType.AC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_db_remote_control);
        context = this;
        Intent intent = getIntent();
        md5 = intent.getStringExtra("md5");
        addDevType = AddDevType.values()[intent.getIntExtra("addDevType",0)];

        toolbar = findViewById(R.id.title);
        searchView = findViewById(R.id.fl_search_view);
        searchList = findViewById(R.id.recy);
        noSearchResult = findViewById(R.id.tv_no_result);
        indexableLayout = findViewById(R.id.indexableLayout);
        mSearchView = findViewById(R.id.searchview);

        indexableLayout.setLayoutManager(new LinearLayoutManager(context));
        indexableLayout.setCompareMode(IndexableLayout.MODE_FAST);
        adapter = new BrandsListAdapter(context);
        adapter.setDatas(sourceDateList, null);
        adapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<SortModel>() {

            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, SortModel entity) {
                itemChoose(entity);
            }
        });
        indexableLayout.setAdapter(adapter);
        indexableLayout.setOverlayStyle_Center();

        searchList.setLayoutManager(new LinearLayoutManager(context));
        searchList.setHasFixedSize(true);
        searchAdapter = new SearchAdapter(context, searchDatas);
        searchList.setAdapter(searchAdapter);

        toolbar.setRightClick(new CommonToolbar.RightListener() {
            @Override
            public void rightClick() {
                bindSubDevice();
            }
        });


        if (addDevType == AddDevType.AirCondition) {//隐藏自定义空调
            toolbar.setEditBtnVisible(false);
            toolbar.setRightTextVisible(false);
        }

        initData();

        ApiManager.getInstance().setOnSetSubDevicveListener(this);

    }


    private void initData() {
        switch (addDevType){
            case AirCondition:
                toolbar.setMainTitle("空调");
                databaseType = DatabaseType.AC;
                break;
            case TV:
                toolbar.setMainTitle("电视");
                databaseType = DatabaseType.TV;
                break;
            case STB:
                toolbar.setMainTitle("机顶盒");
                databaseType = DatabaseType.STB;
                break;
            case IPTV:
            default:
                toolbar.setMainTitle("IPTV");
                databaseType = DatabaseType.IPTV;
                break;
        }
        ApiManager.getInstance().setOnGetDBRCBrandListener(this);
        ApiManager.getInstance().getDBRCBrandWithMd5(md5,databaseType);
    }

    private void initSearch() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() > 0) {
                    if (searchView.getVisibility() == View.GONE) {
                        searchView.setVisibility(View.VISIBLE);
                    }
                    searchAdapter.getFilter().filter(newText.toLowerCase());
                } else {
                    if (searchView.getVisibility() == View.VISIBLE) {
                        searchView.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
    }



    private void itemChoose(SortModel sortModel) {
        Intent intent = new Intent(context, TestCodeActivity.class);
        intent.putExtra("md5",md5);
        intent.putExtra("databaseType",databaseType.ordinal());
        intent.putExtra("brandName",sortModel.getName());
        intent.putExtra("brandId",sortModel.getBrand_id());
        startActivity(intent);
        finish();
    }


    private void bindSubDevice(){
        int subType;
        switch (addDevType) {
            case TV:
                subType = CustomType.TV.ordinal();
                break;
            case STB:
                subType = CustomType.STB.ordinal();
                break;
            case IPTV:
            default:
                subType = CustomType.IPTV.ordinal();
                break;
        }
        SubDevInfo subDevInfo = new SubDevInfo(0, DeviceMainType.CUSTOM,subType,0,0, CarrierType.CARRIER_38,new ArrayList<Integer>(),md5,"");
        ApiManager.getInstance().setSubDeviceWithMd5(md5, subDevInfo, ActionFullType.INSERT);
    }

    @Override
    public void onSetSubDevice(StateType state, String md5, ActionFullType action, SubDevInfo subInfo) {
        if(state == StateType.OK){
            finish();
        }
    }

    @Override
    public void onDBRCBrand(StateType state, String md5, ArrayList<IRLibBrandData> brandList) {
        if(state == StateType.OK){
            brands.clear();
            brands.addAll(brandList);
            sourceDateList.clear();
            for (IRLibBrandData data : brands) {
                SortModel sortModel = new SortModel();
                String name = data.brandName;
                String ename = data.brandEName;
                String modes = data.modeList;
                String brand_id = data.brand_id;

                switch (CommonUtil.getSystemLanguageType()) {
                    case SIMPLIFIED_CHINESE:
                    case TRADITIONAL_CHINESE:
                        sortModel.setName(name);
                        break;
                    default:
                        sortModel.setName(ename);
                        break;
                }
                sortModel.setModeList(modes);
                sortModel.setBrand_id(brand_id);
                sourceDateList.add(sortModel);
            }
            adapter.setDatas(sourceDateList, null);

            initSearch();
        }
    }


    private class SearchAdapter extends CommonAdapter<SortModel> implements Filterable {

        public SearchAdapter(Context context, List<SortModel> datas) {
            super(context, R.layout.item_search, datas);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    ArrayList<SortModel> list = new ArrayList<>();
                    for (SortModel item : sourceDateList) {
                        if (item.getPinyin().startsWith(constraint.toString())
                                || item.getName().toLowerCase().contains(constraint)
                                || CommonUtil.translateSimplied2Tradional(item.getName().toLowerCase()).contains(constraint)) {
                            list.add(item);
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.count = list.size();
                    results.values = list;
                    return results;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    ArrayList<SortModel> list = (ArrayList<SortModel>) results.values;
                    mDatas.clear();
                    mDatas.addAll(list);
                    if (results.count == 0) {
                        noSearchResult.setVisibility(View.VISIBLE);
                    } else {
                        noSearchResult.setVisibility(View.INVISIBLE);
                    }
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public void convert(ViewHolder holder, final SortModel sortModel, final int position) {
            if (CommonUtil.getSystemLanguageType() == LanguageType.TRADITIONAL_CHINESE) {
                holder.setText(R.id.title, CommonUtil.translateSimplied2Tradional(sortModel.getName()));
            } else {
                holder.setText(R.id.title, sortModel.getName());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemChoose(sortModel);
                }
            });
        }
    }



}
