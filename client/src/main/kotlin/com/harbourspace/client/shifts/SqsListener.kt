package com.harbourspace.client.shifts

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@Component
class SqsListener {

    private val queueUrl = "https://sqs.eu-central-1.amazonaws.com/164171672120/shifts"
    private val sqsClient: SqsClient = SqsClient.create()

    @PostConstruct
    fun startListening() {
        Thread {
            while (true) {
                try {
                    val request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .waitTimeSeconds(10)
                        .maxNumberOfMessages(5)
                        .build()

                    val messages = sqsClient.receiveMessage(request).messages()
                    for (msg in messages) {
                        println("Received message: ${msg.body()}")

                        sqsClient.deleteMessage {
                            it.queueUrl(queueUrl).receiptHandle(msg.receiptHandle())
                        }
                    }

                    Thread.sleep(2000)
                } catch (e: Exception) {
                    println("Error in SQS listener: ${e.message}")
                }
            }
        }.start()
    }
}
