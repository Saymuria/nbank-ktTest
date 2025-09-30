package models

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

abstract class AbstractListResponseDeserializer<T : Any, R : BaseModel>(
    private val elementType: Class<T>,
    private val responseConstructor: (List<T>) -> R
) : JsonDeserializer<R>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): R {
        val type = ctxt.typeFactory.constructCollectionType(List::class.java, elementType)
        val list = p.codec.readValue<List<T>>(p, type)
        return responseConstructor(list)
    }
}
