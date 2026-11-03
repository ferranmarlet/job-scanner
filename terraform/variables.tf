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
  description = "Deployment environment (e.g. dev, prod)"
  type        = string
}
