package com.example.m_commerce.features.search.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.core.utils.NetworkManager
import com.example.m_commerce.features.product.domain.entities.Product
import com.example.m_commerce.features.product.domain.usecases.AddToWishlistUseCase
import com.example.m_commerce.features.product.domain.usecases.GetProductByIdUseCase
import com.example.m_commerce.features.product.presentation.SnackBarMessage
import com.example.m_commerce.features.search.domain.usecases.GetProductsUseCase
import com.example.m_commerce.features.wishlist.domain.usecases.DeleteFromWishlistUseCase
import com.example.m_commerce.features.wishlist.domain.usecases.GetWishlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getWishlist: GetWishlistUseCase,
    private val getProductById: GetProductByIdUseCase,
    private val getProducts: GetProductsUseCase,
    private val networkManager: NetworkManager,
    private val addToWishlist: AddToWishlistUseCase,
    private val deleteFromWishlist: DeleteFromWishlistUseCase,
) : ViewModel() {

    private var _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var _message = MutableSharedFlow<SnackBarMessage>()
    val message = _message.asSharedFlow()

    private var allProducts = emptyList<Product>()
    private var filteredProducts = emptyList<Product>()

    var isLoading by mutableStateOf(false)
        private set

    var colors = mutableListOf<String>()
    var categories = mutableListOf<String>()
    var brands = mutableListOf<String>()

    var priceRange = MutableStateFlow(0f..300f)

    fun clear() {
        _uiState.tryEmit(SearchUiState.Loading)
        filteredProducts = allProducts
        _uiState.tryEmit(SearchUiState.Success(filteredProducts))
    }

    fun deleteProduct(
        product: Product,
        query: String,
        selectedFilters: SnapshotStateMap<String, List<String>>,
        range: ClosedFloatingPointRange<Float>
    ) = viewModelScope.launch {
        if (!networkManager.isNetworkAvailable()) {
            _message.emit(SnackBarMessage("No internet connection"))
            return@launch
        }

        isLoading = true
        //_uiState.emit(SearchUiState.Loading)
        deleteFromWishlist(product.id)
            .catch { _message.emit(SnackBarMessage("Failed to remove product from wishlist: ${it.message}")) }
            .collect {

                allProducts = allProducts.filter { newProduct -> newProduct.id != product.id }
                searchAndFilter(query, selectedFilters, range)
                isLoading = false
                _message.emit(SnackBarMessage(
                    message = it,
                    actionLabel = "Undo",
                    onAction = {
                        isLoading = true
                        viewModelScope.launch {
                            addToWishlist(product.id)
                                .catch { e ->
                                    _message.emit(SnackBarMessage("Failed to add product to wishlist: ${e.message}"))
                                }
                                .collect {
                                    //_uiState.emit(SearchUiState.Loading)
                                    allProducts = allProducts + product
                                    searchAndFilter(query, selectedFilters, range)
                                    isLoading = false
                                }
                        }
                    }
                ))
            }
    }

    fun searchAndFilter(
        query: String,
        selectedFilters: SnapshotStateMap<String, List<String>>,
        range: ClosedFloatingPointRange<Float>
    ) {
        _uiState.value = SearchUiState.Loading

        filteredProducts = allProducts.filter { product ->

            val matchesFilters = selectedFilters.all { (key, value) ->
                when (key) {
                    "Color" -> value.any { v -> product.colors.any { it.equals(v, true) } }
                    "Category" -> value.any { v -> product.category.equals(v, true) }
                    "Brand" -> value.any { v -> product.brand.equals(v, true) }
                    else -> true
                }
            }

            val matchesQuery = product.title.contains(query, ignoreCase = true)
            val matchesPrice = product.price.toFloat() in range

            matchesFilters && matchesQuery && matchesPrice
        }

        _uiState.value = if (filteredProducts.isEmpty()) {
            SearchUiState.Empty
        } else {
            val sortedProducts = filteredProducts.sortedBy { it.id }
            SearchUiState.Success(sortedProducts)
        }
    }


    fun getAllProducts(isWishlist: Boolean) =
        viewModelScope.launch {
            if (!isConnected()) return@launch

            _uiState.emit(SearchUiState.Loading)
            allProducts = if (isWishlist) {
                getWishlistProducts()
            } else {
                fetchAllProducts()
            }

            colors = allProducts
                .flatMap { it.colors }
                .toSet()
                .toList().toMutableList()

            categories = allProducts
                .map { it.category }
                .distinct()
                .toMutableList()

            brands = allProducts
                .map { it.brand }
                .distinct()
                .toMutableList()

            fetchPriceRange()
            _uiState.emit(SearchUiState.Success(allProducts))
        }

    private suspend fun fetchPriceRange() {
        val max = ceil(allProducts.maxOfOrNull { it.price.toFloat() } ?: 100f)
        priceRange.emit(0f..max)
    }

    private suspend fun getWishlistProducts(): List<Product> {

        return getWishlist() // List<String>
            .flatMapConcat { ids ->
                if (ids.isEmpty()) {
                    flowOf(emptyList()) // List<Product>
                } else {
                    ids
                        .asFlow() // Flow<String>
                        .flatMapMerge { id ->
                            getProductById(id) // Flow<Product>
                        }
                        .toList()   // Flow<List<Product>>
                        .let { productsList -> flowOf(productsList) }
                }
            }
            .catch { e -> _uiState.emit(SearchUiState.Error(e.message ?: "Unknown error")) }
            .first()
    }

    private suspend fun fetchAllProducts() = getProducts()
        .catch { e ->
            if (e is SecurityException) _uiState.emit(SearchUiState.Empty)
            else _uiState.emit(SearchUiState.Error("Unknown error: ${e.message}"))
        }
        .first()

    private fun isConnected(): Boolean {
        if (!networkManager.isNetworkAvailable()) {
            _uiState.tryEmit(SearchUiState.NoNetwork)
            return false
        } else return true
    }
}