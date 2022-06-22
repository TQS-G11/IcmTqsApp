package pt.ua.icm.icmtqsproject.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.ua.icm.icmtqsproject.data.api.ApiHelper
import pt.ua.icm.icmtqsproject.data.repository.DeliveriesRepository
import pt.ua.icm.icmtqsproject.ui.admin.viewmodel.AdminPageViewModel

class AdminViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPageViewModel::class.java)) {
            return AdminPageViewModel(DeliveriesRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}