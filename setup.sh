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

# Creates the prod template in the prod projects
oc create -f ./environments/prod/prod-application-template.yaml -n hello-prod-management

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
oc create -f ./configuration/roles/group-reader.yaml

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

# Creates the skopeo image

# Imports the jenkins slave base image
oc import-image jenkins-slave-base-rhel7 --confirm --from=registry.access.redhat.com/openshift3/jenkins-slave-base-rhel7 -n openshift

# Creates an image stream to hold the skopeo image
oc create is skopeo -n openshift

# Creates the build config
oc create -f ./configuration/jenkins/agents/skopeo/skopeo-bc.yaml -n openshift

# Creates a new build
oc start-build skopeo -n openshift --wait

# Tags the skopeo image in the jenkins project 
oc tag openshift/skopeo:latest skopeo:latest -n jenkins

# Adds a label for the sync plugin to push the image into the jenkins instance
oc label is skopeo role=jenkins-slave -n jenkins

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
