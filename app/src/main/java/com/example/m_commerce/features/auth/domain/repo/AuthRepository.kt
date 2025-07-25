package com.example.m_commerce.features.auth.domain.repo

import com.example.m_commerce.features.auth.presentation.register.AuthState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(email: String, password: String): Flow<AuthState>
    suspend fun loginUser(email: String, password: String): Flow<AuthState>
    suspend fun sendEmailVerification(user: FirebaseUser) : Flow<AuthState>
    suspend fun createCustomerToken(email: String, password: String, name: String): Flow<String>
    suspend fun createCart(token: String): Flow<String>
    suspend fun storeTokenAndCartId(token: String, cartId: String, uid: String)
}