package com.example.recyclerview

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerview.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // xml 바인딩 선언
    private lateinit var itemAdapter: OrderAdapter //RecyclerView 어댑터
    private var itemList = ArrayList<D_order>() //어댑터 데이터 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RecyclerView
        binding.listOrder.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저
        itemAdapter = OrderAdapter(itemList,
            onSelectedCountChanged = {selectedCount ->   // 정의시 체크박스 라인의 총합계와 선택된 체크박스의 합산수를 OrderAdapter에서 argument로 받음
            binding.txtSelected.text = selectedCount.toString()
        },
            onTotalItemCountChanged = { totalCount ->   // 정의시 체크박스 라인의 총합계와 선택된 체크박스의 합산수를 OrderAdapter에서 argument로 받음
                binding.txtTotal.text = totalCount.toString()
            }
        ) // 콜백 onSelectedCountChanged, onTotalItemCountChanged 이 포함된 아답터
        binding.listOrder.adapter = itemAdapter // 어댑터 설정

        //RecyclerView 구분선 넣기
        val decoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.listOrder.addItemDecoration(decoration)

        // btn_send 클릭 이벤트
        binding.btnSend.setOnClickListener {
            val orderId: String = binding.txtOrderId.text.toString()
            fetchOrders(orderId)
            itemAdapter.clearSelectedItems() // MainActivity에서 OrderAdapter를 선언 하고 OrderAdapter에서 이 함수를 정의 한다.
            binding.txtSelected.text = "0"
        }
    }

    private fun fetchOrders(orderId: String) {
        RetrofitClient.instance.getOrders(orderId).enqueue(object : Callback<List<D_order>> {
            override fun onResponse(call: Call<List<D_order>>, response: Response<List<D_order>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        itemList.clear()
                        itemList.addAll(it)
                        itemAdapter.notifyDataSetChanged()  // RecyclerView 갱신
                    }
                } else {
                    Toast.makeText(this@MainActivity, "서버 응답 오류", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<D_order>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.message.toString())
            }
        })
    }
}

