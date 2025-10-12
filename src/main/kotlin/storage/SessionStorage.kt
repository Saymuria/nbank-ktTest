package storage

import dsl.TestUser
import steps.UserSteps

object SessionStorage {
    private val userStepsMap = LinkedHashMap<TestUser, UserSteps>()

    fun addUsers(users: List<TestUser>) {
        for (user in users) {
            userStepsMap[user] = UserSteps(user.username, user.originalPassword)
        }
    }

    /**
     * Возвращаем объект CreateUserRequest по его порядковому номеру в списке созданных пользователей
     * @param number Порядковый номер, начиная с 1(а не с 0)
     * @return Объект CreateUserRequest, соответсвующий указанному порядковому номеру.
     */
    fun getUser(number: Int): TestUser = userStepsMap.keys.elementAt(number - 1)

    fun getUser(): TestUser = getUser(1)

    fun getSteps(number: Int): UserSteps = userStepsMap.values.elementAt(number - 1)

    fun getSteps(): UserSteps = getSteps(1)

    fun clear() = userStepsMap.clear()
}