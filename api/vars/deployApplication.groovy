#!/usr/bin/env groovy

def call(app, image, tag) {
    if (!openshift.selector("dc", app).exists()) {
        // The creation starts a deployment
        createApplication(app, image, tag)                 
    } else {
        updateApplication(app, image, tag)
    }   

    //verifyDeployment(app)
}