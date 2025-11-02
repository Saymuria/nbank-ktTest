package models.customer.updateCustomerProfile

import framework.generators.GeneratingRule
import models.BaseModel

data class UpdateCustomerProfileRequest (
    @GeneratingRule("^[A-Za-z]+\\s[A-Za-z]+$")
    val name : String
) : BaseModel