package edu.capella.mobile.android.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.AssessmentLearnerBean
import edu.capella.mobile.android.widgets.CPTextView

/*
 * <H1>Class Name</H1>
 * <b>Class Description :</b> class description goes here
 *
 * @author  :  Shruti.Sharma
 * @version :  1.0
 * @since   :  14-04-2020
 *
 * @param param1 <p>Constructor Param1 Description Goes here</p>
 * @param param2 <p>Constructor Param2 Description Goes here</p>
 *
 */

class AssessmentLearnerAdapter(
    val context: Context,
    val assessmentList: ArrayList<AssessmentLearnerBean.FlexpathAssessmentsAndStatus>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<AssessmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssessmentViewHolder {
        return AssessmentViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_assessment_for_learner,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return assessmentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AssessmentViewHolder, position: Int) {

        val title = assessmentList[position].assignmentTitle?.let {
            HtmlCompat.fromHtml(
                it,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
        holder.assessmentCount.text =
            context.resources.getString(R.string.assessment) +" " +assessmentList[position].assessmentCount

        holder.assessmentTitle.text =
            title?.subSequence(title.lastIndexOf("]") + 1, title.length)?.trim()

        if (assessmentList[position].pccpTargetDate != null) {
            holder.assessmentTargetDate.text =
                context.resources.getString(R.string.target_date_colon) + " " + assessmentList[position].pccpTargetDate?.let {
                    Util.formatDateStringInDate(
                        it
                    )
                }
        } else {
            holder.assessmentTargetDate.text =
                context.resources.getString(R.string.target_date_not_set)
        }


//        holder.assessmentStatusInformation.text = context.resources.getString(R.string.attempt) + " #" + (assessmentList[position].pccpAttemptsCount?.toInt()?.plus(
//            1
//        ))

        // for assessment status
        when {
            assessmentList[position].status?.equals(Constants.IS_DUE)!! -> {
                // when assessment not submitted

                holder.assessmentStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_file_blank
                    )
                )
                holder.assessmentStatusInformation.text = context.getString(R.string.not_submitted)

                if (assessmentList[position].pccpTargetDate != null && assessmentList[position]?.pccpTargetDate.toString() != "") {
                    val targetDateInMilis = Util.getDateInMiliSeconds(
                        Util.formatDateNew(
                            assessmentList[position].pccpTargetDate.toString()
                        )
                    )

                    // for over dues
                    if ((System.currentTimeMillis()) > targetDateInMilis) {
                        holder.overdue.visibility = View.VISIBLE
                    } else {
                        holder.overdue.visibility = View.GONE
                    }
                } else {
                    holder.overdue.visibility = View.GONE
                }

            }
            assessmentList[position].status?.equals(Constants.HAS_BEEN_SUBMITTED)!! -> {
                // when assessment submitted
                holder.assessmentStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_file_fill
                    )
                )
                holder.assessmentStatusInformation.text =
                    context.getString(R.string.attempt) + " #" + assessmentList[position].pccpAttemptsCount +" "+ context.getString(
                        R.string.submitted
                    ) + " " + Util.formatDateNew(
                        assessmentList[position].pccpSubmittedDate.toString()!!
                    )
                holder.overdue.visibility = View.GONE
            }

            assessmentList[position].status?.contains("resubmitted",true)!! ->{
                holder.assessmentStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_file_fill
                    )
                )
                holder.assessmentStatusInformation.text =
                    context.getString(R.string.attempt) + " #" + assessmentList[position].pccpAttemptsCount +" "+ context.getString(
                        R.string.submitted
                    ) + " " + Util.formatDateNew(
                        assessmentList[position].pccpSubmittedDate.toString()!!
                    )
                holder.overdue.visibility = View.GONE
            }

            else -> {
                // when assessment evaluated
                holder.assessmentStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_file_fill_blue
                    )
                )
                holder.assessmentStatusInformation.text =
                    context.getString(R.string.attempt) + " #" + assessmentList[position].pccpAttemptsCount+" " + context.getString(
                        R.string.evaluated
                    ) + " " + Util.formatDateNew(
                        assessmentList[position].pccpEvaluatedDate.toString()!!
                    )

                holder.overdue.visibility = View.GONE
            }
        }

        // updated in past 3 day with blue dot

        if (!assessmentList[position].status?.equals(Constants.IS_DUE)!!) {

            if (assessmentList[position].pccpEvaluatedDate != null && assessmentList[position]?.pccpEvaluatedDate.toString() != "") {
                val date = Util.formatDateNew(
                    assessmentList[position].pccpEvaluatedDate.toString()
                )
                if (Util.getDifferenceBetweenDateIsMoreThan72Hours(Util.getDateInMiliSeconds(date))) {
                    holder.assessmentUpdateText.visibility = View.VISIBLE
                    val attemps = assessmentList[position].attempts!!.attempts
                    for (value in attemps!!) {
                        if (value.getRead().toString().trim() == Constants.READ.trim()) {
                            holder.assessmentUpdateText.visibility = View.GONE
                        } else {
                            holder.assessmentUpdateText.visibility = View.VISIBLE
                            break
                        }
                    }
                } else {
                    holder.assessmentUpdateText.visibility = View.GONE
                }


            } else if (assessmentList[position].pccpSubmittedDate != null && assessmentList[position]?.pccpSubmittedDate.toString() != "") {
                val date = Util.formatDateNew(
                    assessmentList[position].pccpSubmittedDate.toString()
                )

                if (Util.getDifferenceBetweenDateIsMoreThan72Hours(Util.getDateInMiliSeconds(date))) {
                    holder.assessmentUpdateText.visibility = View.VISIBLE
                    val attemps= assessmentList[position].attempts!!.attempts
                    for(value in attemps!!)
                    {
                        if(value.getRead().toString().trim()==Constants.READ.trim())
                        {
                            holder.assessmentUpdateText.visibility = View.GONE

                        }else
                        {
                            holder.assessmentUpdateText.visibility = View.VISIBLE

                            break
                        }
                    }
                } else {
                    holder.assessmentUpdateText.visibility = View.GONE
                }
            }
        } else {
            holder.assessmentUpdateText.visibility = View.GONE
        }



        holder.parentView.setOnClickListener {
            listener.onItemClicked(assessmentList[position], position)
        }

    }

    interface OnItemClickListener {
        fun onItemClicked(value: AssessmentLearnerBean.FlexpathAssessmentsAndStatus, count: Int)
    }
}

class AssessmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val parentView = view.findViewById<LinearLayout>(R.id.parentView)
    val assessmentCount = view.findViewById<CPTextView>(R.id.assessment_count)
    val assessmentTitle = view.findViewById<CPTextView>(R.id.assessment_title)
    val assessmentTargetDate = view.findViewById<CPTextView>(R.id.assessment_target_date)
    val overdue = view.findViewById<CPTextView>(R.id.overdue)
    val assessmentStatusIcon = view.findViewById<ImageView>(R.id.status_icon)
    val assessmentStatusInformation = view.findViewById<CPTextView>(R.id.status_information)
    val assessmentUpdateText = view.findViewById<CPTextView>(R.id.updateTime)
    val assessmentStatusLayout = view.findViewById<LinearLayout>(R.id.status_information_Layout)

}
