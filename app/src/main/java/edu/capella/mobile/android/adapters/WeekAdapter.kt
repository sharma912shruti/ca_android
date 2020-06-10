package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.bean.GP2Bean
import edu.capella.mobile.android.widgets.CPTextView

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  25-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class WeekAdapter(val context:Context , val weekList:ArrayList<GP2Bean.LeftNavigationItem>,val isWeek:Boolean,val weekListener: WeekEventListener): RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        return WeekViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_item_course_room_menu,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return weekList.size
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        if(isWeek) {
            if (position == weekList.size - 1) {
                holder.bottomLine.visibility = View.INVISIBLE
            }
        }
        holder.header.text = weekList[position].name
        holder.parentLayout.setOnClickListener {
            weekListener.onWeekItemClick(weekList[position])
        }
    }


    class WeekViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val header = view.findViewById<CPTextView>(R.id.header)!!
        val parentLayout = view.findViewById<RelativeLayout>(R.id.parent_layout)!!
        val bottomLine = view.findViewById<View>(R.id.bottomLine)
    }

    interface  WeekEventListener{
        fun onWeekItemClick(leftNavigationItem: GP2Bean.LeftNavigationItem)
    }
}