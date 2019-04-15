#! /usr/bin/env bash

oc create -f templates/java-pipeline.yaml -n openshift

oc new-project hello-world-dev
oc new-project hello-world-test
oc new-project hello-world-prod

oc new-app --template=jenkins-ephemeral --name=jenkins -n hello-world-dev

oc adm policy add-role-to-user edit system:serviceaccount:hello-world-dev:jenkins -n hello-world-test
oc adm policy add-role-to-user edit system:serviceaccount:hello-world-dev:jenkins -n hello-world-prod

oc new-app --template java-pipeline -n hello-world-dev \
  -p PARAM_GIT_REPO=https://github.com/leandroberetta/openshift-hello-world.git \
  -p PARAM_GIT_BRANCH=master \
  -p PARAM_APPLICATION=hello-world 
