package com.sateeshjh.stockmarketapp.domain.repository

import com.sateeshjh.stockmarketapp.domain.model.CompanyListing
import com.sateeshjh.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}