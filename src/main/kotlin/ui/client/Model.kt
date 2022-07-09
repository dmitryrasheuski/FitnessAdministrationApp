package ui.client

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import entity.Client
import java.time.YearMonth
import java.util.Collections

class Model {
    lateinit var selectedClient: State<Client?>

    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val patronymic = mutableStateOf("")

    val balance = mutableStateOf(0)

    val monthList: MutableState<List<YearMonth>> = mutableStateOf(Collections.emptyList())
    val selectedMonth: MutableState<YearMonth?> = mutableStateOf(null)
    val workoutOrderedCount = mutableStateOf(0)
    val workoutVisitedCount = mutableStateOf(0)
    val workoutCanceledCount = mutableStateOf(0)

    fun init(selectedClient: State<Client?>) {
        this.selectedClient = selectedClient
    }
}