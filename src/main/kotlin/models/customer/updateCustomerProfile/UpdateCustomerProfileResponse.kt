package models.customer.updateCustomerProfile

import models.BaseModel
import models.Customer

data class UpdateCustomerProfileResponse(
    val message: String,
    val customer: Customer,
) : BaseModel
