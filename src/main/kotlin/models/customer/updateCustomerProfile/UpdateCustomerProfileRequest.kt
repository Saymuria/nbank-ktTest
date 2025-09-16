package models.customer.updateCustomerProfile

import framework.generators.GeneratingRule
import models.BaseModel

data class UpdateCustomerProfileRequest (
    @GeneratingRule("[a-zA-Z]")
    val name : String
) : BaseModel