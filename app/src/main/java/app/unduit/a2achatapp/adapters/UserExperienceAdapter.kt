package app.unduit.a2achatapp.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import app.unduit.a2achatapp.R
import app.unduit.a2achatapp.helpers.gone

enum class UserExperience(val category: String) {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    FIVE_PLUS("5+"),
    TEN_PLUS("10")
}

class UserExperienceAdapter(
    context: Context
) : ArrayAdapter<UserExperience>(context, 0, UserExperience.values()) {

    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View =
            convertView ?: layoutInflater.inflate(R.layout.item_experience_spinner, parent, false)
        getItem(position)?.let { country ->
            setDefaultItemForCategory(view, country)
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (position == 0) {
            view = layoutInflater.inflate(R.layout.item_experience_spinner_header, parent, false)
            view.setOnClickListener {
                val root = parent.rootView
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK))
                root.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK))
            }
        } else {
            view = layoutInflater.inflate(R.layout.item_experience_spinner_dark, parent, false)
            getItem(position)?.let { category ->
                setItemForCategory(view, category)
            }

        }
        return view
    }

    override fun getItem(position: Int): UserExperience? {
        if (position == 0) {
            return null
        }
        return super.getItem(position - 1)
    }

    override fun getCount() = super.getCount() + 1

    override fun isEnabled(position: Int) = position != 0

    private fun setDefaultItemForCategory(view: View, userCategory: UserExperience) {
        val tvCategory = view.findViewById<TextView>(R.id.tv_category)
        tvCategory.text = userCategory.category
    }

    private fun setItemForCategory(view: View, userCategory: UserExperience) {
        val tvCategory = view.findViewById<TextView>(R.id.tv_category)
        val icArrow = view.findViewById<AppCompatImageView>(R.id.ic_arrow)
        tvCategory.text = userCategory.category
        icArrow.gone()
    }

}