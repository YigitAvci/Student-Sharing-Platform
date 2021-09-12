package com.avciy.studentsharingplatform

import android.database.sqlite.SQLiteMisuseException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_view_item.view.*

class HomeRecyclerViewAdapter(
    private val listener: Listener,
    private val ownLikesArray: ArrayList<Boolean>,
    private val likesArray: ArrayList<String>,
    private val postIdArray: ArrayList<String>,
    private val userEmailArray: ArrayList<String>,
    private val downloadUrlArray: ArrayList<String>,
    private val classNameOrCodeArray: ArrayList<String>,
    private val classDateArray: ArrayList<String>,
    private val updateDateArray: ArrayList<String>,
    private val firebaseFirestore : FirebaseFirestore,
) : RecyclerView.Adapter<HomeRecyclerViewAdapter.NoteHolder>() {

    interface Listener {
        fun onItemClick(noteModel: NoteModel)
    }

    class NoteHolder(view: View, firebaseFirestore: FirebaseFirestore) : RecyclerView.ViewHolder(view) {

        val firebaseAut = FirebaseAuth.getInstance()

        fun bind(noteModel: NoteModel, listener: HomeRecyclerViewAdapter.Listener, firebaseFirestore: FirebaseFirestore) {
            itemView.setOnClickListener {
                listener.onItemClick(noteModel)
            }

            itemView.button_like.setOnClickListener {
                val docId = noteModel.postId + firebaseAut.currentUser!!.email!!.substring(0, 9)
                if(it.button_like.isLiked != true) {
                    it.button_like.isLiked = true
                    val data = hashMapOf(
                        "postId" to noteModel.postId,
                        "studentNumber" to firebaseAut.currentUser!!.email!!.substring(0, 9)
                    )
                    Log.d("student number", firebaseAut.currentUser!!.email!!.substring(0, 9))
                    firebaseFirestore.collection("Likes").document(docId).set(data).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("Likes database", "updated")
                            firebaseFirestore.collection("Likes").whereEqualTo("postId", noteModel.postId).get().addOnSuccessListener {
                                val likesAmount = it.documents.size.toString()
                                firebaseFirestore.collection("Posts").document(noteModel.postId)
                                    .update("likesAmount", likesAmount)
                            }
                        }else Log.d("likes database", "failure")
                    }
                }else {
                    it.button_like.isLiked = false
                    firebaseFirestore.collection("Likes").document(docId).delete().addOnSuccessListener {
                        firebaseFirestore.collection("Likes").whereEqualTo("postId", noteModel.postId).get().addOnSuccessListener {
                            val likesAmount = it.documents.size.toString()
                            firebaseFirestore.collection("Posts").document(noteModel.postId)
                                .update("likesAmount", likesAmount)
                        }
                    }
                }
            }
        }

        var imageViewNote: ImageView? = null
        var textViewPostOwner: TextView? = null
        var textViewClassName: TextView? = null
        var textViewClassDate: TextView? = null
        var textViewUpdateDate: TextView? = null
        var textViewLikes: TextView? = null
        var buttonLike: com.like.LikeButton? = null

        init {
            imageViewNote = view.findViewById(R.id.imageView_note)
            textViewPostOwner = view.findViewById(R.id.textView_postOwner)
            textViewClassName = view.findViewById(R.id.textView_className)
            textViewClassDate = view.findViewById(R.id.textView_classDate)
            textViewUpdateDate = view.findViewById(R.id.textView_updateDate)
            textViewLikes = view.findViewById(R.id.textView_likes_amount)
            buttonLike = view.findViewById(R.id.button_like)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_view_item, parent, false)

        return NoteHolder(view, firebaseFirestore)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.textViewPostOwner!!.text = userEmailArray[position]
        holder.textViewClassName!!.text = classNameOrCodeArray[position]
        holder.textViewClassDate!!.text = classDateArray[position]
        holder.textViewUpdateDate!!.text = updateDateArray[position]
        holder.textViewLikes!!.text = likesArray[position]
        holder.buttonLike!!.isLiked = ownLikesArray[position]
        Picasso.get().load(downloadUrlArray[position]).into(holder.imageViewNote)

        var noteModel = NoteModel(
            likesArray[position],
            postIdArray[position],
            userEmailArray[position],
            classNameOrCodeArray[position],
            classDateArray[position],
            downloadUrlArray[position],
            updateDateArray[position]
        )

        holder.bind(noteModel, listener, firebaseFirestore)

    }

    override fun getItemCount(): Int {

        return userEmailArray.size

    }
}

/**
package com.avciy.studentsharingplatform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class HomeRecyclerViewAdapter(private val userEmailArray : ArrayList<String>, private val downloadUrlArray : ArrayList<String>, private val classNameOrCodeArray : ArrayList<String>, private val classDateArray : ArrayList<String>, private val updateDateArray : ArrayList<String>) : RecyclerView.Adapter<HomeRecyclerViewAdapter.NoteHolder>() {

    class NoteHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageViewNote : ImageView? = null
        var textViewPostOwner : TextView? = null
        var textViewClassName : TextView? = null
        var textViewClassDate : TextView? = null
        var textViewUpdateDate : TextView? = null

        init {
            imageViewNote = view.findViewById(R.id.imageView_note)
            textViewPostOwner = view.findViewById(R.id.textView_postOwner)
            textViewClassName = view.findViewById(R.id.textView_className)
            textViewClassDate = view.findViewById(R.id.textView_classDate)
            textViewUpdateDate = view.findViewById(R.id.textView_updateDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_view_item, parent, false)

        return NoteHolder(view)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.textViewPostOwner!!.text = userEmailArray[position]
        holder.textViewClassName!!.text = classNameOrCodeArray[position]
        holder.textViewClassDate!!.text = classDateArray[position]
        holder.textViewUpdateDate!!.text = updateDateArray[position]
        Picasso.get().load(downloadUrlArray[position]).into(holder.imageViewNote)

        fun onclick(holder: NoteHolder, view: View, position: Int) {
        }

    }

    override fun getItemCount(): Int {

        return userEmailArray.size

    }

}

 **/