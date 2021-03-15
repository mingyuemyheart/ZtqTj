package com.pcs.ztqtj.view.fragment.air;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.pcs.ztqtj.R;

/**
 * Created by Z on 2017/1/16.
 */

public class FragmentAirMap extends SupportMapFragment implements DistrictSearch.OnDistrictSearchListener {

    private AMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_air_map,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpMapIfNeeded();
        search();
    }

    @Override
    public void onResume() {
        super.onResume();
        search();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = getMap();
        }
    }


    private void search() {
        mMap.clear();
        DistrictSearch search = new DistrictSearch(getActivity());
        DistrictSearchQuery query = new DistrictSearchQuery( );
        query.setKeywords("南昌市");
        query.setShowBoundary(true);
        query.setKeywordsLevel("country");
        search.setQuery(query);
        search.setOnDistrictSearchListener(this);
        search.searchDistrictAsyn();
    }

    @Override
    public void onDistrictSearched(DistrictResult districtResult) {
        if (districtResult == null || districtResult.getDistrict()==null) {
            return;
        }
        //通过ErrorCode判断是否成功
        if(districtResult.getAMapException() != null && districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
            final DistrictItem item = districtResult.getDistrict().get(0);

            if (item == null) {
                return;
            }
            LatLonPoint centerLatLng = item.getCenter();
            if (centerLatLng != null) {
                LatLng latLng = new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),R.drawable.ic_map_location)));
                markerOptions.setFlat(true);
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 8));
                mMap.addMarker(markerOptions);
            }


            new Thread() {
                public void run() {

                    String[] polyStr = item.districtBoundary();
                    if (polyStr == null || polyStr.length == 0) {
                        return;
                    }
                    for (String str : polyStr) {
                        String[] lat = str.split(";");
                        PolylineOptions polylineOption = new PolylineOptions();
                        PolygonOptions polygonOptions = new PolygonOptions();
                        boolean isFirst = true;
                        LatLng firstLatLng = null;
                        for (String latstr : lat) {
                            String[] lats = latstr.split(",");
                            if (isFirst) {
                                isFirst = false;
                                firstLatLng = new LatLng(Double
                                        .parseDouble(lats[1]), Double
                                        .parseDouble(lats[0]));
                            }
                            polylineOption.add(new LatLng(Double
                                    .parseDouble(lats[1]), Double
                                    .parseDouble(lats[0])));
                            polygonOptions.add(new LatLng(Double
                                    .parseDouble(lats[1]), Double
                                    .parseDouble(lats[0])));
                        }
                        if (firstLatLng != null) {
                            polylineOption.add(firstLatLng);
                            polygonOptions.add(firstLatLng);
                        }

                        polygonOptions.strokeColor(Color.alpha(0)).fillColor(Color.argb(100, 23,23,23));
                        //polylineOption.width(10).color(Color.BLUE);
                        //mAMap.addPolyline(polylineOption);
                        mMap.addPolygon(polygonOptions);
                    }
                }
            }.start();
        } else {
            if (districtResult.getAMapException() != null)
                Toast.makeText(getActivity(), districtResult.getAMapException().getErrorCode(), Toast.LENGTH_LONG)
                        .show();
        }
    }
}
