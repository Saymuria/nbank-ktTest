package framework.skeleton.interfaces

import models.BaseModel

//общий интерфейс, который описывает ощие методы свойственны конкретному типу эндпоита
//Crud-create,read,update,delete или post,get,put,delete
interface CrudEndpointInterface {
    fun post(model: BaseModel?): Any
    fun get(id: Long): Any
    fun update(id: Long, model: BaseModel): Any
    fun delete(id: Long) : Any
}