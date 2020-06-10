package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.bean.CourseBasicInfoBean
import edu.capella.mobile.android.widgets.CPTextView

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  24-03-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class StudiesAdapter(val context: Context, val studyList: ArrayList<CourseBasicInfoBean.StudyInstruction>, val studyEventListener:StudyEventListener) : RecyclerView.Adapter<StudiesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudiesViewHolder {
        return StudiesViewHolder(LayoutInflater.from(context).inflate(R.layout.row_study, parent, false))
    }

    override fun getItemCount(): Int {
        return studyList.size
    }

    override fun onBindViewHolder(holder: StudiesViewHolder, position: Int) {
        holder.studyParentLayout.setOnClickListener {
            studyEventListener.onStudyItemClick(studyList[position])
        }

        holder.studyTitle.text = HtmlCompat.fromHtml(studyList[position].title!!,
            HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    interface StudyEventListener{
       fun  onStudyItemClick(studyInstruction: CourseBasicInfoBean.StudyInstruction)
    }
}

class StudiesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val studyTitle = view.findViewById<CPTextView>(R.id.title)!!
    val studyParentLayout = view.findViewById<RelativeLayout>(R.id.studyParentLayout)!!
}
