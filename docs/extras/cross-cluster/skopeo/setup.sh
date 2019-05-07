#! /usr/bin/env bash

oc import-image jenkins-slave-base-rhel7 --confirm --from=registry.access.redhat.com/openshift3/jenkins-slave-base-rhel7 -n openshift

oc create is skopeo -n openshift

oc create -f ./skopeo-bc.yaml -n openshift

oc start-build skopeo -n openshift --wait

oc tag openshift/skopeo:latest skopeo:latest -n jenkins

oc label is skopeo role=jenkins-slave -n jenkins
