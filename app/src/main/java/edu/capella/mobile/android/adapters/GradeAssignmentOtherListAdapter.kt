package edu.capella.mobile.android.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.GradesBlueDotUtil
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.GradeFacultyBean


/**
 * DiscussioForumListAdapter.kt : :  An adapter class to set items in list view
 *
 * Created by Jayesh Lahare on 1/3/2020.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 1/3/2020.
 *
 */



class GradeAssignmentOtherListAdapter() : RecyclerView.Adapter<GradeAssignmentOtherListAdapter.GradeRowViewHolder>() {

    lateinit var context: Context
    lateinit var row: ArrayList<GradeAssignmentListAdapter.GradeCollector?>
    lateinit var gradeItemListener: GradeAssignmentItemListener

    var studentAssignmentStatusRecord : GradeFacultyBean? = null

    var isOther : Boolean = false

    constructor(ctx: Context, isOtherType:Boolean, item_s: ArrayList<GradeAssignmentListAdapter.GradeCollector?>, gradeItemListener_: GradeAssignmentItemListener
    ) : this() {
        this.context = ctx
        this.isOther = isOtherType
        this.row = item_s
        this.gradeItemListener = gradeItemListener_
    }

    fun setStatusRecord(record: GradeFacultyBean?)
    {
        studentAssignmentStatusRecord = record
    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface GradeAssignmentItemListener {
        fun onItemClicked(value: GradeAssignmentListAdapter.GradeCollector?)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): GradeRowViewHolder {
        return GradeRowViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_other_grade_assignment,
                parent,
                false
            )
        )
    }

 /*   fun getStatus(status: String): String?
    {
        if(status != null )
        {
            if( status.contains("GRADED" , true))
            {
                return "has been graded"
            }
            else if( status.contains("SUBMITTED" , true))
            {
                return "has been submitted"
            }
            else if( status.contains("LATE" , true))
            {
                return "is Late"
            }
            else if( status.contains("UPCOMING" , true))
            {
                return "is due this week"
            }
        }
        return null
    }*/
    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(parent: GradeRowViewHolder, position: Int) {
        parent.setIsRecyclable(false)
        try
        {
            val item = row[position]

            if(item!!.isFooter != null && item.isFooter == true)
            {
                parent.gradeListItem.visibility = View.GONE
                parent.otherSeeAllGradesTxtFooter.visibility = View.VISIBLE
                parent.otherSeeAllGradesTxtFooter.contentDescription = context.getString(R.string.see_all_grade_and_instr_feedback) + context.getString(R.string.ada_link_will_open_browser)

                //Util.trace("Footer found")

                parent.otherSeeAllGradesTxtFooter.setOnClickListener {
                    if (this.gradeItemListener != null) {
                        this.gradeItemListener.onItemClicked(item)
                    }
                }
                return
            }

            parent.rightArrow.visibility = View.GONE
            parent.titleTxt.text =
                Html.fromHtml(Util.str(item!!.title), Html.FROM_HTML_MODE_LEGACY).toString().trim()


            parent.titleStatusTxt.visibility = View.GONE

            if (item.status != null && item!!.status!!.contains("GRADED", true)) {
                parent.rightFloatingLayout.visibility = View.VISIBLE
                parent.scoreLayout.visibility = View.VISIBLE
                parent.actualScoreTxt.text = Util.getTwoDigitNumber(item.score!!)
                parent.totalPossibleScore.text = "/" + Util.getNonDecimal(item.totalPossibleScore)
                Util.trace("score : " + Util.getTwoDigitNumber(item.score!!) + "/" + item.totalPossibleScore)


                if (item?.gradedDate != null) {

                    var diff = Util.getDateAgo(item?.gradedDate!!)

                    if (diff <= 3  && GradesBlueDotUtil.isUnRead(item?.id, item?.gradedDate , item?.submittedDate , item?.statusDate)) {
                        parent.dueNotificationLayout.visibility = View.VISIBLE
                        parent.dueTextTxt.text =
                            context.getString(R.string.notification_new_or_updated_past_three_days)
                    }
                    var dt = Util.formatDateTimeNew(item?.gradedDate!!, false, false)
                    parent.statusMsgTxt.visibility = View.VISIBLE
                    parent.statusMsgTxt.text = dt

                }
            }

            if (item.status != null && item!!.status!!.contains("LATE", true)) {
                parent.rightFloatingLayout.visibility = View.VISIBLE
                parent.scoreLayout.visibility = View.GONE
                parent.warningImg.visibility = View.VISIBLE
            }

            if (item.status != null && (item!!.status!!.contains("SUBMITTED", true))) {
                var submittedDate = item?.submittedDate
                if (submittedDate == null) {
                    if (isOther == false) {
                        submittedDate = getAssignmentSubmittedDate(item?.id!!)
                    } else {
                        // submittedDate = getOtherSubmittedDate(item?.id!!)
                    }
                }

                if (submittedDate != null) {
                    var dt = Util.formatDateTimeNew(submittedDate, false, false)
                    parent.statusMsgTxt.visibility = View.VISIBLE
                    parent.statusMsgTxt.text = dt

                }
            }



            if (item.status != null && (item!!.status!!.contains(
                    "UPCOMING",
                    true
                ) || item!!.status!!.contains("DUE", true))
            ) {
                parent.dueNotificationLayout.visibility = View.VISIBLE
            }

            parent.gradeListItem.setOnClickListener {
                if (this.gradeItemListener != null) {
                    this.gradeItemListener.onItemClicked(item)
                }
            }
        }catch (t:Throwable){
            Util.trace("OtherAdpter Error $t")
            t.printStackTrace()
        }
    }

    /**
     * Returns the size of the list.
     *
     * @return : size of list
     */
    override fun getItemCount(): Int {
        return row.size
    }




    /**
     * An ViewHolder class working as a placeholder for items used in list.
     *
     * @param view
     * @see RecyclerView.ViewHolder
     *
     */
    class GradeRowViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleTxt: TextView = view.findViewById<TextView>(R.id.titleTxt)
        val titleStatusTxt: TextView = view.findViewById<TextView>(R.id.titleStatusTxt)
        val rightFloatingLayout:RelativeLayout= view.findViewById<RelativeLayout>(R.id.rightFloatingLayout)

        val scoreLayout:LinearLayout= view.findViewById<LinearLayout>(R.id.scoreLayout)
        val actualScoreTxt: TextView= view.findViewById<TextView>(R.id.actualScoreTxt)

        val warningImg:ImageView= view.findViewById<ImageView>(R.id.warningImg)
        val statusMsgTxt: TextView= view.findViewById<TextView>(R.id.statusMsgTxt)
        val dueNotificationLayout:LinearLayout= view.findViewById<LinearLayout>(R.id.dueNotificationLayout)

        val dueTextTxt: TextView = view.findViewById<TextView>(R.id.dueTextTxt)

        val totalPossibleScore: TextView = view.findViewById<TextView>(R.id.totalPossibleScore)

        val gradeListItem:LinearLayout= view.findViewById<LinearLayout>(R.id.gradeListItem)
        val rightArrow:ImageView= view.findViewById(R.id.rightArrow)

        val otherSeeAllGradesTxtFooter:TextView= view.findViewById<TextView>(R.id.otherSeeAllGradesTxtFooter)



    }


    /*****************************************************************************************/
    private fun getAssignmentSubmittedDate(id: String): String?
    {
        if(studentAssignmentStatusRecord == null)
            return  null
        try
        {
            for(item in studentAssignmentStatusRecord?.courseAssignment!!)
            {
                if(id == item?.id)
                {
                    return item?.submittedDateTime
                }
            }

        }catch (t: Throwable)
        {
            Util.trace("getSubmittedDate error $t")
        }
        return null
    }

}
