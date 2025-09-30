package models.admin

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import models.AbstractListResponseDeserializer
import models.BaseModel
import models.Customer

@JsonDeserialize(using = GetAllUserResponseDeserializer::class)
data class GetAllUserResponse(
    val customers: List<Customer>
): BaseModel

class GetAllUserResponseDeserializer :
    AbstractListResponseDeserializer<Customer, GetAllUserResponse>(
        Customer::class.java,
        ::GetAllUserResponse
    )