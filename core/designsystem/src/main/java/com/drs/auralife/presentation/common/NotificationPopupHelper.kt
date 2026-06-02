package com.drs.auralife.presentation.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.core.designsystem.R
import com.drs.auralife.core.common.util.AppNotification
import com.drs.auralife.presentation.navigation.NavRoutes

@SuppressLint("InflateParams")
class NotificationPopupHelper(
    private val navController: NavController,
) {
    fun show(anchor: android.view.View) {
        val context = anchor.context
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_notification_list, null)
        val rvNotifications = popupView.findViewById<RecyclerView>(R.id.rvNotifications)
        val text = popupView.findViewById<TextView>(R.id.text)

        val notifications = AppNotification.getNotifications(context)

        text.visibility = if (notifications.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE

        rvNotifications.layoutManager = LinearLayoutManager(context)

        val adapter = NotificationAdapter(
            notifications,
            { navController.navigate(NavRoutes.filmDetails(it.first)) },
            { AppNotification.removeNotification(context, it) },
        )

        rvNotifications.adapter = adapter

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true,
        )
        popupWindow.showAsDropDown(anchor)
    }
}
