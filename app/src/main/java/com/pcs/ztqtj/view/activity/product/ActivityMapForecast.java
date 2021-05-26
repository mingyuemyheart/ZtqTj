package com.pcs.ztqtj.view.activity.product;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.AdapterMapForecast;
import com.pcs.ztqtj.control.tool.CommUtils;
import com.pcs.ztqtj.control.tool.ZtqLocationTool;
import com.pcs.ztqtj.control.tool.ZtqLocationTool.PcsLocationListener;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 指点天气
 */
public class ActivityMapForecast extends FragmentActivityZtqBase {
	private AMap mAMap;
	private MapView mMapView;
	// 地图默认每像素密度
	private final float DEFAULKT_SCALE_PER_PIXEL = 2.14253568649292f;
	// 当前经纬度
	private LatLng mLatLng = null;
	// 标记
	private MarkerOptions mMarker = null;
	// 数据适配器
	private AdapterMapForecast mAdapter = null;
	// 搜索：输入框
	private AutoCompleteTextView mAutoText = null;
	// 搜索词
	private String mSearchWord = "";
	// Poi查询条件类
	private PoiSearch.Query mQuery;
	// 地理编码搜索
	private GeocodeSearch mGeocodeSearch;
	// 加油站marker列表
    private List<Marker> gasMarkerList = new ArrayList<>();
    private boolean isFirstShow = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_forecast);
		setTitleText(R.string.map_forecast);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		// 初始化地理编码搜索
		initGeocodeSearch();
		// 初始化列表
		initList();
		// 初始化地图
		initMap();
		// 初始化搜索
		initSearch();
		// 初始化定位
		initLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		// 添加定位监听
		if (mLatLng == null) {
			ZtqLocationTool.getInstance().addListener(mLocationListener);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		// 去除定位监听
		ZtqLocationTool.getInstance().removeListener(mLocationListener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	/**
	 * 初始化地图
	 */
	private void initMap() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
//			mAMap.setMapLanguage(AMap.ENGLISH);
			mAMap.setOnMapClickListener(mMapClick);
            // 标记点击事件
            mAMap.setOnMarkerClickListener(mOnMarkerClick);
			// 缩放
			mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
			// 显示比例尺
			mAMap.getUiSettings().setScaleControlsEnabled(true);
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.getUiSettings().setTiltGesturesEnabled(false);
		}
	}

	private ListView listView;
	private LinearLayout lay_nulldata;
	/**
	 * 初始化列表
	 */
	private void initList() {
		 listView = (ListView) findViewById(R.id.listView);
        lay_nulldata= (LinearLayout) findViewById(R.id.lay_nulldata);
		mAdapter = new AdapterMapForecast(this);
		listView.setAdapter(mAdapter);
		// 关闭按钮
		Button btnClose = (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(mOnClick);
		// 触摸监听
		View layout = findViewById(R.id.layout_data);
		layout.setOnTouchListener(mTouchListener);
	}

	public void setView(boolean isflag){
	    if (isflag){
            listView.setVisibility(View.GONE);
            lay_nulldata.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            lay_nulldata.setVisibility(View.GONE);
        }

    }

	/**
	 * 初始化搜索
	 */
	private void initSearch() {
		// 输入框
		mAutoText = (AutoCompleteTextView) findViewById(R.id.text_search);
		mAutoText.addTextChangedListener(mTextWatcher);
		mAutoText.setOnEditorActionListener(mEditorListener);
		// 按钮
		Button btn = (Button) findViewById(R.id.btn_search);
		btn.setOnClickListener(mOnClick);
	}

	/**
	 * 初始化定位
	 */
	private void initLocation() {
		// 定位4
		LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
		if (latLng != null) {
			locationSuccess(latLng);
            searchGAS(latLng);
		} else {
			// 添加定位监听
			ZtqLocationTool.getInstance().addListener(mLocationListener);
		}
	}

	private void locationSuccess(LatLng latLng) {
		// 切换定位点
		setLocation(latLng);
		// 显示数据
		showDataList(latLng);
		// 调用地理编码搜索
		LatLonPoint point = new LatLonPoint(latLng.latitude, latLng.longitude);
		callGeocodeSearch(point);
	}

	/**
	 * 初始化地理编码搜索
	 */
	private void initGeocodeSearch() {
		mGeocodeSearch = new GeocodeSearch(this);
		mGeocodeSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
	}

    /**
     * 搜索加油站
     */
    public void searchGAS(LatLng latLng) {
        LatLonPoint point = new LatLonPoint(latLng.latitude, latLng.longitude);
        PoiSearch.Query query = new PoiSearch.Query("", "010100", "");
        PoiSearch search = new PoiSearch(this, query);
        search.setBound(new PoiSearch.SearchBound(point, 3000));
        search.setOnPoiSearchListener(gasSearchListener);
        search.searchPOIAsyn();
    }

    private OnPoiSearchListener gasSearchListener = new OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            if(i != AMapException.CODE_AMAP_SUCCESS) {
                return;
            }
            showGASPosition(poiResult.getPois());
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

    /**
     * 显示加油站点
     * @param poiItemList
     */
    private void showGASPosition(List<PoiItem> poiItemList) {
        // 清除数据
        for(Marker marker : gasMarkerList) {
            if(marker != null) {
                marker.remove();
            }
        }
        gasMarkerList.clear();
        if(poiItemList == null || poiItemList.size() == 0) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        BitmapDescriptor descriptor = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gas);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(descriptor.getBitmap(), 70, 70, false);
        descriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
        options.icon(descriptor);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for(PoiItem poiItem : poiItemList) {
            LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            options.position(latLng);
            Marker marker = mAMap.addMarker(options);
            marker.setObject(poiItem.toString());
            gasMarkerList.add(marker);
            builder.include(latLng);
        }
        if(isFirstShow) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
            isFirstShow = false;
        }
    }

	/**
	 * 设置定位点，显示标识
	 * 
	 * @param latLng
     *
	 */
	private void setLocation(LatLng latLng) {
		mLatLng = latLng;
		mAMap.clear();
		// 地图位置
		LatLng tempLatLng = new LatLng(latLng.latitude + getLatitudeAdd(),latLng.longitude);
		// 标记
		if (mMarker == null) {
			mMarker = new MarkerOptions();
		}
		mMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
		mMarker.position(latLng);
		mAMap.addMarker(mMarker);
//		mAMap.moveCamera(CameraUpdateFactory.changeLatLng(tempLatLng));
		mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
		// 去除定位监听
		ZtqLocationTool.getInstance().removeListener(mLocationListener);
	}

	/**
	 * 获取纬度显示偏移
	 * 
	 * @return
	 */
	private double getLatitudeAdd() {
		double latitudeAdd = DEFAULKT_SCALE_PER_PIXEL;
		if (mAMap.getScalePerPixel() > 0) {
			latitudeAdd = (double) mAMap.getScalePerPixel();
		}
		latitudeAdd = -latitudeAdd / 800.0d;
		return latitudeAdd;
	}

	/**
	 * 显示数据列表
	 * 
	 * @param latLng
	 */
	private void showDataList(LatLng latLng) {
		View layout = findViewById(R.id.layout_data);
		layout.setVisibility(View.VISIBLE);
		mAdapter.refresh(6, latLng);
	}

	/**
	 * 调用地理编码搜索（获取地名）
	 * 
	 * @param latLonPoint
	 */
	private void callGeocodeSearch(final LatLonPoint latLonPoint) {
		// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
				GeocodeSearch.AMAP);
		// 设置同步逆地理编码请求
		mGeocodeSearch.getFromLocationAsyn(query);
	}

	/**
	 * 显示地名
	 * 
	 * @param addrName
	 */
	private void showAddrName(String addrName) {
		TextView textView = (TextView) findViewById(R.id.text_list_title);
		textView.setText(addrName);
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		mSearchWord = mAutoText.getText().toString();
		if (mSearchWord == null) {
			return;
		}
		mSearchWord = mSearchWord.trim();
		if ("".equals(mSearchWord)) {
			return;
		}
		// 收起键盘
        CommUtils.closeKeyboard(this);
		// 进度条
		showProgressDialog("正在搜索:\n" + mSearchWord);// 显示进度框
		// 搜索
		mQuery = new PoiSearch.Query(mSearchWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		mQuery.setPageSize(1);// 设置每页最多返回多少条poiitem
		mQuery.setPageNum(0);// 设置查第一页

		PoiSearch poiSearch = new PoiSearch(this, mQuery);
		poiSearch.setOnPoiSearchListener(mPoiListener);
		poiSearch.searchPOIAsyn();
	}

	/**
	 * 地图点击监听
	 */
	private OnMapClickListener mMapClick = new OnMapClickListener() {
		@Override
		public void onMapClick(LatLng latLng) {
			// 清空搜索词
			mSearchWord = "";
			// 收起键盘
            CommUtils.closeKeyboard(ActivityMapForecast.this);
			// 切换定位点
			setLocation(latLng);
			// 显示数据
			showDataList(latLng);
			// 调用地理编码搜索
			LatLonPoint point = new LatLonPoint(latLng.latitude,
					latLng.longitude);
			callGeocodeSearch(point);
			searchGAS(latLng);
		}
	};

    /**
     * 标记点击事件
     */
    private AMap.OnMarkerClickListener mOnMarkerClick = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng latLng = marker.getPosition();
            // 显示地名
            String name = (String) marker.getObject();
            showAddrName(name);
            // 显示数据
            showDataList(latLng);
            return false;
        }

    };

	/**
	 * 位置改变监听
	 */
	private PcsLocationListener mLocationListener = new PcsLocationListener() {
		@Override
		public void onLocationChanged() {
			LatLng latLng = ZtqLocationTool.getInstance().getLatLng();
			locationSuccess(latLng);
		}
	};

	/**
	 * 按钮点击监听
	 */
	private OnClickListener mOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnClose:
				// 关闭按钮
				View layout = findViewById(R.id.layout_data);
				layout.setVisibility(View.GONE);
				break;
			case R.id.btn_search:
				// 开始搜索
				doSearchQuery();
				break;
			}
		}
	};

	/**
	 * 搜索框监听
	 */
	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String newText = s.toString().trim();
			Inputtips inputTips = new Inputtips(ActivityMapForecast.this,
					new InputtipsListener() {

						@Override
						public void onGetInputtips(List<Tip> tipList, int rCode) {
							if (rCode == ZtqLocationTool.RCODE) {// 正确返回
								List<String> listString = new ArrayList<String>();
								for (int i = 0; i < tipList.size(); i++) {
									listString.add(tipList.get(i).getName());
								}
								ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
										getApplicationContext(),
										R.layout.item_map_forecast_search,
										listString);
								mAutoText.setAdapter(aAdapter);
								aAdapter.notifyDataSetChanged();
							}
						}
					});
			try {
				inputTips.requestInputtips(newText, "福建");

			} catch (AMapException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	};

	/**
	 * 触摸监听
	 */
	private OnTouchListener mTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// 收起键盘
            CommUtils.closeKeyboard(ActivityMapForecast.this);
			return true;
		}
	};

	/**
	 * 地理编码搜索监听
	 */
	private OnGeocodeSearchListener mGeocodeSearchListener = new OnGeocodeSearchListener() {

		@Override
		public void onGeocodeSearched(GeocodeResult result, int rCode) {

		}

		@Override
		public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
			if (rCode == ZtqLocationTool.RCODE) {
				if (result != null
						&& result.getRegeocodeAddress() != null
						&& result.getRegeocodeAddress().getFormatAddress() != null) {
                    // 格式化地址
                    String tempAddr = result.getRegeocodeAddress().getFormatAddress();
                    String district = result.getRegeocodeAddress().getDistrict();
                    String city = result.getRegeocodeAddress().getCity();
					if (!TextUtils.isEmpty(district) && tempAddr.indexOf(district) > -1) {
						tempAddr = tempAddr.substring(tempAddr.indexOf(district)
								+ district.length());
					} else if (!TextUtils.isEmpty(city) && tempAddr.indexOf(city) > -1) {
						tempAddr = tempAddr.substring(tempAddr.indexOf(city)
								+ district.length());
					}

					// 显示地名
					showAddrName(tempAddr);
				}
			}
		}

	};

	/**
	 * 搜索地名回调
	 */
	private OnPoiSearchListener mPoiListener = new OnPoiSearchListener() {

		@Override
		public void onPoiSearched(PoiResult result, int rCode) {
			dismissProgressDialog();// 隐藏对话框
			if (rCode == ZtqLocationTool.RCODE) {
				if (result != null && result.getQuery() != null) {// 搜索poi的结果
					if (result.getQuery().equals(mQuery)) {// 是否是同一条
						// 取得搜索到的poiitems有多少页
						List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始

						if (poiItems != null && poiItems.size() > 0) {
							LatLonPoint lalLonPoint = poiItems.get(0)
									.getLatLonPoint();
							LatLng latLnn = new LatLng(
									lalLonPoint.getLatitude(),
									lalLonPoint.getLongitude());
							// 显示标记
							setLocation(latLnn);
							// 显示数据
							showDataList(latLnn);
							// 显示地名
							showAddrName(mSearchWord);
							searchGAS(latLnn);
						} else {
							// 提示无结果
							Toast.makeText(ActivityMapForecast.this,
									R.string.search_no_result,
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					// 提示无结果
					Toast.makeText(ActivityMapForecast.this,
							R.string.search_no_result, Toast.LENGTH_SHORT)
							.show();
				}
			} else if (rCode == 27) {
				// 网络错误
				Toast.makeText(ActivityMapForecast.this,
						R.string.search_net_error, Toast.LENGTH_SHORT).show();
			} else {
				// 其他错误
				Toast.makeText(ActivityMapForecast.this, R.string.search_error,
						Toast.LENGTH_SHORT).show();
			}
		}

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

	/**
	 * 搜索框键盘监听
	 */
	private OnEditorActionListener mEditorListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			doSearchQuery();
			return false;
		}
	};
}
