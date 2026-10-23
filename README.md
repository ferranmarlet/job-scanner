# jobscan

## Working locally

Copy `.env.example` to `.env.properties` and fill in the values. The local database username and password are the ones in `compose.yaml`.

You can use docker to run the application and its database, or run the application directly on your local machine, as explained below.

### To launch dockerized application locally

```sh
docker compose up --build
```
This starts the application and a PostgreSQL database. The API is then available at http://localhost:8080. Press Ctrl+C to stop the containers. The database keeps its data until you run `docker compose down -v`.

### To launch the application on your local machine

Start only the PostgreSQL local database, then run the application:
```sh
docker compose up -d --wait postgres
./gradlew bootRun
```

### Other commands

- To build the application:
```sh
./gradlew build
```

- To launch the test suite. The tests start their own temporary PostgreSQL with Testcontainers, so Docker has to be running, but the local database doesn't:
```sh
./gradlew test
```

- To launch a single test:
```sh
./gradlew test --tests {test name}
```

## Deploying a new version

Run these scripts locally to release a new version of the app. You can run them from any directory; the examples below run from the project root.

### 1. Push the Docker image to ECR

`scripts/build_push_docker_image.sh` builds the application image and pushes it to ECR with the `latest` tag:

```sh
scripts/build_push_docker_image.sh
```

### 2. Publish the image

Pushing the image doesn't update the running app. `scripts/publish_docker_image.sh` forces a new ECS deployment so the service starts the latest image, then waits until the deployment finishes:

```sh
scripts/publish_docker_image.sh
```

The API is briefly unavailable during the deployment, because the old task stops before the new one starts.

### Prerequisites

- Docker, Terraform and the AWS CLI are installed.
- You are logged in to AWS (`aws login`).
- Terraform has been initialized and the infrastructure exists. The first deployment has to create the ECR repository, push the image, and then create everything else, so run these instead of the steps above:

  ```sh
  terraform -chdir=terraform init
  terraform -chdir=terraform apply -var-file=dev.tfvars -target=aws_ecr_repository.app
  scripts/build_push_docker_image.sh
  terraform -chdir=terraform apply -var-file=dev.tfvars
  ```

### Working with deployed code
- To open a tunnel to the ECS instance:

  1. Install the plugin (Ubuntu, x86_64):
```sh
curl -fsSL -o /tmp/session-manager-plugin.deb \
https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb
sudo dpkg -i /tmp/session-manager-plugin.deb
session-manager-plugin --version
```

  2. Open the tunnel:
```sh
ASG=$(terraform -chdir=terraform output -raw asg_name)
INSTANCE_ID=$(aws ec2 describe-instances --region eu-west-1 \
--filters "Name=tag:aws:autoscaling:groupName,Values=$ASG" "Name=instance-state-name,Values=running" \
--query 'Reservations[0].Instances[0].InstanceId' --output text)

aws ssm start-session --region eu-west-1 --target "$INSTANCE_ID" \
--document-name AWS-StartPortForwardingSession \
--parameters '{"portNumber":["8080"],"localPortNumber":["8080"]}'
```

### Applying terraform changes
```sh
cd terraform
terraform apply -var-file=dev.tfvars
```

## Architecture

Source: [`infrastructure-architecture.mmd`](infrastructure-architecture.mmd)

```mermaid
%% source: terraform/main.tf, terraform/dev.tfvars (environment: dev); Findwork edge from application.properties; operator edge from README.md
flowchart LR
    operator(["Operator"])
    findworkApi["Findwork API"]

    subgraph awsAccount["AWS (eu-west-1)"]
        internetGateway["Internet Gateway"]
        ecrRepository["ECR"]
        cloudwatchLogs["CloudWatch Logs"]
        findworkSecret["Secrets Manager"]

        subgraph vpc["VPC"]
            subgraph publicSubnet["Public subnet"]
                appService["ECS service"]
                ecsInstance["EC2 t2.micro"]
            end

            subgraph privateSubnets["Private subnets"]
                rdsPostgres[("RDS PostgreSQL")]
            end
        end
    end

    operator ~~~ appService
    operator -->|"SSM port-forward"| ecsInstance

    appService -->|"runs on"| ecsInstance
    ecrRepository -->|"image"| appService
    findworkSecret -->|"API key"| appService
    ecsInstance -->|"logs"| cloudwatchLogs
    appService -->|"HTTPS"| internetGateway
    internetGateway --> findworkApi
    appService -.->|"not connected yet"| rdsPostgres
```
