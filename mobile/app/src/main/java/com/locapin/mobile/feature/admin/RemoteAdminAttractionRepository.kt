package com.locapin.mobile.feature.admin

import com.locapin.mobile.data.remote.AdminAttractionRequest
import com.locapin.mobile.data.remote.AttractionApiService
import com.locapin.mobile.data.remote.DestinationDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Singleton
class RemoteAdminAttractionRepository @Inject constructor(
    private val attractionApiService: AttractionApiService,
    private val fallbackRepository: InMemoryAdminAttractionRepository
) : AdminAttractionRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _attractions = MutableStateFlow(fallbackRepository.attractions.value)
    private val _errorMessage = MutableStateFlow<String?>(null)

    override val attractions: StateFlow<List<AdminAttraction>> = _attractions.asStateFlow()
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        refreshAttractions()
    }

    override fun clearError() {
        _errorMessage.value = null
    }

    override fun getAttractionById(id: String): AdminAttraction? =
        _attractions.value.firstOrNull { it.id == id }

    override fun createAttraction(input: AdminAttractionInput): Boolean = runBlocking {
        runCatching {
            val created = attractionApiService.createAttraction(input.toRequest()).data ?: return@runCatching
            _attractions.value = (_attractions.value + created.toAdminAttraction()).distinctBy { it.id }.sortedBy { it.name.lowercase() }
            _errorMessage.value = null
        }.onFailure {
            _errorMessage.value = "Unable to create attraction. Please try again."
        }.isSuccess
    }

    override fun updateAttraction(id: String, input: AdminAttractionInput): Boolean = runBlocking {
        runCatching {
            val updated = attractionApiService.updateAttraction(id, input.toRequest()).data ?: return@runCatching
            _attractions.value = _attractions.value
                .map { item -> if (item.id == id) updated.toAdminAttraction() else item }
                .sortedBy { it.name.lowercase() }
            _errorMessage.value = null
        }.onFailure {
            _errorMessage.value = "Unable to update attraction. Please try again."
        }.isSuccess
    }

    override fun deleteAttraction(id: String): Boolean = runBlocking {
        runCatching {
            attractionApiService.deleteAttraction(id)
            _attractions.value = _attractions.value.filterNot { it.id == id }
            _errorMessage.value = null
        }.onFailure {
            _errorMessage.value = "Unable to delete attraction. Please try again."
        }.isSuccess
    }

    private fun refreshAttractions() {
        repositoryScope.launch {
            runCatching {
                attractionApiService.getAttractions(size = 100).data.orEmpty()
                    .map(DestinationDto::toAdminAttraction)
                    .sortedBy { it.name.lowercase() }
            }.onSuccess { remoteAttractions ->
                _attractions.value = remoteAttractions
                _errorMessage.value = null
            }.onFailure {
                _attractions.value = fallbackRepository.attractions.value
                _errorMessage.value = "Showing offline attraction data."
            }
        }
    }

    private fun AdminAttractionInput.toRequest() = AdminAttractionRequest(
        name = name,
        knownFor = knownFor,
        description = description,
        category = category,
        latitude = latitude,
        longitude = longitude,
        area = area,
        isVisible = isVisible
    )

    private fun DestinationDto.toAdminAttraction() = AdminAttraction(
        id = id,
        name = name,
        knownFor = description.orEmpty(),
        description = description.orEmpty(),
        category = category?.name.orEmpty(),
        latitude = latitude ?: 0.0,
        longitude = longitude ?: 0.0,
        area = address.orEmpty(),
        isVisible = true
    )
}
