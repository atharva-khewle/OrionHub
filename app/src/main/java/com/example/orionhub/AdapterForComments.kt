import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.orionhub.CommentModel
import com.example.orionhub.R

class AdapterForComments(
        private var commentList: List<CommentModel>,
        var context: Context,
        private val onCommentSelected: (CommentModel) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<AdapterForComments.CommentViewHolder>() {

//
//        fun setFilteredList(newList: List<CommentModel>) {
//                commentList = newList
//                notifyDataSetChanged()
//        }

        inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val authorTextView: TextView = itemView.findViewById(R.id.commented_username)
                val contentTextView: TextView = itemView.findViewById(R.id.commented_content)
                val a : ConstraintLayout = itemView.findViewById(R.id.a)
                val replyingto:TextView = itemView.findViewById(R.id.comment_comment_replying_to)
                val replytocomment:LinearLayout=itemView.findViewById(R.id.comment_on_comment)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
                return CommentViewHolder(view)
        }

        override fun getItemCount(): Int = commentList.size

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
                val comment = commentList[position]
                        holder.replytocomment.setOnClickListener {
                                onCommentSelected(commentList[position]) // Invoke the callback
                        }

                holder.authorTextView.setText(comment.authorId)
                holder.contentTextView.setText(comment.content)
                Toast.makeText(context, "1", Toast.LENGTH_SHORT).show()

                if (comment.replyingTo !="Post"){
                        holder.replyingto.setText(comment.replyingTo)
                        holder.a.visibility = View.VISIBLE
                }

        }


}
