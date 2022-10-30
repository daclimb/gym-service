#!/usr/bin/env bash

echo "Init localstack"
echo "Creating s3"
awslocal s3 mb s3://image