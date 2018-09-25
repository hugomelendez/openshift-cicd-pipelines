#!/usr/bin/env groovy

def call(app, image, tag) {
    openshift.set("triggers", "dc/${app}", "--remove-all")
    openshift.set("triggers", "dc/${app}", "--from-image=${image}:${tag}", "-c ${app}")
}