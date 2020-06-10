package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.Constants.READ
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.activity.AssessmentAttemptDetailActivity
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

class AttemptAdapter(
    val context: Context,
    val attemptList: ArrayList<AssessmentLearnerBean.Attempt>,
    val listener: OnAttemptItemClickListener
) : RecyclerView.Adapter<AttemptAdapter.AttemptViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttemptViewHolder {
        return AttemptViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_attempt_detail,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return attemptList.size
    }

    override fun onBindViewHolder(holder: AttemptViewHolder, position: Int) {

        // for over dues
        when {
            attemptList[position].status.equals(Constants.SUBMITTED, true) -> {
                holder.assessmentStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_file_fill
                    )
                )
            }
            attemptList[position].status.equals(Constants.GRADED) -> {
                holder.assessmentStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_file_fill_blue
                    )
                )
            }
            attemptList[position].status.equals(context.getString(R.string.not_submitted)) -> {
                holder.assessmentStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_file_blank
                    )
                )
            }
        }


        if( attemptList[position].status?.equals(context.getString(R.string.not_yet_available))!!){
            holder.attemptCount.visibility =View.VISIBLE
            holder.statusLayout.visibility= View.GONE
            holder.notAvailable.visibility = View.VISIBLE
            holder.notAvailable.text = attemptList[position].status
            holder.activeAttemptLayout.visibility = View.GONE
            holder.attemptCount.text =
                context.getString(R.string.attempt) +" " + attemptList[position].attemptName
            holder.overDue.visibility = View.GONE
        }else{
            holder.statusLayout.visibility= View.VISIBLE
            holder.attemptCount.visibility =View.GONE
            holder.notAvailable.visibility = View.GONE
            holder.activeAttemptLayout.visibility = View.VISIBLE
            if(attemptList[position].status.equals(Constants.SUBMITTED)) {
                holder.visitUrl.visibility = View.GONE
                holder.assessmentStatusInformation.text = context.getString(R.string.submitted) + " " +attemptList[position].submittedDate?.let {
                    Util.formatDateNew(
                        it
                    )
                }
                holder.overDue.visibility = View.GONE
            }else if(attemptList[position].status.equals(Constants.GRADED)){
                holder.visitUrl.visibility = View.GONE
                holder.assessmentStatusInformation.text = context.getString(R.string.evaluated) + " " + attemptList[position].gradedDate?.let {
                    Util.formatDateNew(
                        it
                    )
                }
                holder.overDue.visibility = View.GONE
            }else {
                holder.visitUrl.visibility = View.VISIBLE
                holder.assessmentStatusInformation.text = context.getString(R.string.not_submitted)
                val date = (context as AssessmentAttemptDetailActivity).getTargetDateToCompare()
                if(date != "" ) {
                    val targetDateInMilis = Util.getDateInMiliSeconds(
                        Util.formatDateTimeNew(
                            date,
                            false,
                            false
                        )
                    )

                    // for over dues
                    if ((System.currentTimeMillis()) > targetDateInMilis && position == 0) {
                        holder.overDue.visibility = View.VISIBLE
                    } else {
                        holder.overDue.visibility = View.GONE
                    }
//                    if(attemptList[position].overdue!!){
//                        holder.overDue.visibility = View.VISIBLE
//                    }else{
//                        holder.overDue.visibility = View.GONE
//                    }
                }else{
                    holder.overDue.visibility = View.GONE
                }

            }
            holder.activeAttemptCount.text =
                context.getString(R.string.attempt) +" " + attemptList[position].attemptName
        }

        holder.visitUrl.setOnClickListener {
            listener.onUrlClick()
        }

        if(attemptList[position].status.equals(Constants.SUBMITTED)) {
            if (attemptList[position].submittedDate != null) {
                if (Util.getDifferenceBetweenDateIsMoreThan72Hours(
                        Util.getDateInMiliSeconds(
                            Util.formatDateTimeNew(
                                attemptList[position].submittedDate!!,
                                false,
                                false
                            )
                        )
                    )
                ) {
                    if (attemptList[position].getRead().toString().trim() == READ.trim()) {
                        holder.assessmentUpdateText.visibility = View.GONE
//                        holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.submitted_on)  + attemptList[position].submittedDate
                    } else {
                        holder.assessmentUpdateText.visibility = View.VISIBLE
//                        holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.evaluated_on)  + attemptList[position].gradedDate + context.getString(R.string.new_or_updated_in_past_3_days)
                    }

                } else {
                    attemptList[position].setRead(Constants.READ)
//                    holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.submitted_on)  + attemptList[position].submittedDate
                    holder.assessmentUpdateText.visibility = View.GONE
                }
            } else {
                attemptList[position].setRead(Constants.READ)
//                holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.submitted_on)  + attemptList[position].submittedDate
                holder.assessmentUpdateText.visibility = View.GONE
            }
        }
        else if(attemptList[position].status.equals(Constants.GRADED))
        {
            if (attemptList[position].gradedDate != null) {

                if (Util.getDifferenceBetweenDateIsMoreThan72Hours(
                        Util.getDateInMiliSeconds(
                            Util.formatDateTimeNew(
                                attemptList[position].gradedDate!!,
                                false,
                                false
                            )
                        )
                    )
                ) {
                    if (attemptList[position].getRead().toString().trim() == READ.trim()) {
                        holder.assessmentUpdateText.visibility = View.GONE
//                        holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.evaluated_on)  + attemptList[position].gradedDate
                    } else {
                        holder.assessmentUpdateText.visibility = View.VISIBLE
//                        holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.evaluated_on)  + attemptList[position].gradedDate + context.getString(R.string.new_or_updated_in_past_3_days)
                    }


                } else {
                    attemptList[position].setRead(Constants.READ)
//                    holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.evaluated_on)  + attemptList[position].gradedDate
                    holder.assessmentUpdateText.visibility = View.GONE
                }
            } else {
                attemptList[position].setRead(Constants.READ)
//                holder.parentView.contentDescription = context.getString(R.string.attempt) + attemptList[position].attemptName + attemptList[position].status + context.getString(R.string.evaluated_on)  + attemptList[position].gradedDate
                holder.assessmentUpdateText.visibility = View.GONE
            }

        }
        else
        {
            holder.assessmentUpdateText.visibility = View.GONE
        }

        holder.parentView.setOnClickListener {
            listener.onItemClicked(attemptList[position])
        }
    }


    interface OnAttemptItemClickListener {
        fun onItemClicked(value: AssessmentLearnerBean.Attempt)
        fun onUrlClick()
    }

    class AttemptViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val activeAttemptLayout = view.findViewById<RelativeLayout>(R.id.active_attempt)
        val activeAttemptCount = view.findViewById<CPTextView>(R.id.active_attempt_count)
        val attemptCount = view.findViewById<CPTextView>(R.id.attempt_count)
        val assessmentStatusIcon = view.findViewById<ImageView>(R.id.status_icon)
        val assessmentStatusInformation = view.findViewById<CPTextView>(R.id.status_information)
        val assessmentUpdateText = view.findViewById<CPTextView>(R.id.updateTimeInfo)
        val notAvailable = view.findViewById<CPTextView>(R.id.notAvailable)
        val statusLayout = view.findViewById<LinearLayout>(R.id.statusLayout)
        val parentView = view.findViewById<LinearLayout>(R.id.parentView)
        val overDue = view.findViewById<CPTextView>(R.id.overdue)
        val visitUrl = view.findViewById<CPTextView>(R.id.visit_url)
    }
}

