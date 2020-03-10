package hzkj.cc.map

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatus.Builder
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.model.LatLng
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_map.*
import pub.devrel.easypermissions.EasyPermissions

/**

 * @Author chencheng
 * @Date 2020-03-02-11:33
 */
@Route(path = "/map/main")
class MapFragment : BaseFragment<MapViewModel>(), EasyPermissions.PermissionCallbacks {

    var areaName: String? = null
    var areaCode: String? = null

    private var sennors = mutableListOf<Sennor>()
    private var cameras = mutableListOf<Camera>()

    private var permissions =
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    private var broadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            areaCode = intent!!.getStringExtra("areaCode")
            areaName = intent!!.getStringExtra("areaName")
            viewModel.getCameras(areaCode!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_map
    }

    override fun initView() {
    }

    override fun initData() {
        activity!!.registerReceiver(broadCast, IntentFilter().apply { addAction("map") })
    }

    override fun onDestroy() {
        super.onDestroy()
        bmapView?.onDestroy()
        activity!!.unregisterReceiver(broadCast)
    }

    override fun onShow() {
        bmapView?.onResume()
//        areaCode?.let {
//            viewModel.getCameras(it)
//        }
    }

    override fun onResume() {
        super.onResume()
        bmapView.onResume()
    }

    private fun moveToCenterOfSennors() {
        var x = 0.0
        var y = 0.0
        for (camera in cameras) {
            var lat = camera.bdLngLat!!.split(",")[1].toDouble()
            var lng = camera.bdLngLat!!.split(",")[0].toDouble()
            x += lng
            y += lat
            bmapView.map.addOverlay(
                MarkerOptions().position(
                    LatLng(
                        lat,
                        lng
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.b_camera))
            )
        }
        for (sennor in sennors) {
            var lat = sennor.bdLngLat!!.split(",")[1].toDouble()
            var lng = sennor.bdLngLat!!.split(",")[0].toDouble()
            bmapView.map.addOverlay(
                MarkerOptions().position(
                    LatLng(
                        lat,
                        lng
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.b_sensor))
            )
        }
        x /= cameras.size
        y /= cameras.size
        val mMapStatus = Builder() //要移动的点
            .target(LatLng(y, x)) //放大地图到20倍
            .zoom(14f)
            .build()
        val mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus)
        bmapView.map.setMapStatus(mMapStatusUpdate)
    }

    override fun startObserve() {
        viewModel.cameraDatas.observe(this, Observer {
            cameras.clear()
            for (camera in it) {
                if (camera.equipmentCode != "99999999") {
                    cameras.add(camera)
                }
            }
            if (cameras.size == 0) {
                ViewUtil.toast(activity as Context, "该地区没有设备")
            }
            viewModel.getSennors(areaCode!!)
        })
        viewModel.sennorDatas.observe(this, Observer {
            sennors.run {
                clear()
            }
            for (sennor in it) {
                if (sennor.sensorCode != "8888") {
                    sennors.add(sennor)
                }
            }
            if (sennors.size == 0) {
                ViewUtil.toast(activity as Context, "该地区没有传感器")
            }
            activity?.sendBroadcast(Intent().apply {
                action = "main"
                putExtra("areaCode", areaCode)
                putExtra("from", "map")
            })
            textview_areaName.text = areaName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (EasyPermissions.hasPermissions(activity as Context, *permissions)) {
                    println("无需申请")
                    moveToCenterOfSennors()
                } else {
                    EasyPermissions.requestPermissions(
                        this,
                        "请求必要的权限,拒绝权限可能会无法使用app",
                        0,
                        *permissions
                    )
                }
            } else {
                println("申请过了")
                moveToCenterOfSennors()
            }

        })
    }

    override fun updateError(it: Int?) {
        super.updateError(it)
        when (it) {
            MapViewModel.CAMERAS -> {
                ViewUtil.toast(activity as Context, "获取设备失败")
            }
            MapViewModel.SENNORS -> {
                ViewUtil.toast(activity as Context, "获取传感器失败")

            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        moveToCenterOfSennors()
    }

    override fun onPause() {
        super.onPause()
        bmapView?.onPause()
    }

    override fun onHide() {
        bmapView?.onPause()

    }
}