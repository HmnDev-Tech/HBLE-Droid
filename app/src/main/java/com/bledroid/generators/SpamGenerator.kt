package com.bledroid.generators

import com.bledroid.models.AdvertisementSet

interface SpamGenerator {
    val name: String
    fun generate(): List<AdvertisementSet>
}
