variable "aws_region" {
  description = "AWS region to deploy into"
  type        = string
  default     = "eu-west-1"
}

variable "project_name" {
  description = "Project name, used for naming and tagging resources"
  type        = string
  default     = "jobscan"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "dev"
}

variable "db_name" {
  description = "Name of the initial database"
  type        = string
  default     = "jobscan"
}

variable "db_username" {
  description = "Master username for RDS"
  type        = string
  default     = "postgres"
}

variable "db_app_username" {
  description = "Application DB user that authenticates via IAM (must be granted rds_iam)"
  type        = string
  default     = "jobscan_app"
}

variable "container_image" {
  description = "Docker image URI (e.g. from ECR or Docker Hub)"
  default     = "your-dockerhub-user/kotlin-app:latest"
}

variable "container_port" {
  description = "Port server listens on"
  default     = 8080
}
