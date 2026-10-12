output "rds_endpoint" {
  description = "RDS instance endpoint"
  value       = aws_db_instance.main.endpoint
}

output "rds_port" {
  description = "RDS instance port"
  value       = aws_db_instance.main.port
}

output "rds_db_name" {
  description = "Database name"
  value       = aws_db_instance.main.db_name
}

output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "app_security_group_id" {
  description = "Attach this SG to your app (EC2, Lambda, ECS) to allow DB access"
  value       = aws_security_group.app.id
}

output "db_secret_arn" {
  description = "ARN of the Secrets Manager secret containing DB credentials"
  value       = aws_secretsmanager_secret.db_password.arn
}
