#!/usr/bin/env groovy

def call(repo, branch, secret) {
    def gitInfo = [:]

    gitInfo['url'] = repo

    if (env.GIT_SECRET && !secret.equals("none"))
        gitInfo['credentialsId'] = secret

    return gitInfo
}