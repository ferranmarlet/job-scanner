terraform {
  required_version = ">= 1.10"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }

  # Remote state: uncomment and fill in once the state bucket exists.
  # backend "s3" {
  #   bucket       = "<state-bucket>"
  #   key          = "jobscan/terraform.tfstate"
  #   region       = "eu-west-1"
  #   use_lockfile = true
  # }
}
