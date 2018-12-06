#! /usr/bin/env bash

# Sets the MiniShift profile for the non-prod cluster
minishift profile set non-prod

# Starts the non-prod cluster
minishift start

# Logs in as admin to create projects
oc login "https://$(minishift ip):8443" -u admin -p admin

# Creates projects for development (n areas, n projects)
oc new-project hello-dev

# Creates the test projects (n areas, n projects)
oc new-project hello-test

# Creates the prod (management) projects (n areas, n projects)
oc new-project hello-prod-management

# Creates the development templates in the development projects
oc create -f ./environments/dev/java/java-app-pipelines-template.yaml -n hello-dev
oc create -f ./environments/dev/java/java-app-pipeline-branch-template.yaml -n hello-dev

# Creates the test template in the test projects
oc create -f ./environments/test/test-application-template.yaml -n hello-test
oc create -f ./environments/test/config/change-config-test-template.yaml -n hello-test

# Creates the prod template in the prod projects
oc create -f ./environments/prod/prod-application-template.yaml -n hello-prod-management
oc create -f ./environments/prod/config/change-config-prod-template.yaml -n hello-prod

# Jenkins

# Creates the Jenkins project
oc new-project jenkins

# Deploys Jenkins
oc new-app jenkins-persistent -n jenkins

# Sets Jenkins service account permissions
oc adm policy add-role-to-user admin system:serviceaccount:jenkins:jenkins -n hello-dev
oc adm policy add-role-to-user admin system:serviceaccount:jenkins:jenkins -n hello-test
oc adm policy add-role-to-user admin system:serviceaccount:jenkins:jenkins -n hello-prod-management
oc adm policy add-cluster-role-to-user system:registry system:serviceaccount:jenkins:jenkins
oc adm policy add-cluster-role-to-user system:image-builder system:serviceaccount:jenkins:jenkins

# Creates a new cluster role for reading groups in the cluster
"apiVersion: v1
kind: ClusterRole
metadata:
  name: group-reader
rules:
- apiGroups:
  - user.openshift.io
  resources:
  - groups
  - identities
  - useridentitymappings
  - users
  verbs:
  - get
  - list
  - watch" | oc create -f -

# Allows jenkins service account to read groups from cluster
oc adm policy add-cluster-role-to-user group-reader system:serviceaccount:jenkins:jenkins

# Avoids a Jenkins instance in every project a pipeline is created
minishift openshift config set --patch '{"jenkinsPipelineConfig":{"autoProvisionEnabled":false}}'

# Creates new groups
oc adm groups new developers
oc adm groups new testers
oc adm groups new administrators
oc adm groups new test-approvers
oc adm groups new prod-approvers

# Adds users to groups
oc adm groups add-users developers leandro carlos hugo ana
oc adm groups add-users testers maria diego
oc adm groups add-users administrators mario
oc adm groups add-users test-approvers jose mario
oc adm groups add-users prod-approvers hernan mario

# Sets permissions

# Project memberships
oc adm policy add-role-to-group admin developers -n hello-dev
oc adm policy add-role-to-group view developers -n jenkins
oc adm policy add-role-to-group view developers -n hello-test
oc adm policy add-role-to-group view testers -n hello-test
oc adm policy add-role-to-group admin administrators -n hello-dev
oc adm policy add-role-to-group admin administrators -n hello-test
oc adm policy add-role-to-group admin administrators -n hello-prod-management
oc adm policy add-role-to-group admin administrators -n jenkins
oc adm policy add-role-to-group edit prod-approvers -n jenkins
oc adm policy add-role-to-group edit test-approvers -n jenkins

# Exposes the non-prod cluster registry
minishift addons apply registry-route

# Gathers information for Jenkins

export SRC_REGISTRY_URL=$(oc get route docker-registry -n default --template={{.spec.host}})
export SRC_REGISTRY_TOKEN=$(oc sa get-token jenkins -n jenkins)

# Sets the MiniShift profile for the prod cluster
minishift profile set prod

# Starts the prod cluster
minishift start

# Logs in as admin to create projects
oc login https://$(minishift ip):8443 -u admin -p admin

# Creates the prod projects
oc new-project hello-prod

# Creates the project where the admin sa will be
oc new-project prod-management

# Creates an admin service account for deployments, etc
oc create sa admin -n prod-management

# Sets permissions
oc adm policy add-role-to-user admin system:serviceaccount:prod-management:admin -n hello-prod
oc adm policy add-cluster-role-to-user system:registry system:serviceaccount:prod-management:admin
oc adm policy add-cluster-role-to-user system:image-builder system:serviceaccount:prod-management:admin

# Exposes the prod cluster registry
minishift addons apply registry-route

# Gathers information for Jenkins

export DST_CLUSTER_URL="insecure://$(minishift ip):8443"
export DST_CLUSTER_TOKEN=$(oc sa get-token admin -n prod-management)

export DST_REGISTRY_URL=$(oc get route docker-registry -n default --template={{.spec.host}})
export DST_REGISTRY_TOKEN=$(oc sa get-token admin -n prod-management)

export PIPELINE_LIBRARY_REPOSITORY="https://github.com/redhatcsargentina/openshift-pipeline-library.git"

export CREDENTIALS="jenkins-repository-credentials"

export SRC_REGISTRY_CREDENTIALS="jenkins-src-registry-credentials"
export DST_REGISTRY_CREDENTIALS="jenkins-dst-registry-credentials"

# Jenkins configuration

# Changes to the MiniShift profile for the non-prod cluster
minishift profile set non-prod

# Logs in as admin
oc login "https://$(minishift ip):8443" -u admin -p admin

# Pauses deployments for Jenkins
oc rollout pause dc/jenkins -n jenkins

oc set env dc/jenkins SRC_REGISTRY_URL=${SRC_REGISTRY_URL} -n jenkins
oc set env dc/jenkins DST_REGISTRY_URL=${DST_REGISTRY_URL} -n jenkins
oc set env dc/jenkins DST_CLUSTER_URL=${DST_CLUSTER_URL} -n jenkins
oc set env dc/jenkins DST_CLUSTER_TOKEN=${DST_CLUSTER_TOKEN} -n jenkins
oc set env dc/jenkins PIPELINE_LIBRARY_REPOSITORY=${PIPELINE_LIBRARY_REPOSITORY} -n jenkins
oc set env dc/jenkins CREDENTIALS=${CREDENTIALS} -n jenkins
oc set env dc/jenkins SRC_REGISTRY_CREDENTIALS=${SRC_REGISTRY_CREDENTIALS} -n jenkins
oc set env dc/jenkins DST_REGISTRY_CREDENTIALS=${DST_REGISTRY_CREDENTIALS} -n jenkins

oc create secret generic src-registry-credentials --from-literal=username=unused --from-literal=password=${SRC_REGISTRY_TOKEN} --type=kubernetes.io/basic-auth -n jenkins
oc create secret generic dst-registry-credentials --from-literal=username=unused --from-literal=password=${DST_REGISTRY_TOKEN} --type=kubernetes.io/basic-auth -n jenkins

# The CREDENTIALS environment variable needs to be defined with a valid password for cloning the repositories
oc create secret generic repository-credentials --from-literal=username=unused --from-literal=password=${CREDENTIALS} --type=kubernetes.io/basic-auth -n jenkins

# Resumes deployments for Jenkins
oc rollout resume dc/jenkins -n jenkins
