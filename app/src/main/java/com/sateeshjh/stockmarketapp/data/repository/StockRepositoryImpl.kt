package com.sateeshjh.stockmarketapp.data.repository

import com.sateeshjh.stockmarketapp.data.csv.CSVParser
import com.sateeshjh.stockmarketapp.data.local.StockDatabase
import com.sateeshjh.stockmarketapp.data.mapper.toCompanyListing
import com.sateeshjh.stockmarketapp.data.mapper.toCompanyListingEntity
import com.sateeshjh.stockmarketapp.data.remote.StockApi
import com.sateeshjh.stockmarketapp.domain.model.CompanyListing
import com.sateeshjh.stockmarketapp.domain.repository.StockRepository
import com.sateeshjh.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    val db: StockDatabase,
    val companyListingsParser: CSVParser<CompanyListing>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))

            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map {
                    it.toCompanyListing()
                }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank() // all company listings
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache) {
                emit(
                    Resource.Loading(isLoading = false)
                )
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load the data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Couldn't load the data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    dao.searchCompanyListing("")
                        .map {
                            it.toCompanyListing()
                        }
                ))
                emit(Resource.Loading(false))
            }
        }
    }
}