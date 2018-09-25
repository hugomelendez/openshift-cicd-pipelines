#!/usr/bin/env groovy

def call(app) {
    def dc = openshift.selector("dc", app)

    // Waits for the deployment to finish   
    def latestDeploymentVersion = openshift.selector("dc", app).object().status.latestVersion
    def rc = openshift.selector("rc", "${app}-${latestDeploymentVersion}")
    
    rc.untilEach(1){
        def rcMap = it.object()

        return (rcMap.status.replicas.equals(rcMap.status.readyReplicas))
    }
}