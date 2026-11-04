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
