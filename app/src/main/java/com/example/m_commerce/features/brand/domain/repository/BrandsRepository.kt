package com.example.m_commerce.features.brand.domain.repository

import com.example.m_commerce.features.brand.data.dto.BrandDto
import com.example.m_commerce.features.product.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface BrandsRepository {
    fun getBrands(first: Int): Flow<List<BrandDto>>
    fun getProductsByBrandName(name: String): Flow<List<Product>>
}