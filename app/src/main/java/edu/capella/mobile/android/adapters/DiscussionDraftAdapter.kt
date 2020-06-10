package edu.capella.mobile.android.adapters

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.bean.DiscussionDraftBean
import edu.capella.mobile.android.widgets.CPTextView
import kotlin.collections.ArrayList


/**
 * DiscussionDraftAdapter.kt :  An adapter class to set items in list view
 *
 * @author Kush Pandya
 * @version 1.0
 * @since 12/3/2020.
 *
 */


class DiscussionDraftAdapter(
    val context: Context,
    private val draftList: ArrayList<DiscussionDraftBean.Draft?>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<DiscussionDraftViewHolder>() {

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface OnItemClickListener {
        fun onItemClicked(value: DiscussionDraftBean.Draft)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DiscussionDraftViewHolder {
        return DiscussionDraftViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.discussion_draft_row,
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
    override fun onBindViewHolder(parent: DiscussionDraftViewHolder, position: Int) {
try {
        val discussionDraft = draftList[position]
        if (discussionDraft!!.parentMessageAuthorName != null) {
            parent.responseToTxt.text =
                context.resources.getString(R.string.response_to) + "" + discussionDraft!!.parentMessageAuthorName.toString().split(
                    "\\s".toRegex()
                )[0];
            parent.responseToTxt.visibility = View.VISIBLE
            val font =
                Typeface.createFromAsset(context.assets, "fonts/roboto_medium_italic.ttf")
            parent.responseToTxt.typeface = font
        }
        parent.subjectTxt.text = discussionDraft!!.subject
        parent.discussionTxt.text =
            context.resources.getString(R.string.discussions) + ": " + discussionDraft!!.forumTitle
        parent.discussionTxt.contentDescription=context.resources.getString(R.string.discussions) + ": " + discussionDraft!!.forumTitle
        parent.draftDiscriptionTxt.text = discussionDraft!!.bodyText
        parent.draftDiscriptionTxt.contentDescription=discussionDraft!!.bodyText
        if (parent.draftDiscriptionTxt.text.length > 140) {
//            var stringFirst = discussionDraft!!.bodyText.substring(0, 140).trim();

            var stringFirst =  Util.getWordsForMoreFeature(discussionDraft!!.bodyText , 140).trim()

            var stringSecond = context.resources.getString(R.string.more)

            val spannable = SpannableString(stringFirst + stringSecond)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    itemClickListener.onItemClicked(draftList[position]!!)

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.setColor(ContextCompat.getColor(context, R.color.checkBoxColor));
                    ds.setUnderlineText(false);
                }
            }

            spannable.setSpan(
                clickableSpan,
                stringFirst!!.length+3,
                stringFirst!!.length + stringSecond!!.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            parent.draftDiscriptionTxt.text = spannable
            parent.draftDiscriptionTxt.setMovementMethod(LinkMovementMethod.getInstance());
        }

    parent.dateTxt.text =
        context.resources.getString(R.string.last_saved_on) + " " + Util.convertDateLocalToCST( Util.getDateForCST(
            discussionDraft.modifiedDate,
            Constants.DATE_FORMAT_AM_PM
        ))+ " " + context.resources.getString(R.string.central)

//        parent.dateTxt.text =
//            context.resources.getString(R.string.last_saved_on) + " " + Util.getDate(
//                discussionDraft.modifiedDate,
//                Constants.DATE_FORMAT_AM_PM
//            ) + " " + context.resources.getString(R.string.central)

//        parent.dateTxt.contentDescription=context.resources.getString(R.string.last_saved_on) + " " + Util.getDate(
//            discussionDraft.modifiedDate,
//            Constants.DATE_FORMAT_AM_PM
//        ) + " " + context.resources.getString(R.string.central)

    parent.dateTxt.contentDescription=context.resources.getString(R.string.last_saved_on) + " " + Util.convertDateLocalToCST( Util.getDateForCST(
        discussionDraft.modifiedDate,
        Constants.DATE_FORMAT_AM_PM
    ))+ " " + context.resources.getString(R.string.central)


        parent.parentLayout.setOnClickListener {
            itemClickListener.onItemClicked(draftList[position]!!)
        }
        parent.draftDiscriptionTxt.setOnClickListener {
            itemClickListener.onItemClicked(draftList[position]!!)
        }

}catch (t: Throwable)
        {
            Util.trace("Draft Crashed : $t")
            t.printStackTrace()
        }
    }

    /**
     * Returns the size of the list.
     *
     * @return : size of list
     */
    override fun getItemCount(): Int {
        return draftList.size
    }
}

/**
 * An ViewHolder class working as a placeholder for items used in list.
 *
 * @param view
 * @see RecyclerView.ViewHolder
 *
 */
class DiscussionDraftViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val draftDiscriptionTxt = view.findViewById<CPTextView>(R.id.draftDiscriptionTxt)!!
    val subjectTxt = view.findViewById<CPTextView>(R.id.subjectTxt)!!
    val dateTxt = view.findViewById<CPTextView>(R.id.dateTxt)!!
    val discussionTxt = view.findViewById<CPTextView>(R.id.discussionTxt)!!
    val parentLayout = view.findViewById<LinearLayout>(R.id.parentLayout)!!
    val responseToTxt = view.findViewById<TextView>(R.id.responseToTxt)!!
}


