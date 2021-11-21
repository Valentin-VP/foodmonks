#!/bin/bash
docker build -t foodmonks-prod-backend .
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-backend
docker tag foodmonks-prod-backend:latest 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-backend:$1
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-backend:$1
