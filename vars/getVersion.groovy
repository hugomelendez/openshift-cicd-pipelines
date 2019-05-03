#!/usr/bin/env groovy

def call(tech) {
    if (tech.equals("maven"))
        readMavenPom().getVersion()
}