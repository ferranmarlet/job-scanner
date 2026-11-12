#!/usr/bin/env bash
# Starts the dev environment stopped by scripts/stop_environment.sh: the RDS instance, the EC2
# instance and the ECS task. Then it moves the backoffice's Elastic IP to the new instance,
# which is how CloudFront reaches the API.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

REGION="eu-west-1"
CLUSTER="jobscan-cluster"
SERVICE="jobscan-dev-service"
DB_INSTANCE="jobscan-dev"
BACKOFFICE_DIR="${BACKOFFICE_DIR:-../jobscan-backoffice}"
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

db_status() {
  aws rds describe-db-instances --region "$REGION" --db-instance-identifier "$DB_INSTANCE" \
    --query 'DBInstances[0].DBInstanceStatus' --output text
}

registered_instances() {
  aws ecs describe-clusters --region "$REGION" --clusters "$CLUSTER" \
    --query 'clusters[0].registeredContainerInstancesCount' --output text
}

# The database goes first, because the app fails to start without it
if [[ "$(db_status)" == "stopped" ]]; then
  echo "Starting the RDS instance..."
  aws rds start-db-instance --region "$REGION" --db-instance-identifier "$DB_INSTANCE" > /dev/null
fi
echo "Waiting for the RDS instance to be available..."
aws rds wait db-instance-available --region "$REGION" --db-instance-identifier "$DB_INSTANCE"

echo "Starting the EC2 instance..."
aws autoscaling update-auto-scaling-group --region "$REGION" --auto-scaling-group-name "$ASG" \
  --min-size 1 --desired-capacity 1
wait_for 1 "the instance to join the ECS cluster" registered_instances

echo "Starting the ECS service..."
aws ecs update-service --region "$REGION" --cluster "$CLUSTER" --service "$SERVICE" \
  --desired-count 1 > /dev/null
aws ecs wait services-stable --region "$REGION" --cluster "$CLUSTER" --services "$SERVICE"

# Only the association is applied, so other pending backoffice changes aren't deployed by accident
echo "Moving the Elastic IP to the new instance..."
terraform -chdir="$BACKOFFICE_DIR/terraform" apply -target=aws_eip_association.api -auto-approve > /dev/null

echo "Environment started: $(terraform -chdir="$BACKOFFICE_DIR/terraform" output -raw url)"
