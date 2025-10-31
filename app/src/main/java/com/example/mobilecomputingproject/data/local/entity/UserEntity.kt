package com.example.mobilecomputingproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobilecomputingproject.domain.model.Address
import com.example.mobilecomputingproject.domain.model.Company
import com.example.mobilecomputingproject.domain.model.Geo
import com.example.mobilecomputingproject.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val addressStreet: String,
    val addressSuite: String,
    val addressCity: String,
    val addressZipcode: String,
    val addressGeoLat: String,
    val addressGeoLng: String,
    val phone: String,
    val website: String,
    val companyName: String,
    val companyCatchPhrase: String,
    val companyBs: String
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    username = username,
    email = email,
    address = Address(
        street = addressStreet,
        suite = addressSuite,
        city = addressCity,
        zipcode = addressZipcode,
        geo = Geo(lat = addressGeoLat, lng = addressGeoLng)
    ),
    phone = phone,
    website = website,
    company = Company(
        name = companyName,
        catchPhrase = companyCatchPhrase,
        bs = companyBs
    )
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    username = username,
    email = email,
    addressStreet = address.street,
    addressSuite = address.suite,
    addressCity = address.city,
    addressZipcode = address.zipcode,
    addressGeoLat = address.geo.lat,
    addressGeoLng = address.geo.lng,
    phone = phone,
    website = website,
    companyName = company.name,
    companyCatchPhrase = company.catchPhrase,
    companyBs = company.bs
)
