package com.example.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.databinding.ItemLayoutBinding


// 어댑터 클래스
class OrderAdapter(
    private val itemList: List<D_order>,
    private val onSelectedCountChanged: (Int) -> Unit,  // 선택된 아이템 개수 변경 콜백 Unit은 자바의 void와 비슷 --> 받는 값은 있으나 전달 값은 없다. 외부에서 특정 동작(예: UI 업데이트)을 실행하는 데 사용됨
    private val onTotalItemCountChanged: (Int) -> Unit  // 전체 아이템 개수 전달 콜백 Unit은 자바의 void와 비슷 --> 받는 값은 있으나 전달 값은 없다. 외부에서 특정 동작(예: UI 업데이트)을 실행하는 데 사용됨
) : RecyclerView.Adapter<OrderAdapter.ItemViewHolder>()  {

    // 체크된 아이템 저장 (position을 저장하여 관리)
    private val checkedItems = mutableSetOf<Int>()

    // 전체 아이템 수는 변하지 않기 때문에 초기화 시 전달
    init {
        // 전체 아이템 수는 변하지 않기 때문에 초기화 시 전달
        onTotalItemCountChanged(itemList.size)
    }

    // ViewHolder 클래스
    inner class ItemViewHolder(private val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: D_order, position: Int) { // 하나하나의 item 설정을 정의하기 때문에 List<D_order>가 아닌 D_order을 변수로 사용 한다.
            binding.txtNumber.text = (position + 1).toString()
            binding.txtOrderId.text = item.order_id
            binding.txtItemId.text = item.item_id
            binding.txtItemName.text = item.item_name
            binding.txtQuantity.text = "수량 : ${item.quantity}"

            // 전체 리스트 숫자를 콜백(inner class 밖에서 선언 하면 리스트가 생성되기 전 이기 때문에 항상 숫자는 "0" 이다.
            onTotalItemCountChanged(itemList.size)

            // 기존 체크 상태 유지
            binding.chkOrder.setOnCheckedChangeListener(null) // 리싸이클러뷰가 재사용 될 때, 체크박스의 상태값이 재사용된 뷰에 반영되지 않기 위해 리스너를 중지 시킴
            binding.chkOrder.isChecked = checkedItems.contains(position)

            //itemView click event
            itemView.setOnClickListener  {
                showQuantityDialog(item, position)
            }

//            //checkbox click event 체크박스 클릭으로는 선택 및 해지 못 하게 --> 아래 코드로 대체 가능
//            binding.chkOrder.setOnClickListener{
//                if(binding.chkOrder.isChecked) {
//                    binding.chkOrder.isChecked = false
//                    // checkedItems.add(position)
//                }else{
//                    binding.chkOrder.isChecked = false
//                    //checkedItems.remove(position)
//                }
//                // 선택된 아이템 수가 변경되었으므로 콜백 호출 <-- 액션만 처리. 체크된 수는 업데이트 하지 않음
//                // onSelectedCountChanged(checkedItems.size) // 선택된 아이템 개수를 MainActivity에 전달
//            }

            // 체크박스 클릭 방지 (다이얼로그에서만 변경)
            binding.chkOrder.setOnClickListener {
                binding.chkOrder.isChecked = checkedItems.contains(position) // 현재 위치가 선택 되어 있는지 확인 하는 로직
            }

        }

        private fun showQuantityDialog(item: D_order, position: Int){
            val context = itemView.context // 다이얼로그 창을 띄우기 위한 화면 또는 앱 정보
            val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
            dialogBuilder.setTitle("수량 : ${item.quantity}")
//            dialogBuilder.setMessage("Quantity : ${item.quantity}")

            dialogBuilder.setPositiveButton("Confirm"){_,_->
                binding.chkOrder.isChecked = true
                checkedItems.add(position)
                onSelectedCountChanged(checkedItems.size) // 선택된 아이템 개수를 MainActivity에 전달
                notifyItemChanged(position)
            }
            dialogBuilder.setNegativeButton("Cancel"){_,_->
                binding.chkOrder.isChecked = false
                checkedItems.remove(position)
                onSelectedCountChanged(checkedItems.size) // 선택된 아이템 개수를 MainActivity에 전달
                notifyItemChanged(position)
            }

            val dialog = dialogBuilder.create()
            dialog.setCancelable(false) // 화면 이외를 클릭 하여 다이얼로그 창을 닫는 것을 금지
            dialog.show()
        }

    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    // 데이터 바인딩
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position], position)
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = itemList.size

    // 체크된 아이템들을 초기화하는 메서드 --> MainActivity에서 OrderAdapter를 정의 함으로써 clearSelectedItems() 함수를 사용 한다.
    fun clearSelectedItems() {
        checkedItems.clear()  // 체크된 항목들 초기화
        notifyDataSetChanged() // RecyclerView 갱신
    }

    // 체크된 아이템들을 MainActivity로 전달
    fun getCheckedItems(): Set<Int> {
        return checkedItems
    }

}
