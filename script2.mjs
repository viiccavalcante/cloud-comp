import {
  EC2Client,
  RunInstancesCommand,
  DescribeInstancesCommand,
  CreateImageCommand,
  DescribeImagesCommand
} from "@aws-sdk/client-ec2";
import { Buffer } from "buffer";

const client = new EC2Client({ region: "eu-central-1" });

const userDataScript = `#!/bin/bash
yum update -y
amazon-linux-extras enable corretto17
yum install -y java-17-amazon-corretto git
cd /home/ec2-user
git clone --branch hw2 https://github.com/viiccavalcante/cloud-comp.git
cd cloud-comp
java -jar client/build/libs/client-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
`;

const encodedUserData = Buffer.from(userDataScript).toString("base64");

const params = {
  ImageId: "ami-0767046d1677be5a0",
  InstanceType: "t2.micro",
  MinCount: 1,
  MaxCount: 1,
  KeyName: "my-key",
  SecurityGroupIds: ["sg-094024a92161d00b6"],
  UserData: encodedUserData,
  TagSpecifications: [
    {
      ResourceType: "instance",
      Tags: [{ Key: "Name", Value: "hw1-ec2" }],
    },
  ],
};

const run = async () => {
  console.time("InstanceBootTime");

  const result = await client.send(new RunInstancesCommand(params));
  const instanceId = result.Instances[0].InstanceId;

  let state = "pending";
  while (state !== "running") {
    await new Promise((r) => setTimeout(r, 5000));
    const res = await client.send(
      new DescribeInstancesCommand({ InstanceIds: [instanceId] })
    );
    state = res.Reservations[0].Instances[0].State.Name;
  }

  console.timeEnd("InstanceBootTime");

  console.log("Creating AMI...");
  const createImageRes = await client.send(new CreateImageCommand({
    InstanceId: instanceId,
    Name: `hw1-ec2-ami-${Date.now()}`,
    NoReboot: true,
  }));

  const amiId = createImageRes.ImageId;
  console.log("AMI created:", amiId);

  let amiState = "pending";
  while (amiState !== "available") {
    await new Promise((r) => setTimeout(r, 15000));
    const imageDesc = await client.send(new DescribeImagesCommand({ ImageIds: [amiId] }));
    amiState = imageDesc.Images[0].State;
    console.log("AMI state:", amiState);
  }

  console.log("AMI:", amiId);
};

run().catch(console.error);
