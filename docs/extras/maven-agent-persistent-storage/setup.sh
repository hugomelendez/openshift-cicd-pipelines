#! /usr/bin/env bash

oc project cicd

echo "apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: maven
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 100Gi" | oc create -f - -n cicd

oc create -f ./jenkins/maven-agent.yaml -n cicd