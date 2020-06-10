package edu.capella.mobile.android.adapters

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.utils.Constants
import edu.capella.mobile.android.utils.Util
import edu.capella.mobile.android.activity.CampusActivity
import edu.capella.mobile.android.bean.CampusNewsBean
import edu.capella.mobile.android.bean.EventMessage

import edu.capella.mobile.android.widgets.CPTextView

/**
 * CampusAdapter.kt :  An adapter class to set items in list view
 *
 * @author Kush Pandya
 * @version 1.0
 * @since 17/2/2020.
 *
 */


class CampusAdapter(

    val context: Context,
     val items: ArrayList<CampusNewsBean.NewsItem>, private val itemss: ArrayList<CampusActivity.CommonClass>,
     val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<CampusViewHolder>() {

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface OnItemClickListener {
        fun onNewsItemClicked(value: CampusNewsBean.NewsItem)
        fun onAlertItemClicked(message: EventMessage)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CampusViewHolder {
        return CampusViewHolder(LayoutInflater.from(context).inflate(R.layout.campus_row, parent, false))
    }

    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(parent: CampusViewHolder, position: Int) {

        if(itemss[position].itemType.equals("Alert"))
        {
            parent.alertParentLayout.visibility=View.VISIBLE
            parent.newsParentLayout.visibility=View.GONE
            val  eventMessage = itemss[position].itemData as EventMessage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                parent.alertTxt.text =
                    Html.fromHtml(eventMessage.messageText, Html.FROM_HTML_MODE_COMPACT).trim()

                parent.alertTxt.contentDescription = Html.fromHtml(eventMessage.messageText, Html.FROM_HTML_MODE_COMPACT).trim()
            }

            parent.alertParentLayout.setOnClickListener {
                itemClickListener.onAlertItemClicked(eventMessage)
            }
        }
        else
        {
            val  newsItem = itemss[position].itemData as CampusNewsBean.NewsItem
            parent.newsParentLayout.visibility=View.VISIBLE
            parent.alertParentLayout.visibility=View.GONE

            parent.dateTxt.text =  Util.getDate(newsItem.itemPublishDate,Constants.DATE_FORMAT)
            parent.dateTxt.contentDescription = Util.getDate(newsItem.itemPublishDate,Constants.DATE_FORMAT)

            parent.titleTxt.text = HtmlCompat.fromHtml(newsItem.itemTitle,HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            parent.titleTxt.contentDescription = HtmlCompat.fromHtml(newsItem.itemTitle,HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

            parent.newsParentLayout.setOnClickListener {
                itemClickListener.onNewsItemClicked(newsItem)
            }
        }

//        parent.newsParentLayout.visibility=View.VISIBLE
//        parent.dateTxt.text =  Util.getDate(items[position].itemPublishDate,Constants.DATE_FORMAT)
//        parent.titleTxt.text = HtmlCompat.fromHtml(items[position].itemTitle,HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
//        parent.newsParentLayout.setOnClickListener {
//            itemClickListener.onItemClicked(items[position])
//        }


    }

    /**
     * Returns the size of the list.
     *
     * @return : size of list
     */
    override fun getItemCount(): Int {
        return itemss.size
    }
}

/**
 * An ViewHolder class working as a placeholder for items used in list.
 *
 * @param view
 * @see RecyclerView.ViewHolder
 *
 */
class CampusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val dateTxt = view.findViewById<CPTextView>(R.id.dateTxt)!!
    val titleTxt = view.findViewById<CPTextView>(R.id.titleTxt)!!
    val newsParentLayout = view.findViewById<LinearLayout>(R.id.newsParentLayout)!!
    val alertParentLayout = view.findViewById<LinearLayout>(R.id.alertParentLayout)!!
    val alertTxt = view.findViewById<CPTextView>(R.id.alertTxt)!!
}

