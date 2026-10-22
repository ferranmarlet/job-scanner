#!/usr/bin/env bash
set -euo pipefail

REGION="eu-west-1"
CLUSTER="jobscan-cluster"
SERVICE="jobscan-dev-service"

aws ecs update-service --region "$REGION" --cluster "$CLUSTER" --service "$SERVICE" \
  --force-new-deployment > /dev/null

echo "Waiting for $SERVICE to become stable..."
aws ecs wait services-stable --region "$REGION" --cluster "$CLUSTER" --services "$SERVICE"
echo "Deployment finished."
