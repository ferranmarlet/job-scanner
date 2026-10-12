# database
resource "aws_rds_cluster" "jobscan_dev" {
  cluster_identifier      = "jobscan-dev"
  engine                  = "aurora-postgresql"
  engine_mode             = "provisioned"
  database_name           = "jobscan"
  master_username         = "postgres"
  master_password         = var.db_password
  skip_final_snapshot     = true

  serverlessv2_scaling_configuration {
    min_capacity = 0
    max_capacity = 4
  }
}

resource "aws_rds_cluster_instance" "jobscan_dev_instance" {
  identifier         = "jobscan-dev-instance-1"
  cluster_identifier = aws_rds_cluster.jobscan_dev.id
  instance_class     = "db.serverless"
  engine             = aws_rds_cluster.jobscan_dev.engine
  engine_version     = aws_rds_cluster.jobscan_dev.engine_version
}
