#!/bin/bash
docker build -t foodmonks-prod-frontend .
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-frontend
docker tag foodmonks-prod-frontend:latest 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-frontend:$1
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-prod-frontend:$1
