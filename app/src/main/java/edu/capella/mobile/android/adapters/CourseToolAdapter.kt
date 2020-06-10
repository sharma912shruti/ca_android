package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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

class CourseToolAdapter(val context:Context,val toolList:ArrayList<GP2Bean.LeftNavigationItem>,val listener: CourseToolEventListener)  : RecyclerView.Adapter<CourseToolAdapter.CourseToolViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseToolViewHolder {
        return CourseToolViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.syllabus_row_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return toolList.size
    }

    override fun onBindViewHolder(holder: CourseToolViewHolder, position: Int) {
        holder.syllabusTitle.text = toolList[position].name
        holder.syllabusRowParent.setOnClickListener {
            listener.onCourseToolClick(toolList[position])
        }
    }


    class CourseToolViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val syllabusTitle = view.findViewById<CPTextView>(R.id.syllabusTitle)!!
        val syllabusRowParent = view.findViewById<LinearLayout>(R.id.syllabusRowParent)!!
    }

    interface CourseToolEventListener{
        fun onCourseToolClick(leftNavigationItem: GP2Bean.LeftNavigationItem)
    }
}