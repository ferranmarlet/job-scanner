# jobscan

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

### To launch locally

```sh
docker build -t jobscan .
docker run --rm -p 8080:8080 --env-file .env.properties jobscan
```
The API is then available at http://localhost:8080. Press Ctrl+C to stop the container, and --rm removes it on exit.

## Architecture

Source: [`infrastructure-architecture.mmd`](infrastructure-architecture.mmd)

```mermaid
%% source: terraform/main.tf, terraform/variables.tf, terraform/dev.tfvars (environment: dev, working tree)
%% source: runtime edges to Findwork from src/main/resources/application.properties; operator edge from README.md; deployment workflow (scripts/*.sh) left out on purpose
flowchart LR
    operator(["Operator<br/>AWS CLI"])
    findworkApi["Findwork API<br/>findwork.dev/api"]

    subgraph awsAccount["AWS account, eu-west-1 (dev)"]
        internetGateway["Internet Gateway<br/>only route to 0.0.0.0/0, public subnet only"]

        subgraph vpc["VPC 10.0.0.0/16"]
            subgraph publicSubnet["Public subnet 10.0.10.0/24 (eu-west-1a)"]
                appService["ECS service jobscan-dev-service<br/>cluster jobscan-cluster, EC2 launch type, desired 1<br/>task: 512 CPU, 768 MiB, bridge mode, port 8080"]
                ecsInstance["EC2 container instance<br/>t2.micro, ECS-optimized AL2023, public IP<br/>no inbound SG rules, all outbound<br/>ASG min 0 / desired 0 / max 1"]
            end

            subgraph privateSubnets["Private subnets 10.0.1.0/24 + 10.0.2.0/24 (eu-west-1a, b)"]
                rdsPostgres[("RDS PostgreSQL 16, database jobscan<br/>db.t3.micro, 20 GB gp2, single-AZ, not public<br/>encrypted with aws/rds key, IAM auth on<br/>7-day backups, no deletion protection")]
            end
        end

        subgraph regionalServices["Regional services"]
            ecrRepository["ECR jobscan-dev<br/>mutable tags, scan on push<br/>keeps the 5 most recent images"]
            cloudwatchLogs["CloudWatch Logs<br/>/ecs/jobscan-dev, 7-day retention"]
            findworkSecret["Secrets Manager<br/>Findwork API key<br/>created outside Terraform, ARN from tfvars"]
        end
    end

    operator ~~~ appService
    operator -->|"port-forwards 8080 via SSM session"| ecsInstance

    appService -->|"runs task on"| ecsInstance
    ecrRepository -->|"supplies image to"| appService
    findworkSecret -->|"injects FINDWORK_API_KEY at startup into"| appService
    ecsInstance -->|"ships task logs via awslogs to"| cloudwatchLogs
    appService -->|"sends outbound HTTPS through"| internetGateway
    internetGateway -->|"reaches"| findworkApi
    appService -.->|"allowed tcp/5432 and rds-db:connect as jobscan_app<br/>app has no database client yet"| rdsPostgres
```
