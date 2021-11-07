#!/bin/bash
docker build -t foodmonks-dev-frontend .
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-frontend
docker tag foodmonks-dev-frontend:latest 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-frontend:$1
docker push 711621010839.dkr.ecr.us-east-2.amazonaws.com/foodmonks-dev-frontend:$1
