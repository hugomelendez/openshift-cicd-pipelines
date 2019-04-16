#! /usr/bin/env bash

oc new-project dev
oc new-project test
oc new-project prod

oc new-app --template=jenkins-ephemeral --name=jenkins -n dev

oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n test
oc adm policy add-role-to-user edit system:serviceaccount:dev:jenkins -n prod

oc new-app -f src/main/openshift/template.yaml -n dev -p APP_NAME=openshift-hello-world -p GIT_REPO=https://github.com/redhatcsargentina/openshift-pipelines.git -p GIT_BRANCH=master

oc create secret generic app-repository-credentials --from-literal=username=leandroberetta --from-file=password=$PASSWORD --type=kubernetes.io/basic-auth -n dev
oc create secret generic pipeline-library-repository-credentials --from-literal=username=leandroberetta --from-literal=password=$PASSWORD --type=kubernetes.io/basic-auth -n dev

oc label secret app-repository-credentials credential.sync.jenkins.openshift.io=true -n dev
oc label secret pipeline-library-repository-credentials credential.sync.jenkins.openshift.io=true -n dev

oc start-build openshift-hello-world-pipeline -n dev
