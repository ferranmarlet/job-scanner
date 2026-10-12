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
