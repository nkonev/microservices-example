package com.example.webapp.repo

import com.example.webapp.dto.EditChatDto
import com.example.webapp.dto.ResponseChatDto
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import javax.inject.Inject


class ChatRepository {
    @Inject
    private lateinit var mongoClient: MongoClient

    companion object {
        const val dbName = "chat"
        const val collectionName = "chats"
    }

    fun insertChat(name: String, ownerId: String) {
        val map = mapOf(
                "name" to name,
                "owner" to ownerId
        )
        val document: BasicDBObject = BasicDBObject(map)
        mongoClient.getDatabase(dbName).getCollection(collectionName, BasicDBObject::class.java).insertOne(document)
    }

    fun getChats(): List<ResponseChatDto> {
        val ret: List<ResponseChatDto> = mongoClient.getDatabase(dbName).getCollection(collectionName, BasicDBObject::class.java).find().limit(100)
                .map(fun(basicDBObject: BasicDBObject?): ResponseChatDto {
                    val name: String = basicDBObject?.getString("name") ?: throw RuntimeException("Cannot get name")
                    val owner: String = basicDBObject.getString("owner") ?: throw RuntimeException("Cannot get owner")
                    val dto: ResponseChatDto = ResponseChatDto(name, owner)
                    return dto
                }
                ).toList()
        return ret
    }

    fun editChat(chatName: EditChatDto, userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}