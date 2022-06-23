package pt.ua.icm.icmtqsproject.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import pt.ua.icm.icmtqsproject.data.repository.DeliveriesRepository
import pt.ua.icm.icmtqsproject.utils.Resource

class HomePageViewModel(private val deliveriesRepository: DeliveriesRepository) : ViewModel() {
    fun getDeliveries(token:String ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = deliveriesRepository.getDeliveries(token)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}