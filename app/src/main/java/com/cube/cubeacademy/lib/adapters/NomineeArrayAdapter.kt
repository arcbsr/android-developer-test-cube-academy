package com.cube.cubeacademy.lib.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cube.cubeacademy.R
import com.cube.cubeacademy.lib.models.Nominee

class NomineeArrayAdapter (private val context: Context, private val items: List<Nominee>) : BaseAdapter() {

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
        val nominee = getItem(position) as Nominee

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_nominee_list_item, null)

        val name = view.findViewById<TextView>(R.id.name)
        name.text = "${nominee.firstName} ${nominee.lastName}"

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}