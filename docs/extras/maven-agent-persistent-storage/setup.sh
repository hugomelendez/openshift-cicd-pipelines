#! /usr/bin/env bash

oc project jenkins

echo "apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: maven
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 100Gi" | oc create -f - -n jenkins

oc create -f ./maven-agent.yaml -n jenkins