package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.bean.GettingStartedBean
import edu.capella.mobile.android.widgets.CPTextView
import org.w3c.dom.Text


/**
 * GettingStartedListAdapter.kt : :  An adapter class to set Getting Started items in list view
 *
 * Created by Didarul Khan on 04/01/2020.
 *
 * @author Didarul.Khan
 * @version 1.0
 * @since 04/01/2020.
 *
 */



class GettingStartedListAdapter() : RecyclerView.Adapter<GettingStartedListAdapter.GettingStartedViewHolder>() {

    lateinit var context: Context
    lateinit var items: ArrayList<GettingStartedBean.GettingStarted.GettingStartedContent?>
    lateinit var gettingStartedItemListener: GettingStartedItemListener


    constructor(ctx: Context,
                item_s: ArrayList<GettingStartedBean.GettingStarted.GettingStartedContent?>,
                gettingStartedClickListener: GettingStartedItemListener)
            :  this() {
        this.context = ctx
        this.items = item_s
        this.gettingStartedItemListener = gettingStartedClickListener
    }

    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface GettingStartedItemListener {
        fun onItemClicked(value: GettingStartedBean.GettingStarted.GettingStartedContent?)
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): GettingStartedViewHolder {
        return GettingStartedViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.getting_started_row_layout,
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
    override fun onBindViewHolder(parent: GettingStartedViewHolder, position: Int) {

        val tName = items[position]?.title!!

            if(items[position]?.contentType!!.contains("REG" , true)) {
                parent.gettingStartedTitle.text = tName
            }else
            {
                parent.gettingStartedTitle.text = "Forum: "+ tName
            }
        //this.picLoader.displayImage(items[position]?.profileImage!!, parent.matesImageLogo)

        parent.gettingStartedRowParent.setOnClickListener {
            this.gettingStartedItemListener.onItemClicked(items[position])
        }
    }

    /**
     * Returns the size of the list.
     *
     * @return : size of list
     */
    override fun getItemCount(): Int {
        return items.size
    }


    /**
     * An ViewHolder class working as a placeholder for items used in list.
     *
     * @param view
     * @see RecyclerView.ViewHolder
     *
     */
    class GettingStartedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val gettingStartedTitle: TextView = view.findViewById(R.id.gettingStartedTitle)
        val gettingStartedRowParent: LinearLayout = view.findViewById(R.id.gettingStartedRowParent)

    }

}
