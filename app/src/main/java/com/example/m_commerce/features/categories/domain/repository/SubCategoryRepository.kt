package com.example.m_commerce.features.categories.domain.repository

import com.example.m_commerce.features.categories.data.dto.CategoryDto
import kotlinx.coroutines.flow.Flow

interface SubCategoryRepository {
    fun getCategories(): Flow<List<CategoryDto>>
}