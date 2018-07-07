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

# Sets permissions

# Allows jenkins service account from test to edit development projects
oc adm policy add-role-to-user edit system:serviceaccount:test:jenkins -n dev1
oc adm policy add-role-to-user edit system:serviceaccount:test:jenkins -n dev2

# Allows jenkins service account to read groups from cluster
oc adm policy add-cluster-role-to-user group-reader system:serviceaccount:test:jenkins

# Project memberships
oc adm policy add-role-to-user edit developer1 -n dev1
oc adm policy add-role-to-user edit developer2 -n dev1

oc adm policy add-role-to-user edit developer3 -n dev2
oc adm policy add-role-to-user edit developer4 -n dev2

oc adm policy add-role-to-group admin administrator -n dev1
oc adm policy add-role-to-group admin administrator -n dev2
oc adm policy add-role-to-group admin administrator -n test
oc adm policy add-role-to-group admin administrator -n prod

oc adm policy add-role-to-group view test-approvers -n test
oc adm policy add-role-to-group view prod-approvers -n prod






