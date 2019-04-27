#!/usr/bin/env groovy

def call(parameters) {
    openshift.withCluster(parameters.clusterUrl, parameters.credentialsId) {
        openshift.withProject(parameters.project) {
            openshift.selector("bc", parameters.application).startBuild("--from-dir=${parameters.artifactsDir}", "--wait=true")
        }
    }
}