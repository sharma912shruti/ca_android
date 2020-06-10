package edu.capella.mobile.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.capella.mobile.android.R
import edu.capella.mobile.android.activity.MenuActivity
import edu.capella.mobile.android.widgets.CPTextView


/**
 * MenuAdapter.kt :  An adapter class to set items in list view
 *
 * Created by SSharma on 25/2/2020.
 *
 * @author SSharma
 * @version 1.0
 * @since 25/2/2020.
 *
 */

class MenuAdapter(val context: Context,  val drawerItem: ArrayList<String>,  val drawerIcons :ArrayList<Int>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null


    /**
     * Listener for callback which tells user about selected Item in the list.
     *
     */
    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    /**
     * Method to sets the callback method.
     *
     * @param mItemClick
     */
    fun setOnIemClickListener(mItemClick: OnItemClickListener) {
        this.itemClickListener = mItemClick
    }


    /**
     * Factory method  used to inflate a layout ,return view in ViewHolder class
     *
     * @param parent
     * @param p1
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_item_drawer, parent, false)
        return DrawerRowViewHolder(v)
    }

    /**
     * Returns the size of the list.
     *
     * @return : size of list
     */
    override fun getItemCount(): Int {
        return drawerItem.size
    }

    /**
     * Factory method is used to initialize value of variable
     *
     * @param parent
     * @param position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DrawerRowViewHolder) {
            holder.menuIcon.setImageResource(drawerIcons[position])
            holder.menuName.text = drawerItem[position]
            holder.parentView.setOnClickListener {  itemClickListener?.onItemClicked(drawerIcons[position])}

            if(drawerItem[position] == context.resources.getString(R.string.capella_email)){
                val count = (context as MenuActivity).getUnreadEmailCount().toString()
                holder.menuName.contentDescription = context.resources.getString(R.string.capella_email) + count +" "+context.resources.getString(R.string.action_available)+" "+ context.resources.getString(R.string.link_will_open)
                holder.emailCount.visibility = View.VISIBLE
                holder.emailCount.text = count
            }else{
                holder.emailCount.visibility = View.GONE
                if (drawerItem[position].equals(context.resources.getString(R.string.menu_view_campus_on_full_site))){
                    holder.menuName.contentDescription = context.resources.getString(R.string.menu_view_campus_on_full_site) +" "+context.resources.getString(R.string.action_available)+" "+ context.resources.getString(R.string.link_will_open)
                }else{
                    holder.menuName.contentDescription = drawerItem[position]+" "+context.resources.getString(R.string.action_available)
                }
            }

        }
    }

    /**
     * An ViewHolder class working as a placeholder for items used in list.
     *
     * @param view
     * @see RecyclerView.ViewHolder
     *
     */
    inner class DrawerRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuIcon: ImageView = itemView.findViewById(R.id.menu_icon)
        var menuName: TextView = itemView.findViewById(R.id.menu_name)
        var parentView: LinearLayout = itemView.findViewById(R.id.parentLinearLayout)
        var emailCount:CPTextView = itemView.findViewById(R.id.emailCount)
    }

}
