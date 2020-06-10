package edu.capella.mobile.android.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.bean.DiscussionForumBean


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



class DiscussionForumListAdapter() : RecyclerView.Adapter<DiscussionForumListAdapter.DiscussionRowViewHolder>() {

    lateinit var context: Context
    lateinit var row: ArrayList<DiscussionForumCollector?>
    lateinit var discussionItemListener: DiscussioForumListItemListener


    constructor(ctx: Context, item_s: ArrayList<DiscussionForumCollector?>, discussionItemListener_: DiscussioForumListItemListener
    ) : this() {
        this.context = ctx
        this.row = item_s
        this.discussionItemListener = discussionItemListener_
    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface DiscussioForumListItemListener {
        fun onItemClicked(value: DiscussionForumCollector?)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DiscussionRowViewHolder {
        return DiscussionRowViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.row_discussion_forum,
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
    override fun onBindViewHolder(parent: DiscussionRowViewHolder, position: Int) {

        parent.setIsRecyclable(false)
        if(row[position]?.isFooter == true)
        {
            parent.yellodig_layout.visibility = View.VISIBLE
            parent.mainLayoutForForum.visibility = View.GONE

            parent.go_to_course_menu.setOnClickListener {
                discussionItemListener.onItemClicked(row[position])
            }
            return

        }else
        {
            parent.yellodig_layout.visibility = View.GONE
            parent.mainLayoutForForum.visibility = View.VISIBLE
        }

        if(row[position]?.isTitleType == true)
        {
            // show title
            parent.discussionForumGroupHeader.visibility = View.VISIBLE
            parent.discussionGroupTitleTxt.text = row[position]?.groupTitle
            parent.discussionForumListItem.visibility = View.GONE
            parent.separatorLine.visibility = View.GONE


        }else
        {
            parent.discussionForumGroupHeader.visibility = View.GONE
            parent.discussionForumListItem.visibility = View.VISIBLE
            parent.separatorLine.visibility = View.VISIBLE
            parent.discussionTitleTxt.text = row[position]?.forumBean!!.title

           /* if(row[position]?.forumBean?.unreadMessageCount!! > 0)
            {
                parent.discussionTitleTxt.setTypeface( parent.discussionTitleTxt.typeface, Typeface.BOLD);
            }else
            {
                parent.discussionTitleTxt.setTypeface( null, Typeface.NORMAL);
            }*/

            var replyAda = ""
            var isUnreadItems = false

            if(row[position]?.forumBean?.unreadMessageCount!! > 0)
            {
                // DO NOTHING, its already Bold
                //parent.discussionTitleTxt.setTypeface( parent.discussionTitleTxt.typeface, Typeface.BOLD);
                isUnreadItems = true
            }else
            {
                //parent.discussionTitleTxt.setTypeface( null, Typeface.NORMAL);
                parent.discussionTitleTxt.typeface =  null // Making it normal or read state
                isUnreadItems = false
            }

            if(((row[position]?.forumBean?.repliesToMeCount!! != null )) && (row[position]?.forumBean?.repliesToMeCount!! > 0) )
            {
                parent.unreadReplyLayout.visibility = View.VISIBLE
                if(row[position]?.forumBean?.repliesToMeCount!! == 1)
                {
                    parent.unreadReplyForumTxt.text = context.getString(R.string.unread_reply_to_you)
                    replyAda =  context.getString(R.string.unread_reply_to_you)
                }else  if(row[position]?.forumBean?.repliesToMeCount!! > 1)
                {
                    parent.unreadReplyForumTxt.text = context.getString(R.string.unread_replies_to_you)
                    replyAda = context.getString(R.string.unread_replies_to_you)
                }
            }else
            {
                parent.unreadReplyLayout.visibility = View.GONE
            }

            if(row[position]?.showSeparator == false)
            {
                parent.separatorLine.visibility = View.INVISIBLE
            }else
            {
                parent.separatorLine.visibility = View.VISIBLE
            }

            var adaText =  row[position]?.forumBean!!.title

            if(replyAda!= "")
            {
                adaText =adaText  + "," + replyAda
            }

            if(isUnreadItems == true)
            {
                adaText = adaText + "," + context.getString(R.string.ada_unread_items)
            }

            parent.discussionForumListItem.contentDescription = adaText

            // show row
            parent.discussionForumListItem.setOnClickListener {
                discussionItemListener.onItemClicked(row[position])
            }
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
    class DiscussionRowViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val discussionForumGroupHeader= view.findViewById<LinearLayout>(R.id.discussionForumGroupHeader)!!
        val discussionGroupTitleTxt= view.findViewById<TextView>(R.id.discussionGroupTitleTxt)!!

        val discussionForumListItem= view.findViewById<RelativeLayout>(R.id.discussionForumListItem)!!
        val discussionTitleTxt= view.findViewById<TextView>(R.id.discussionTitleTxt)!!
        val unreadReplyLayout= view.findViewById<LinearLayout>(R.id.unreadReplyLayout)!!
        val unreadReplyForumTxt= view.findViewById<TextView>(R.id.unreadReplyForumTxt)!!
        val separatorLine= view.findViewById<View>(R.id.separatorLine)!!

        var mainLayoutForForum = view.findViewById<LinearLayout>(R.id.mainLayoutForForum)!!
        var yellodig_layout = view.findViewById<LinearLayout>(R.id.yellodig_layout)!!

        var go_to_course_menu = view.findViewById<TextView>(R.id.go_to_course_menu)!!



    }

    data class DiscussionForumCollector(
        var isTitleType: Boolean? = null,
        var groupTitle: String? = null ,
        var groupId: String? = null,
        var showSeparator:Boolean = true,
        var isFooter:Boolean = false,
        var forumBean: DiscussionForumBean.NewDiscussionData.GroupDiscussion.Forum? = null)
    {
    }
}
