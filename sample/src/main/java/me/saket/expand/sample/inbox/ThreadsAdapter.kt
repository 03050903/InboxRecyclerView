package me.saket.expand.sample.inbox

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import me.saket.expand.sample.EmailThread
import me.saket.expand.sample.R

class ThreadsAdapter : ListAdapter<EmailThread, EmailViewHolder>(EmailThread.ItemDiffer()) {

  val itemClicks = PublishRelay.create<EmailThreadClicked>()!!

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
    val threadLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_email_thread, parent, false)
    return EmailViewHolder(threadLayout, itemClicks)
  }

  override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
    holder.emailThread = getItem(position)
    holder.render()
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }
}

class EmailViewHolder(
    itemView: View,
    itemClicks: PublishRelay<EmailThreadClicked>
) : RecyclerView.ViewHolder(itemView) {

  private val bylineTextView = itemView.findViewById<TextView>(R.id.emailthread_item_byline)
  private val subjectTextView = itemView.findViewById<TextView>(R.id.emailthread_item_subject)
  private val bodyTextView = itemView.findViewById<TextView>(R.id.emailthread_item_body)

  lateinit var emailThread: EmailThread

  init {
    itemView.setOnClickListener {
      itemClicks.accept(EmailThreadClicked(emailThread, adapterPosition, itemId))
    }
  }

  @SuppressLint("SetTextI18n")
  fun render() {
    val latestEmail = emailThread.emails.last()
    bylineTextView.text = "${emailThread.sender.name} — ${latestEmail.timestamp}"

    subjectTextView.text = emailThread.subject
    val subjectTextSize = subjectTextView.resources.getDimensionPixelSize(when {
      latestEmail.hasImageAttachments -> R.dimen.emailthread_subject_textize_with_photo_attachments
      else -> R.dimen.emailthread_subject_textize_without_photo_attachments
    })
    subjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, subjectTextSize.toFloat())

    bodyTextView.text = latestEmail.body.replace("\n", " ")
    bodyTextView.visibility = if (latestEmail.showBodyInThreads) View.VISIBLE else View.GONE
  }
}
