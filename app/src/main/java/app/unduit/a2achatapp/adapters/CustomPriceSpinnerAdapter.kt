package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import app.unduit.a2achatapp.R

class CustomPriceSpinnerAdapter (private val context: Context, private val items: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_price_spinner_adapter, parent, false)
        val textView = view.findViewById<TextView>(R.id.item_name)


        val item = items[position]
        textView.text = item




        return view
    }


}