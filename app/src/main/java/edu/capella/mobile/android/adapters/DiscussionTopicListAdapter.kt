package edu.capella.mobile.android.adapters


import android.content.Context
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.DiscussionTopicBean


/**
 * DiscussioTopicListAdapter.kt : :  An adapter class to set items in list view
 *
 * Created by Jayesh Lahare on 1/3/2020.
 *
 * @author Jayesh Lahare
 * @version 1.0
 * @since 1/3/2020.
 *
 */

class DiscussionTopicListAdapter() : RecyclerView.Adapter<DiscussionTopicListAdapter.DiscussionTopicRowViewHolder>()
{
    lateinit var context: Context
    lateinit var row: ArrayList<DiscussionTopicBean.NewDiscussionData.Topic?>
    lateinit var discussionTopicItemListener: DiscussioTopicListItemListener

    constructor(ctx: Context, item_s: ArrayList<DiscussionTopicBean.NewDiscussionData.Topic?>, discussionTopicItemListener_: DiscussioTopicListItemListener
    ) : this()
    {
        this.context = ctx
        this.row = item_s
        this.discussionTopicItemListener = discussionTopicItemListener_
    }

    fun setItems(newrow: ArrayList<DiscussionTopicBean.NewDiscussionData.Topic?>)
    {
        this.row = newrow
    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface DiscussioTopicListItemListener {
        fun onItemClicked(value: DiscussionTopicBean.NewDiscussionData.Topic?)
    }

    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DiscussionTopicRowViewHolder {
        return DiscussionTopicRowViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_discussion_topic,
                parent,
                false
            )
        )
    }

    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(parent: DiscussionTopicRowViewHolder, position: Int) {

                parent.setIsRecyclable(false)
                parent.subjectTxt.text =  Html.fromHtml( Util.str(row[position]?.subject) , Html.FROM_HTML_MODE_LEGACY).toString().trim()
                parent.postDateTxt.text = Util.getDate(row[position]?.postDate!! , Constants.DATE_FORMAT)
                parent.postedNameTxt.text = row[position]?.postedName

                var isBold = false

                if(row[position]?.repliesToMeCount!! != null)  //&& (row[position]?.unreadReplyCount!!  > 0 ))
                {
                    val count : Int  =  row[position]?.repliesToMeCount!!.toInt()

                    if(count == 1)
                    {

                        parent.unreadTopicReplyLayout.visibility = View.VISIBLE
                        parent.unreadReplyTxtWithDot.text = context.getString(R.string.unread_reply_to_you)
                    }else if(count > 1)
                    {

                        parent.unreadTopicReplyLayout.visibility = View.VISIBLE
                        parent.unreadReplyTxtWithDot.text = context.getString(R.string.unread_replies_to_you)
                    }else
                    {

                        parent.unreadTopicReplyLayout.visibility = View.GONE
                    }
                }else
                {
//
                    parent.unreadTopicReplyLayout.visibility = View.GONE
                }

                if(row[position]?.postedEmployeeRole != null) {
                    parent.postedEmployeeRoleTxt.text = getRole(row[position]?.postedEmployeeRole.toString())
                    parent.postedEmployeeRoleLayout.visibility  = View.VISIBLE
                }else
                {
                    parent.postedEmployeeRoleLayout.visibility  = View.GONE
                }

                var actualText =  Html.fromHtml( Util.str(row[position]?.bodyText) , Html.FROM_HTML_MODE_LEGACY).toString().trim()

                 if(actualText.length > 140)
                 {
                     var textToDisaply =  actualText
                     var moreText = "more"
                     textToDisaply =  Util.getWordsForMoreFeature(actualText , 140).trim() +"..."
                     textToDisaply += moreText
                     val spanString = SpannableString(textToDisaply)
                     val startIndex: Int = textToDisaply.lastIndexOf(moreText)
                     spanString.setSpan( ForegroundColorSpan(context.getColor(R.color.dark_blue_color)),startIndex,startIndex + moreText.length,
                         Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                     )
                     parent.bodyText.text = spanString
                 }else
                 {
                     parent.bodyText.text = actualText
                 }

            //if(row[position]?.unreadMessageCount !=null && row[position]?.unreadMessageCount!! > 0)
          if( row[position]?.unreadMessageCount!! > 0  || row[position]?.isUnread == true )
            {
/*                if((row[position]?.repliesToMeCount != null )  && (row[position]?.repliesToMeCount!!.toInt() >0 )  )
                {*/
                    isBold = true
               /* }else
                {
                    isBold = false
                }*/
            }
             else
            {
                isBold = false
            }



           if(isBold == true)
            {
                Util.trace("isBold $isBold for " + Util.str(row[position]?.subject))
                 /*****DO NOTHING FOR BOLD BECAUSE IN XML ITS ALREADY MEDIUM WEIGHT****/
               /* parent.subjectTxt.setTypeface( parent.subjectTxt.typeface, Typeface.BOLD)
                parent.postDateTxt.setTypeface( parent.postDateTxt.typeface, Typeface.BOLD)
                parent.postedNameTxt.setTypeface( parent.postedNameTxt.typeface, Typeface.BOLD)
                parent.bodyText.setTypeface( parent.bodyText.typeface, Typeface.BOLD)*/
            }else
            {
                Util.trace("isBold $isBold for " + Util.str(row[position]?.subject))

       /*         parent.subjectTxt.setTypeface( null, Typeface.NORMAL)
                parent.postDateTxt.setTypeface(null, Typeface.NORMAL)
                parent.postedNameTxt.setTypeface( null, Typeface.NORMAL)
                parent.bodyText.setTypeface( null, Typeface.NORMAL)*/
                parent.subjectTxt.typeface = null
                parent.postDateTxt.typeface = null
                parent.postedNameTxt.typeface = null
                parent.bodyText.typeface = null
            }

        parent.discussionTopicListItem.setOnClickListener {
            discussionTopicItemListener.onItemClicked(row[position])
        }

    }

    private fun getRole(role: String?):String
    {
        if(role!!.contains("Primary" , true))
        {
            return context.getString(R.string.primary_instructor)
        }
        else if(role!!.contains("Secondary" , true))
        {
            return context.getString(R.string.secondary_instructor)
        }
        else if(role!!.contains("Writing" , true) || role!!.contains("Coach" , true))
        {
            return context.getString(R.string.writing_coach)
        }

        return role
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
    class DiscussionTopicRowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val discussionTopicListItem: LinearLayout = view.findViewById(R.id.discussionTopicListItem)
        val subjectTxt: TextView = view.findViewById(R.id.subjectTxt)
        val unreadTopicReplyLayout: LinearLayout = view.findViewById(R.id.unreadTopicReplyLayout)
        val postDateTxt: TextView = view.findViewById(R.id.postDateTxt)
        val unreadReplyTxtWithDot: TextView = view.findViewById(R.id.unreadReplyTxtWithDot)
        val postedNameTxt: TextView = view.findViewById(R.id.postedNameTxt)
        val postedEmployeeRoleLayout: LinearLayout = view.findViewById(R.id.postedEmployeeRoleLayout)
        val postedEmployeeRoleTxt: TextView = view.findViewById(R.id.postedEmployeeRoleTxt)
        val bodyText: TextView = view.findViewById(R.id.bodyText)
    }


}
