#!/usr/bin/env groovy

def call(app, artifactsDir) {
    // If artifacts dir is set, binary s2i build is used
    if (artifactsDir)
        openshift.selector("bc", app).startBuild("--from-dir=${artifactsDir}", "--wait=true")
    else 
        openshift.selector("bc", app).startBuild("--wait=true")       
}

