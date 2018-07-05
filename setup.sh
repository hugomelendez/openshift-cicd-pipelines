#! /usr/bin/env bash

CLUSTER_URL=https://192.168.64.39:8443

# Logs in as admin to create projects
oc login $CLUSTER_URL -p admin -p admin

# Creates projects for development (n teams, n projects)
oc new-project dev1
oc new-project dev2
# Creates the test project
oc new-project test
# Creates the prod project
oc new-project prod

# Creates the development templates
oc create -f ./dev/dev-pipelines-template.yaml -n openshift
oc create -f ./dev/branch-pipeline-template.yaml -n openshift

# Creates the test template
oc create -f ./test/test-pipeline-template.yaml -n test

# Creates a new cluster role for reading groups in the cluster
oc create -f group-reader.yaml

# Creates new groups
oc adm groups new developers
oc adm groups new testers
oc adm groups new administrators
oc adm groups new test-approvers
oc adm groups new prod-approvers

# Adds users to groups
oc adm groups add-users developers developer1 developer2 developer3 developer4
oc adm groups add-users testers tester1 tester2
oc adm groups add-users administrators administrator1
oc adm groups add-users test-approvers test-approver1 test-approver2
oc adm groups add-users prod-approvers prod-approver1

