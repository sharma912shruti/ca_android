package edu.capella.mobile.android.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.Constants.READ
import edu.capella.mobile.android.utils.Constants.UNREAD
import edu.capella.mobile.android.utils.Util

import edu.capella.mobile.android.bean.CourseDetailBean

import edu.capella.mobile.android.widgets.CPTextView

/**
 * AnnouncementsAdapter.kt :  An adapter class to set items in list view
 *
 * @author Kush Pandya
 * @version 1.0
 * @since 12/3/2020.
 *
 */


class AnnouncementsAdapter(
    val context: Context,
    private val courseAnnoucement: ArrayList<CourseDetailBean.CourseAnnouncement?>,
    private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<AnnouncementsViewHolder>() {

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface OnItemClickListener {
        fun onItemClicked(value: CourseDetailBean.CourseAnnouncement)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): AnnouncementsViewHolder {
        return AnnouncementsViewHolder(LayoutInflater.from(context).inflate(R.layout.announcements_row, parent, false))
    }

    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(parent: AnnouncementsViewHolder, position: Int) {
        parent.dateTxt.text =  Util.getDate(courseAnnoucement[position]?.startDate!!,Constants.DATE_FORMAT)
        Log.e("Date ==",Util.getDate(courseAnnoucement[position]?.startDate!!,Constants.DATE_FORMAT_SEC))
        parent.titleTxt.text = HtmlCompat.fromHtml(courseAnnoucement[position]?.title!!,HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

       var textString= parent.titleTxt.text.toString()

        parent.parentLayout.setOnClickListener {
            itemClickListener.onItemClicked(courseAnnoucement[position]!!)
        }
        if (Util.getDifferenceBetweenDateIsMoreThan72Hours(courseAnnoucement[position]?.startDate!!)) {

            if(courseAnnoucement[position]?.getRead().toString().trim() == UNREAD.trim()) {
                parent.blueDotImage.visibility = View.VISIBLE
//                parent.titleTxt.contentDescription=textString+" "+context.resources.getString(R.string.ada_new_updated_three_days)
                parent.updateText.visibility = View.VISIBLE
            }

            else {
                parent.blueDotImage.visibility = View.GONE
                parent.updateText.visibility = View.GONE
//                parent.titleTxt.contentDescription=textString
                courseAnnoucement[position]?.setRead(READ)
            }

//            parent.blueDotImage.visibility = View.VISIBLE
//            parent.updateText.visibility = View.VISIBLE
        }
        else
        {
            parent.blueDotImage.visibility = View.GONE
            parent.updateText.visibility = View.GONE
            courseAnnoucement[position]?.setRead(READ)
        }
    }

    /**
     * Returns the size of the list.
     *
     * @return : size of list
     */
    override fun getItemCount(): Int {
        return courseAnnoucement.size
    }
}

/**
 * An ViewHolder class working as a placeholder for items used in list.
 *
 * @param view
 * @see RecyclerView.ViewHolder
 *
 */
class AnnouncementsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val dateTxt = view.findViewById<CPTextView>(R.id.dateTxt)!!
    val titleTxt = view.findViewById<CPTextView>(R.id.titleTxt)!!
    val updateText = view.findViewById<CPTextView>(R.id.Txt)
    val parentLayout = view.findViewById<LinearLayout>(R.id.parentLayout)!!
    val blueDotImage = view.findViewById<ImageView>(R.id.blue_dot_image)

}
