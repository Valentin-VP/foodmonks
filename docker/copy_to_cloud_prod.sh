#!/bin/bash
docker save -o backend.tar 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-backend:latest
docker save -o frontend.tar 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-frontend:latest
scp backend.tar frontend.tar aws-docker:/home/ubuntu/docker_images
