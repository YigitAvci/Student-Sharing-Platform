package com.avciy.studentsharingplatform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class UsersRecyclerViewAdapter(
    private val listener: Listener, private val photoUrlArray: ArrayList<String>,
    private val studentNumberArray: ArrayList<String>,
    private val displayNameArray: ArrayList<String>,
    private val departmentArray: ArrayList<String>
) : RecyclerView.Adapter<UsersRecyclerViewAdapter.UserHolder>() {

    interface Listener {
        fun onItemClick(userModel: UserModel)
    }

    class UserHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(userModel: UserModel, listener: Listener) {
            itemView.setOnClickListener {
                listener.onItemClick(userModel)
            }
        }

        var imageViewPhotoUrl: CircleImageView? = null
        var textViewDisplayName: TextView? = null
        var textViewStudentNumber: TextView? = null

        init {
            imageViewPhotoUrl = view.findViewById(R.id.imageView_profilePhoto)
            textViewDisplayName = view.findViewById(R.id.textView_displayName)
            textViewStudentNumber = view.findViewById(R.id.textView_studentNumber)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.users_recycler_view_item, parent, false)

        return UserHolder(view)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {

        Picasso.get().load(photoUrlArray[position]).into(holder.imageViewPhotoUrl)
        holder.textViewDisplayName!!.text = displayNameArray[position]
        holder.textViewStudentNumber!!.text = studentNumberArray[position]

        var userModel = UserModel(
            studentNumberArray[position],
            displayNameArray[position],
            departmentArray[position],
            photoUrlArray[position]
        )

        holder.bind(userModel, listener)
    }

    override fun getItemCount(): Int {
        return studentNumberArray.size
    }
}