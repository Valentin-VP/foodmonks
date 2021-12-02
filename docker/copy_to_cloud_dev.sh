#!/bin/bash
# docker save -o backend.tar 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-backend:latest
# docker save -o frontend.tar 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-frontend:latest
# scp backend.tar frontend.tar aws-dev:/home/ubuntu/docker_images
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 711621010839.dkr.ecr.us-east-2.amazonaws.com
docker compose -f docker-compose.aws.dev.yml build --build-arg "BACKEND_DOCKER_IMAGE_TAG=${1:-latest} FRONTEND_DOCKER_IMAGE_TAG=${1:-latest}" --no-cache
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-backend:${1:-latest}
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-frontend:${1:-latest}
docker save -o backend.tar 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-backend:${1:-latest}
docker save -o frontend.tar 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-frontend:${1:-latest}
scp backend.tar frontend.tar aws-dev:/home/ubuntu/docker_images