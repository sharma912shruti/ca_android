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
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.GradeFacultyBean

import java.io.Serializable


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



class GradeAssignmentListAdapter() : RecyclerView.Adapter<GradeAssignmentListAdapter.GradeRowViewHolder>() {

    lateinit var context: Context
    lateinit var row: ArrayList<GradeCollector?>
    lateinit var gradeItemListener: GradeAssignmentItemListener

    var studentAssignmentStatusRecord : GradeFacultyBean? = null

    var isOther : Boolean = false

    constructor(ctx: Context,isOtherType:Boolean, item_s: ArrayList<GradeCollector?>, gradeItemListener_: GradeAssignmentItemListener
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
        fun onItemClicked(value: GradeCollector?)
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
                R.layout.row_grade_assignment,
                parent,
                false
            )
        )
    }

    fun getStatus(item : GradeCollector): PlaceHolder?
    {
                             var status: String? = item?.status

                            if( status!!.contains("GRADED" , true))
                            {
                                var data = PlaceHolder(status , item.gradedDate)
                                return data
                            }
                            else if( status.contains("SUBMITTED" , true))
                            {
                                var data = PlaceHolder(status , item.submittedDate)
                                return data
                            }
                            else if( status.contains("is late" , true))
                            {
                                var data = PlaceHolder(status , item.statusDate)
                                return data
                            }
                            else if( status.contains("is due this week" , true))
                            {

                                var data = PlaceHolder(status , item.statusDate)
                                return data
                            }
                            else if( status.contains("UPCOMING" , true))
                            {
                                var data = PlaceHolder(status , null)
                                return data
                            }


        return  PlaceHolder(status , null)
    }

    inner class PlaceHolder(var status: String? ,var  date : String?)
    {

    }

    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(parent: GradeRowViewHolder, position: Int) {
        parent.setIsRecyclable(false)
        try {
            val item = row[position]

            parent.titleTxt.text =
                Html.fromHtml(Util.str(item!!.title), Html.FROM_HTML_MODE_LEGACY).toString().trim()

            var stautsOfItem: PlaceHolder = getStatus(item)!!


            if (stautsOfItem != null) {

                parent.titleStatusTxt.text = stautsOfItem.status
            } else {
                parent.titleStatusTxt.visibility = View.GONE
            }


            if (item.status != null && item!!.status!!.contains("GRADED", true))
            {
                parent.rightFloatingLayout.visibility = View.VISIBLE
                parent.scoreLayout.visibility = View.VISIBLE
                parent.actualScoreTxt.text = Util.getTwoDigitNumber(item.score!!)
                parent.totalPossibleScore.text = "/" + Util.getNonDecimal(item.totalPossibleScore)


                if (item?.gradedDate != null)
                {
                    var diff = Util.getDateAgo(item?.gradedDate!!)

                    if (diff <= 3 && item!!.isUnRead) {
                        parent.dueNotificationLayout.visibility = View.VISIBLE
                        parent.dueTextTxt.text =   context.getString(R.string.notification_new_or_updated_past_three_days)
                    }

                    var dt = Util.formatDateTimeNew(item?.gradedDate!!, false, false)
                    parent.statusMsgTxt.visibility = View.VISIBLE
                    parent.statusMsgTxt.text = dt

                }else
                {
                    parent.statusMsgTxt.visibility = View.GONE
                    parent.dueNotificationLayout.visibility = View.GONE
                }

                Util.trace("For GRADED " + parent.statusMsgTxt.text + " Unit : " + parent.titleTxt.text)
            } else if (item.status != null && item!!.status!!.contains("LATE", true))
            {
                parent.rightFloatingLayout.visibility = View.VISIBLE
                parent.scoreLayout.visibility = View.GONE
                parent.warningImg.visibility = View.VISIBLE

                if(item?.statusDate !=null) {
                    var dt = Util.formatDateTimeNew(item?.statusDate!!, false, false)
                    parent.statusMsgTxt.visibility = View.VISIBLE
                    parent.statusMsgTxt.text = "As of: " + dt

                    /**
                     * On request of anuj, removing notificaiton for LATE scenerio*/
                    var diff = Util.getDateAgo(item?.statusDate!!)
                    diff = Math.abs(diff)
                    /*if (diff <= 3 && item!!.isUnRead) {
                        parent.dueNotificationLayout.visibility = View.VISIBLE
                        parent.dueTextTxt.text =
                            context.getString(R.string.new_or_updated_in_past_3_days)
                    } else {
                        parent.dueNotificationLayout.visibility = View.VISIBLE
                        parent.dueTextTxt.text = context.getString(R.string.late)
                    }*/
                    /** as par Micaela dot upto 72 hrs only)*/
                    if (diff <= 3 && item!!.isUnRead) {
                        parent.dueNotificationLayout.visibility = View.VISIBLE
                        parent.dueTextTxt.text =
                            context.getString(R.string.late)
                    }
                }else
                {
                    parent.statusMsgTxt.visibility = View.GONE
                    parent.dueNotificationLayout.visibility = View.GONE
                }


                Util.trace("For Late " + parent.statusMsgTxt.text + " Unit : " + parent.titleTxt.text)
            } else if (item.status != null && (item!!.status!!.contains("SUBMITTED", true))) {
                var submittedDate = item?.submittedDate


                if (submittedDate != null) {
                    var dt = Util.formatDateTimeNew(submittedDate, false, false)
                    parent.statusMsgTxt.visibility = View.VISIBLE
                    parent.statusMsgTxt.text = dt

                    var diff = Util.getDateAgo(item?.submittedDate!!)

                    if (diff <= 3 && item?.isUnRead)
                    {
                        parent.dueNotificationLayout.visibility = View.VISIBLE
                        parent.dueTextTxt.text =
                            context.getString(R.string.notification_new_or_updated_past_three_days)
                    }
                }else
                {
                    parent.statusMsgTxt.visibility = View.GONE
                    parent.dueNotificationLayout.visibility = View.GONE
                }
                Util.trace("For SUBMITTED " + parent.statusMsgTxt.text + " Unit : " + parent.titleTxt.text)
            } else if (item.status != null && item!!.status!!.contains("due this week", true)) {

                if(item?.statusDate!=null)
                {
                    var dt = Util.formatDateTimeNew(item?.statusDate!!, false, false)
                    parent.statusMsgTxt.visibility = View.VISIBLE
                    parent.statusMsgTxt.text = "Submit by: " + dt

                    var diff = Util.getDateAgo(item?.statusDate!!)

                    if (diff <= 3 && item?.isUnRead)
                    {
                        parent.dueNotificationLayout.visibility = View.VISIBLE
                        parent.dueTextTxt.text = context.getString(R.string.due_soon)
                        var days: Long = Util.getDateAgo(item.statusDate!!)
                        Util.trace("Days ago is : $days")
                        if (days.compareTo(0) == 0) {
                            parent.titleStatusTxt.text = context.getString(R.string.is_due_today)
                            parent.statusMsgTxt.text = dt
                            parent.dueTextTxt.text = context.getString(R.string.due_today)
                        }
                    }

                    var days: Long = Util.getDateAgo(item.statusDate!!)

                    if (days.compareTo(0) == 0) {
                        parent.titleStatusTxt.text = context.getString(R.string.is_due_today)
                    }


                    if (item?.isUnRead == false) {
                        parent.dueNotificationLayout.visibility = View.GONE
                    }
                }else
                {
                    parent.statusMsgTxt.visibility = View.GONE
                    parent.dueNotificationLayout.visibility = View.GONE
                }

                Util.trace("For DUE week/today " + parent.statusMsgTxt.text + " Unit : " + parent.titleTxt.text)

            } else {
                parent.statusMsgTxt.visibility = View.GONE
            }



            parent.gradeListItem.setOnClickListener {
                if (this.gradeItemListener != null) {
                    this.gradeItemListener.onItemClicked(item)
                }
            }
        }catch (t:Throwable){
            Util.trace("GradeAssignmentListAdapter error : $t")
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


    }

    data class GradeCollector(
        var   id : String? = null ,
        var  title: String? = null ,
        var  score: String? = null ,
        var  totalPossibleScore: String? = null ,
        var  status: String? = null ,
       var  instructionLink: String? = null ,
       var  forumWebLink: String? = null ,
        var  link: String? = null ,
        var  instructions: String? = null ,
        var  webLink: String? = null ,
        var  gradedDate: String? = null ,
        var  submittedDate: String? = null,
        var  statusDate: String? = null,
        var statusMessage : String? = null,
        var isFooter:Boolean? = null,
         var isUnRead: Boolean = false) : Serializable
    {
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
