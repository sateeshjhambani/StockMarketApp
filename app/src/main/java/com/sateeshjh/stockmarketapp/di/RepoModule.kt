package com.sateeshjh.stockmarketapp.di

import com.sateeshjh.stockmarketapp.data.csv.CSVParser
import com.sateeshjh.stockmarketapp.data.csv.CompanyListingsParser
import com.sateeshjh.stockmarketapp.data.mapper.IntradayInfoParser
import com.sateeshjh.stockmarketapp.data.repository.StockRepositoryImpl
import com.sateeshjh.stockmarketapp.domain.model.CompanyListing
import com.sateeshjh.stockmarketapp.domain.model.IntradayInfo
import com.sateeshjh.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}