#!/usr/bin/env groovy

def call(app, image, tag) {
    // Creates the application and get the brand new BuildConfig
    def dc = openshift.newApp("${image}:${tag}", "--name=${app}").narrow("dc");
    // Creates the app Route
    openshift.selector("svc", app).expose();                
}