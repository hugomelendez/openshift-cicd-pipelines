# Introduction

This repository contains an implementation CI/CD in OpenShift. 

The usual CI/CD practice includes the use of:

* One or more OpenShift clusters (NON-PROD, PROD, etc)
* One or more Jenkins instance (depending the secutity policies)
* One or more pipelines (pipelines per environment, cross pipelines, etc)

This implementation is modularized and can be deployed covering the mentioned configurations.

The pipelines use the declarative approach and the [OpenShift Jenkins Pipeline Plugin](https://github.com/openshift/jenkins-client-plugin).
