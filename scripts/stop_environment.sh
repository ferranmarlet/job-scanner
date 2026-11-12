#!/usr/bin/env bash
# Stops the dev environment's billable compute: the ECS task, the EC2 instance it runs on and the
# RDS instance. Start it again with scripts/start_environment.sh.
#
# Still billed while stopped: RDS storage and backups, the backoffice's Elastic IP, ECR images and logs.
# AWS starts a stopped RDS instance again after 7 days; run this script again to stop it.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

REGION="eu-west-1"
CLUSTER="jobscan-cluster"
SERVICE="jobscan-dev-service"
DB_INSTANCE="jobscan-dev"
ASG=$(terraform -chdir=terraform output -raw asg_name)

# Polls a command until it prints the expected value, up to 20 minutes
wait_for() {
  local expected=$1 description=$2
  shift 2
  for _ in $(seq 1 80); do
    [[ "$("$@")" == "$expected" ]] && return 0
    sleep 15
  done
  echo "Timed out waiting for $description." >&2
  return 1
}

instance_count() {
  aws autoscaling describe-auto-scaling-groups --region "$REGION" --auto-scaling-group-names "$ASG" \
    --query 'length(AutoScalingGroups[0].Instances)' --output text
}

db_status() {
  aws rds describe-db-instances --region "$REGION" --db-instance-identifier "$DB_INSTANCE" \
    --query 'DBInstances[0].DBInstanceStatus' --output text
}

# The task goes first, so it shuts down cleanly before its instance is terminated
echo "Stopping the ECS service..."
aws ecs update-service --region "$REGION" --cluster "$CLUSTER" --service "$SERVICE" \
  --desired-count 0 > /dev/null
aws ecs wait services-stable --region "$REGION" --cluster "$CLUSTER" --services "$SERVICE"

echo "Terminating the EC2 instance..."
aws autoscaling update-auto-scaling-group --region "$REGION" --auto-scaling-group-name "$ASG" \
  --min-size 0 --desired-capacity 0
wait_for 0 "the instance to terminate" instance_count

case "$(db_status)" in
  available)
    echo "Stopping the RDS instance..."
    aws rds stop-db-instance --region "$REGION" --db-instance-identifier "$DB_INSTANCE" > /dev/null
    wait_for stopped "the RDS instance to stop" db_status
    ;;
  stopped | stopping)
    echo "The RDS instance is already stopped."
    ;;
  *)
    echo "The RDS instance is $(db_status), so it can't be stopped now. Run this script again later." >&2
    exit 1
    ;;
esac

echo "Environment stopped."
