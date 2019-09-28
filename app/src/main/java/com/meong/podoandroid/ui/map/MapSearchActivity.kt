package com.meong.podoandroid.ui.map

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.meong.podoandroid.BR
import com.meong.podoandroid.data.StoreItem

import com.meong.podoandroid.databinding.ActivityMapSearchBinding
import com.meong.podoandroid.databinding.RvItemSearchLocationBinding
import com.meong.podoandroid.network.NetworkService
import com.meong.podoandroid.ui.feed.FeedRecommendActivity
import com.meong.podoandroid.ui.map.get.GetLocationListResponse
import com.meong.podoandroid.ui.map.get.GetLocationListResponseData
import com.meong.podoandroid.ui.home.MainActivity
import kotlinx.android.synthetic.main.activity_map_search.*
import kotlinx.android.synthetic.main.nav_main.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapSearchActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMapSearchBinding

    lateinit var networkService: NetworkService

    val TAG: String = "MapSearchAvtivity"

    var locationItems: MutableLiveData<List<GetLocationListResponseData>> =
            MutableLiveData<List<GetLocationListResponseData>>().apply { this.value = listOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMapSearchBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        mBinding.lifecycleOwner = this
        mBinding.activity = this

        getAddressData()

        setDrawer()
        setOnBtnClickListener()
        onDrawerItemClickListener()
    }

    private fun getAddressData() {
        val builder = Retrofit.Builder()
        val retrofit_loc_search = builder
                .baseUrl("https://dapi.kakao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        networkService = retrofit_loc_search.create(NetworkService::class.java)

        var header = "KakaoAK f58c0b6bf01032faee81071dd1d935c6"

        et_map_search_act_location.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(string: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (string.toString() != "") {
                    getLocationList(header, string.toString())
                }
            }
        })

    }

    private fun getLocationList(header: String, location: String) {
        var getLocationList = networkService.getLocationList(header, location)
        getLocationList.enqueue(object : Callback<GetLocationListResponse> {
            override fun onFailure(call: Call<GetLocationListResponse>, t: Throwable) {
                Log.d(TAG, t.message.toString())
            }

            override fun onResponse(call: Call<GetLocationListResponse>, response: Response<GetLocationListResponse>) {
                if (response.isSuccessful) {
                    mBinding.setVariable(BR.item, response.body()!!.documents)
                    locationItems.value = response.body()!!.documents

                    mBinding.recyclerView.adapter = SearchLocationAdapter { locationItems : GetLocationListResponseData -> recyclerViewItemClicked(locationItems)}
                    mBinding.recyclerView.adapter!!.notifyDataSetChanged()
                }
            }
        })
    }

    private fun onDrawerItemClickListener() {
        // 홈이 눌렸을 때
        txt_nav_main_home.setOnClickListener {
            startActivity<MainActivity>()
            finish()
        }

        //병원 위치 눌렸을 때
        txt_nav_main_hospital.setOnClickListener {
            startActivity<MapActivity>()
            finish()
        }

        txt_nav_main_recommend.setOnClickListener {
            startActivity<FeedRecommendActivity>()
            finish()
        }


    }

    private fun setDrawer() {
        drawer_map_search_act.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setOnBtnClickListener() {
        map_hamburger.setOnClickListener { drawer_map_search_act.openDrawer(Gravity.LEFT) }
        img_search_map_act_clear.setOnClickListener {
            locationItems.value = listOf()
            et_map_search_act_location.text.clear()
        }

        img_map_search_act_back.setOnClickListener {
            finish()
        }
    }

    private fun recyclerViewItemClicked(item: GetLocationListResponseData ){
        var item = StoreItem(item.place_name,item.y!!.toFloat(), item.x!!.toFloat(),item.address_name)

        val intent : Intent = Intent()
        intent.putExtra("lat",item.latitude)
        intent.putExtra("lon",item.longtitude)
        intent.putExtra("name",item.name)
        intent.putExtra("address",item.address)

        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}

class SearchLocationAdapter(clickListener : (GetLocationListResponseData) -> Unit) : RecyclerView.Adapter<SearchLocationAdapter.MyHolder>() {

    val clickListener : (GetLocationListResponseData) -> Unit = clickListener
    var items: List<GetLocationListResponseData> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemSearchLocationBinding.inflate(inflater, parent, false)

        return MyHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(items[position],clickListener)
    }


    inner class MyHolder(private val binding: RvItemSearchLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GetLocationListResponseData,clickListener : (GetLocationListResponseData)-> Unit) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
            binding.root.setOnClickListener { clickListener(item) }
        }
    }
}

@BindingAdapter("app_recyclerview_location_items")
fun RecyclerView.setItems(items: List<GetLocationListResponseData>) {
    (adapter as? SearchLocationAdapter)?.run {
        this.items = items
        this.notifyDataSetChanged()
    }
}

@BindingAdapter("android:visibility")
fun View.setVisibilityBinding(visible: Boolean) {
    this.visibility = if (visible)
        View.VISIBLE
    else
        View.GONE
}
