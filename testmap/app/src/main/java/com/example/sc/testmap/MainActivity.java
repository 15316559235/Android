package com.example.sc.testmap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import java.util.List;
import com.baidu.mapapi.map.InfoWindow.*;



public class MainActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private boolean isFirstIn=true;

    //定位相关
    private LocationClient mLocationClient=null;
    private MyLocationListener mLocationListener=null;
    private double mLatitude;
    private double mLongtitude;
    private BitmapDescriptor mIconLocation=null;
    private MyOrientationListener myOrientationListener=null;
    private Context context=null;
    private float mCurrentX;

    private MyLocationConfiguration.LocationMode mLcationMode=null;


    //覆盖物相关
    private BitmapDescriptor mMarker=null;
    private RelativeLayout mMarkerLy=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        initView();

        initLocation();

        initMarker();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extrainfo=marker.getExtraInfo();
                Info info=(Info)extrainfo.getSerializable("info");
                ImageView iv=(ImageView)mMarkerLy.findViewById(R.id.id_info_img);
                TextView distance=(TextView)mMarkerLy.findViewById(R.id.id_info_distance);
                TextView good=(TextView)mMarkerLy.findViewById(R.id.id_info_good);
                TextView name=(TextView)mMarkerLy.findViewById(R.id.id_info_name);

                iv.setImageResource(info.getImfId());
                distance.setText(info.getDistance());
                name.setText(info.getName());
                good.setText(info.getGood()+"");

                mMarkerLy.setVisibility(View.VISIBLE);

                InfoWindow infoWindow;
                TextView tv=new TextView(context);
                tv.setBackgroundResource(R.mipmap.popup_middle);
                tv.setPadding(30,20,30,50);
                tv.setText(info.getName());
                tv.setTextColor(Color.parseColor("#000000"));

                final LatLng latLng=marker.getPosition();
                Point p=mBaiduMap.getProjection().toScreenLocation(latLng);
                p.y-=47;
                LatLng ll=mBaiduMap.getProjection().fromScreenLocation(p);

                infoWindow=new InfoWindow(tv,ll,47);

                mBaiduMap.showInfoWindow(infoWindow);

                mMarkerLy.setVisibility(View.VISIBLE);

                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    private void initMarker(){
        mMarker=BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        mMarkerLy=(RelativeLayout)findViewById(R.id.id_maker_ly);
    }

    private void initView() {
        context=getApplicationContext();

        mMapView = (MapView) findViewById(R.id.id_bmapView);
        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
    }

    private void initLocation(){
        mLcationMode= MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient=new LocationClient(this);
        mLocationListener=new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        LocationClientOption option=new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);

        mIconLocation= BitmapDescriptorFactory.fromResource(R.mipmap.gps);

        myOrientationListener=new MyOrientationListener(context);

        myOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX=x;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()) {
            mLocationClient.start();
        }

        myOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_map_common:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通（OFF）");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通（ON）");
                }
                break;
            case R.id.id_map_location:
                toMyLocation();
                break;
            case R.id.id_map_mode_common:
                mLcationMode= MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.id_map_mode_following:
                mLcationMode= MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:
                mLcationMode= MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.id_map_add_overlay:
                addOverlays(Info.infos);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addOverlays(List<Info> infos){
        mBaiduMap.clear();

        LatLng latLng=null;
        Marker marker=null;
        OverlayOptions options=null;
        for(Info info:infos){
            latLng=new LatLng(info.getLatitude(),info.getLongitude());
            options=new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            marker=(Marker) mBaiduMap.addOverlay(options);

            Bundle arg0=new Bundle();
            arg0.putSerializable("info",info);
            marker.setExtraInfo(arg0);
        }

        MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    public void toMyLocation(){
        LatLng latLng=new LatLng(mLatitude,mLongtitude);
        MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(msu);
    }

    private class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data=new MyLocationData.Builder().direction(mCurrentX).accuracy(location.getRadius()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(data);

            mLatitude=location.getLatitude();
            mLongtitude=location.getLongitude();

            MyLocationConfiguration config=new MyLocationConfiguration(mLcationMode,true,mIconLocation);
            mBaiduMap.setMyLocationConfiguration(config);

            if(isFirstIn){
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn=false;
            }
        }
    }
}
