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

output "ecs_cluster_name" {
  value = aws_ecs_cluster.main.name
}

output "ecr_repository_url" {
  description = "ECR repository to push the application image to"
  value       = aws_ecr_repository.app.repository_url
}

output "asg_name" {
  description = "Auto Scaling group name, used to find the instance for SSM port forwarding"
  value       = aws_autoscaling_group.ecs_asg.name
}
