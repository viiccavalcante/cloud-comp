package com.harbourspace.client.shifts

import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Service
class SqsPublisher {

    private val sqsClient: SqsClient = SqsClient.builder()
        .region(Region.EU_CENTRAL_1)
        .build()

    private val queueUrl = "https://sqs.eu-central-1.amazonaws.com/164171672120/shifts"

    fun sendSqsMessage(userId: String) {
        val messageBody = "Shift created for user: ${userId}"

        val request = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(messageBody)
            .build()

        sqsClient.sendMessage(request)
        println("Sent to SQS: $messageBody")
    }
}
