#! /usr/bin/env bash

oc new-project hello-dev
oc new-project hello-test
oc new-project hello-prod

oc new-project jenkins

oc create secret generic repository-credentials --from-file=ssh-privatekey=$HOME/.ssh/id_rsa --type=kubernetes.io/ssh-auth -n hello-dev
oc label secret repository-credentials credential.sync.jenkins.openshift.io=true -n hello-dev
oc annotate secret repository-credentials 'build.openshift.io/source-secret-match-uri-1=ssh://github.com/*' -n hello-dev

oc create secret generic repository-credentials --from-file=ssh-privatekey=$HOME/.ssh/id_rsa --type=kubernetes.io/ssh-auth -n jenkins
oc label secret repository-credentials credential.sync.jenkins.openshift.io=true -n jenkins

oc new-build jenkins:2 --binary --name custom-jenkins -n jenkins
oc start-build custom-jenkins --from-dir=./jenkins --wait -n jenkins
oc new-app --template=jenkins-ephemeral --name=jenkins -p JENKINS_IMAGE_STREAM_TAG=custom-jenkins:latest -p NAMESPACE=jenkins -n jenkins

oc adm policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n hello-dev
oc adm policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n hello-test
oc adm policy add-role-to-user edit system:serviceaccount:jenkins:jenkins -n hello-prod

oc create -f ./templates/ci-pipeline.yaml -n hello-dev
oc new-app --template ci-pipeline -p APP_NAME=hello-service-ci -p GIT_REPO=ssh://git@github.com/redhatcsargentina/openshift-hello-service.git -p GIT_BRANCH=master -n hello-dev

oc create -f ./templates/cd-pipeline.yaml -n hello-dev
oc new-app --template cd-pipeline -p APP_NAME=hello-service -p GIT_REPO=ssh://git@github.com/redhatcsargentina/openshift-hello-service.git -n hello-dev