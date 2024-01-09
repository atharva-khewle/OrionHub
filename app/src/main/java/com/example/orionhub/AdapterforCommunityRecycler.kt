//package com.example.orionhub
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.RecyclerView
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
//
//class AdapterforCommunityRecycler (var mylist :List<String>, context : Context): RecyclerView.Adapter<AdapterforCommunityRecycler.listViewHolder>(){
//
//    fun setfilteredlist(newlist : List<String>){
//        this.mylist = newlist
//        notifyDataSetChanged()
//    }
//
//    inner class listViewHolder(itemview : View):RecyclerView.ViewHolder(itemview){
//        val subtext : TextView=itemview.findViewById(R.id.searchbar_sub_text)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): listViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_for_search,parent,false)
//        return listViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//return mylist.size   }
//
//    override fun onBindViewHolder(holder: listViewHolder, position: Int) {
//        holder.subtext.setText(mylist[position])
//    }
//
//
//
//    private fun openSubredditFragment(subredditName: String) {
//        val fragment = SubredditFragment.newInstance(subredditName)
//        displayFragment(fragment)
//    }
//
//
//
//    private fun displayFragment(fragment: Fragment ) {
//        context.supportFragmentManager .beginTransaction()
//            .replace(R.id.inMainFrag_layout, fragment) // Replace with your FrameLayout ID
//            .commit()
//    }
//
//
//
////    private fun fragmentReplace(frag: Fragment){
////        var fragmanager = parentFragmentManager
////        var transaction = fragmanager.beginTransaction()
//////        val fragmentManager = supportFragmentManager
////        val fragmentTransaction = fragmanager.beginTransaction()
////        fragmentTransaction.replace(R.id.inMainFrag_layout, frag)
////        fragmentTransaction.commit()
////    }
////
//
//
//}


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orionhub.R

class AdapterforCommunityRecycler(
    private var myList: List<String>,
    private val onSubredditSelected: (String) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<AdapterforCommunityRecycler.ListViewHolder>() {

    fun setFilteredList(newList: List<String>) {
        myList = newList
        notifyDataSetChanged()
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subText: TextView = itemView.findViewById(R.id.searchbar_sub_text)

        init {
            itemView.setOnClickListener {
                onSubredditSelected(myList[adapterPosition]) // Invoke the callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_for_search, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.subText.text = "r/"+myList[position]
    }
}
