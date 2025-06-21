import { SQSClient, CreateQueueCommand } from "@aws-sdk/client-sqs";

const client = new SQSClient({ region: "eu-central-1" });

const run = async () => {
  const command = new CreateQueueCommand({
    QueueName: "shifts",
    Attributes: {
      DelaySeconds: "0",
      MessageRetentionPeriod: "86400",
    },
  });

  const response = await client.send(command);
  console.log("Queue URL:", response.QueueUrl);
};

run().catch(console.error);
