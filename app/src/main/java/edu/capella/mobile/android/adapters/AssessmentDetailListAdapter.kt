package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.bean.CourseAssessmentBean
import edu.capella.mobile.android.widgets.CPTextView

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  15-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class AssessmentDetailListAdapter(val context: Context,
                                  val list: ArrayList<CourseAssessmentBean.StudyInstruction>,
                                  val listener:OnItemClickListener) :
    RecyclerView.Adapter<AssessmentDetailListAdapter.AssessmentDetailListViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AssessmentDetailListViewHolder {
        return AssessmentDetailListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.syllabus_row_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClicked(value: CourseAssessmentBean.StudyInstruction)
    }

    override fun onBindViewHolder(holder: AssessmentDetailListViewHolder, position: Int) {

        val title = list[position].title.let {
            it?.let { it1 ->
                HtmlCompat.fromHtml(
                    it1, HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }

        holder.title.text = title

//        holder.parentView.contentDescription = title
        holder.parentView.setOnClickListener {
            listener.onItemClicked(list[position])
        }
    }


    class AssessmentDetailListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<CPTextView>(R.id.syllabusTitle)!!
        val parentView = view.findViewById<LinearLayout>(R.id.syllabusRowParent)!!

    }
}