#!/usr/bin/env groovy

def call(parameters) {
    env.GIT_COMMIT = checkout(scm).GIT_COMMIT
}